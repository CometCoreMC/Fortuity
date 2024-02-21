package net.cometcore.fortuity.event;

import net.cometcore.fortuity.Fortuity;
import net.cometcore.fortuity.event.events.*;
import net.cometcore.fortuity.utils.FileManager;
import net.cometcore.fortuity.worlds.game.GameWorld;
import net.cometcore.fortuity.worlds.game.GameWorldGenerator;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class GameEventHandler {

    public void onStart(StartGameEvent event) {

        GameWorld world = event.game.fortuityWorld;

        world.generateWorld(event.getPlayingPlayers().size());

        List<Location> spawnLocations = Collections.emptyList();
        if(world != null) {
            spawnLocations = event.game.fortuityWorld.getSpawnLocations();
        }

        // Teleport Players
        if (!spawnLocations.isEmpty()) {
            List<Player> players = event.getPlayingPlayers();
            assert players.size() <= spawnLocations.size();
            for(int i = 0; i<players.size(); i++){
                // Set GameModes
                players.get(i).setGameMode(GameMode.SURVIVAL);
                Location spawnLocation = spawnLocations.stream().toList().get(i).add(0.5, 1, 0.5);
                players.get(i).setHealth(players.get(i).getHealthScale());
                players.get(i).teleport(spawnLocation);
                StartGameEvent.freezePlayer(players.get(i));
            }
        }

        // CountDown
        StartGameEvent.StartingCounter gameLoop = event.StartingCounter(5, event);
        gameLoop.runTaskTimerAsynchronously(Fortuity.getInstance(), 20, 20);

        // Create Listeners
        Fortuity.getInstance().getServer().getPluginManager().registerEvents(event, Fortuity.getInstance());
    }

    public void onRun(RunGameEvent event) {

        // Create Random Item Task
        BossBar bossBar = BossBar.bossBar(Component.text("Time Until Next Item"), 1.0F, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);

        RunGameEvent.RandomItemLoop gameLoop = event.RandomItemLoop(30, bossBar, event);
        gameLoop.runTaskTimerAsynchronously(Fortuity.getInstance(), 0, 1);

        // Create Listeners
        Fortuity.getInstance().getServer().getPluginManager().registerEvents(event, Fortuity.getInstance());
    }

    public void onEnd(EndGameEvent event, RunGameEvent before) {
        before.cancel();
        onEnd(event);
    }
    public void onEnd(EndGameEvent event) {

        World defaultWorld = Bukkit.getWorld("world");

        // Teleport all players out
        for(Player player : event.getPlayingPlayers()){
            player.setGameMode(GameMode.ADVENTURE);
            // Clean Up
            player.getInventory().clear();
            player.setHealth(player.getHealthScale());

            // Teleport players
            if(defaultWorld != null)
                player.teleport(defaultWorld.getSpawnLocation());
        }

        event.game.fortuityWorld.reset();

        World world = Bukkit.getWorld(GameWorld.name);
        if (world != null) {
            Bukkit.getServer().unloadWorld(world, false);

            world.getWorldFolder().delete();

            event.game.fortuityWorld = new GameWorld(World.Environment.NORMAL);
        }

        // Call Reset
        event.game.invokeResetEvent();
    }

    public void onPause(PauseGameEvent event) {
        // TODO
    }
    public void onResume(ResumeGameEvent event) {
        // TODO
    }
}
