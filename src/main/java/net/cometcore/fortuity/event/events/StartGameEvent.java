package net.cometcore.fortuity.event.events;

import net.cometcore.fortuity.event.GameEvent;
import net.cometcore.fortuity.event.generic.GenericStateGameEvent;
import net.cometcore.fortuity.game.Game;
import net.cometcore.fortuity.team.Team;
import net.cometcore.fortuity.team.TeamState;
import net.cometcore.fortuity.utils.SenderHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class StartGameEvent extends GameEvent implements GenericStateGameEvent, Listener {
    public final List<Team> teams;
    public final List<Player> players;
    public final List<Player> frozenPlayers;
    public final Game game;

    public StartGameEvent(Game game, List<Team> teams, List<Player> players) {
        this.teams = teams;
        this.players = players;
        this.frozenPlayers = new ArrayList<>(players);
        this.game = game;
    }

    public StartingCounter StartingCounter(int countdown, StartGameEvent event) {
         return new StartingCounter(countdown, event);
    }

    @Override
    public void cancel() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot cancel a start game event");
    }

    public class StartingCounter extends BukkitRunnable {

        private final StartGameEvent event;
        private final int countdown;
        private int counter;

        private StartingCounter(int countdown, StartGameEvent event) {
            this.countdown = countdown;
            this.event = event;
            this.counter = countdown + 1;
        }

        @Override
        public void run() {

            if(counter >= (countdown + 1)){
                // Display splash text to all players
                broadcastSplashText("Welcome to Fortuity!");
            }
            else if(counter > 0){
                // Broadcast countdown message to all players
                broadcastSplashText("Game Starting in " + counter);
            }
            else {
                cancel();

                unregisterEvents();

                for(Player player : players)
                    StartGameEvent.unfreezePlayer(player);

                // Start the game
                event.game.invokeRunEvent(event);
            }
            // Decrement countdown
            counter--;
        }

        private void broadcastSplashText(String message) {
            // Iterate over all players and display splash text
            Title title = Title.title(
                    Component.text(message).color(NamedTextColor.YELLOW),
                    Component.empty(),
                    Title.Times.times(Duration.ZERO, Duration.ofMillis(750), Duration.ofMillis(250)));

            for (Player player : players) {
                player.showTitle(title);
            }
        }

    }

    private void unregisterEvents(){
        HandlerList.unregisterAll(this);
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

    @EventHandler
    public void onFly(PlayerToggleFlightEvent event){
        Player player = event.getPlayer();
        event.setCancelled(true);
        player.teleport(player.getLocation());
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
    }

    public static void freezePlayer(Player player){
        player.setAllowFlight(true);
        player.teleport(player.getLocation().add(0,0.1,0));
        player.setFlying(true);
        player.setFlySpeed(0);
    }

    public static void unfreezePlayer(Player player){
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFlySpeed(0.1F);
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
}
