package net.cometcore.fortuity.event.events;

import net.cometcore.fortuity.Fortuity;
import net.cometcore.fortuity.event.GameEvent;
import net.cometcore.fortuity.event.generic.GenericStateGameEvent;
import net.cometcore.fortuity.game.Game;
import net.cometcore.fortuity.team.Team;
import net.cometcore.fortuity.team.TeamState;
import net.cometcore.fortuity.utils.SenderHelper;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class RunGameEvent extends GameEvent implements GenericStateGameEvent, Listener {
    public final List<Team> teams;
    public final List<Player> players;
    public final Game game;

    public RunGameEvent(Game game, List<Team> teams, List<Player> players) {
        this.teams = teams;
        this.players = players;
        this.game = game;

        checkGame();
    }

    private RandomItemLoop task;
    public RandomItemLoop RandomItemLoop(int countdown, BossBar bossBar, RunGameEvent event) {
        task = new RandomItemLoop(countdown, bossBar, event);
        return task;
    }

    @Override
    public void cancel() throws UnsupportedOperationException {
        try{
            task.cancel();
        } catch (Exception ignored){}

        // Remove the BossBar from all online players
        for (Player player : this.players) {
            if(task != null && task.bossBar != null) {
                task.bossBar.removeViewer(player);
            }
        }

        // Delete Task
        task = null;
        Fortuity.getLOGGER().info("Running State Cancelled / Stopped");
    }

    @Override
    public List<Team> getPlayingTeams() {
        return teams;
    }

    @Override
    public List<Player> getPlayingPlayers() {
        return players;
    }

    @Override
    public Game getGame() {
        return game;
    }

    public class RandomItemLoop extends BukkitRunnable {

        private final int countdown;
        private int counter;
        private final RunGameEvent event;
        private final BossBar bossBar;

        private RandomItemLoop(int countdown, BossBar bossBar, RunGameEvent event) {
            this.bossBar = bossBar;
            this.countdown = countdown;
            this.event = event;
            this.counter = countdown * 20; // Ticks
            // Show the BossBar to each player individually
            for (Player player : players) {
                bossBar.addViewer(player);
            }
        }

        @Override
        public void run() {
            // Decrement countdown
            counter--;

            // Check if countdown is finished
            if (counter < 0) {
                // Reset countdown and boss bar
                counter = countdown * 20; // Ticks
                bossBar.progress(1.0F); // Reset boss bar progress

                // Give each user a random item
                for (Player player : event.getPlayingPlayers()) {
                    giveRandomItem(player);
                }
            }

            bossBar.progress((float)counter / ((float)countdown * 20));
        }

        private void giveRandomItem(Player player) {
            // Generate a random item
            Random random = new Random();
            Material[] materials = Material.values();
            Material randomMaterial = materials[random.nextInt(materials.length)];

            // Check if the player is online before giving Item
            Player uniquePlayer = player.getPlayer();
            if (uniquePlayer != null && uniquePlayer.isOnline()) {
                // Give the player the random item
                ItemStack itemStack = new ItemStack(randomMaterial);
                uniquePlayer.getInventory().addItem(itemStack);
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event){
        // Get the player who left
        Player player = event.getPlayer();
        if (players.contains(player)) {
            removePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerChangedWorldEvent event){

        // Get the player who left
        Player player = event.getPlayer();
        if (players.contains(player)) {
            removePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){

        // Get the player who died
        Player player = event.getPlayer();
        if (players.contains(player)) {
            eliminatePlayer(player);
        }

        event.setCancelled(true);
    }

    private void removePlayer(Player player){

        players.remove(player);

        for(Team team : teams.stream().filter(entry -> entry.hasPlayer(player)).toList()){
            team.changePlayerState(player, Team.PlayerState.ELIMINATED);
            SenderHelper.sendMessage(players, NamedTextColor.GOLD, "Player " + player.getName() + " Has Been Eliminated!");

            if(team.getPlayersWithState(Team.PlayerState.ALIVE).isEmpty()){
                SenderHelper.sendMessage(players, NamedTextColor.GOLD, "Team " + team.getName() + " Has Been Eliminated!");
                team.changeTeamState(TeamState.ELIMINATED);
            }
        }

        checkGame();
    }

    private void eliminatePlayer(Player player){

        player.setGameMode(GameMode.SPECTATOR);

        for(Team team : teams.stream().filter(entry -> entry.hasPlayer(player)).toList()){
            team.changePlayerState(player, Team.PlayerState.ELIMINATED);
            SenderHelper.sendMessage(players, NamedTextColor.GOLD, "Player " + player.getName() + " Has Been Eliminated!");

            if(team.getPlayersWithState(Team.PlayerState.ALIVE).isEmpty()){
                SenderHelper.sendMessage(players, NamedTextColor.GOLD, "Team " + team.getName() + " Has Been Eliminated!");
                team.changeTeamState(TeamState.ELIMINATED);
            }
        }

        checkGame();
    }

    private void checkGame(){

        List<Team> aliveTeams = teams.stream().filter(entry -> entry.teamState == TeamState.ALIVE).toList();

        if(aliveTeams.size() <= 1){

            cancel();

            if(aliveTeams.size() == 1){
                Team team = aliveTeams.get(0);
                SenderHelper.sendMessage(this.players, NamedTextColor.GREEN, "Congratulations to " + team.getName() + "!");
                SenderHelper.sendMessage(this.players, NamedTextColor.GREEN, "Players: " + team.getPlayers().stream().map(Player::getName).toList());
            }
            SenderHelper.sendMessage(this.players, NamedTextColor.DARK_GREEN, "Thank you For Playing!");

            HandlerList.unregisterAll(this);
            this.game.invokeEndEvent(this);
        }
    }
}
