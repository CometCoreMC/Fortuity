package net.cometcore.fortuity.game;

import net.cometcore.fortuity.event.GameEventHandler;
import net.cometcore.fortuity.event.events.*;
import net.cometcore.fortuity.game.commands.GameCommandManager;
import net.cometcore.fortuity.team.Team;
import net.cometcore.fortuity.team.TeamManager;
import net.cometcore.fortuity.utils.SenderHelper;
import net.cometcore.fortuity.worlds.game.GameWorld;
import net.cometcore.fortuity.worlds.game.GameWorldGenerator;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Plugin plugin;
    private final String name;
    private final GameProperties properties;
    private final GameCommandManager gameCommandManager;
    private final List<GameEventHandler> handlers = new ArrayList<>();

    public GameWorld fortuityWorld;

    private GameState state = GameState.NOT_STARTED;

    /**
     *
     * @param plugin Main plugin
     * @param name Name of the game
     */
    public Game(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.properties = new GameProperties(name);
        this.gameCommandManager = new GameCommandManager(this, properties);

        // Generate Worlds
        fortuityWorld = new GameWorld(World.Environment.NORMAL);
    }

    /**
     * Register a game event handler
     * @param handler Handler to register
     */
    public void registerEventHandler(GameEventHandler handler) {
        handlers.add(handler);
    }

    /**
     * Join the game
     */
    public void join(Player player) {

        if (TeamManager.hasTeam(player)) {
            SenderHelper.sendError(player, "You are already in the game");
            return;
        }

        final Team team = new Team(player.getName(), "", NamedTextColor.WHITE);

        team.addPlayer(player);
        SenderHelper.sendSuccess(player, "You joined the team " + team.getName());
    }

    /**
     * Leave the game
     */
    public void leave(Player player) {

        if (!TeamManager.hasTeam(player)) {
            SenderHelper.sendError(player, "You are not in a team");
            return;
        }

        final Team team = TeamManager.getTeam(player);
        if (team == null) {
            SenderHelper.sendError(player, "Team of the player not found");
            return;
        }
        team.removePlayer(player);
        SenderHelper.sendSuccess(player, "You left the game");
        plugin.getLogger().info("Player  " + player.displayName().examinableName() + " has left the game " + name + "!");

    }

    /**
     * Start the game
     */
    public void start(Player player) {
        if (state != GameState.NOT_STARTED) {
            SenderHelper.sendError(player, "The game has not started.");
        }
        plugin.getLogger().info("Game " + name + " has been started by "+player.getName()+" !");
        startingEvent(player);
    }


    /**
     * Pause the game
     */
    public void pause(Player player) {
        if (state != GameState.RUNNING) {
            SenderHelper.sendError(player, "The game is not running.");
            return;
        }

        plugin.getLogger().info("Game " + name + " has been paused by "+player.getName()+" !");
        pausedEvent(player);
    }

    /**
     * Resume the game
     */
    public void resume(Player player) {
        if (state != GameState.PAUSED) {
            SenderHelper.sendError(player, "The game is not paused.");
            return;
        }

        plugin.getLogger().info("Game " + name + " has been resumed by "+player.getName()+" !");
        resumedEvent(player);
    }


    /**
     * End the game
     */
    public void stop(Player player) {
        if (state != GameState.RUNNING) {
            SenderHelper.sendError(player, "The game is not running.");
            return;
        }

        plugin.getLogger().info("Game " + name + " has been stopped by "+player.getName()+" !");
        stopEvent(player);
    }

    public void invokeResetEvent(){
        resetEvent();
    }
    private void resetEvent(){
        state = GameState.NOT_STARTED;

        // Clear Everything
        TeamManager.unregisterAll();
    }

    private void startingEvent(Player player){
        final var playing = getPlaying(player);
        if (playing == null) {
            return;
        }

        state = GameState.STARTING;

        final var event = new StartGameEvent(this, playing.teams, playing.players);
        handlers.forEach(handler -> handler.onStart(event));
    }

    RunGameEvent gameEvent;
    public void invokeRunEvent(StartGameEvent event){
        runningEvent(new Playing(event.getPlayingTeams(), event.getPlayingPlayers()));
    }
    private void runningEvent(Playing playing){
        state = GameState.RUNNING;

        final var event = new RunGameEvent(this, playing.teams, playing.players);
        gameEvent = event;
        handlers.forEach(handler -> handler.onRun(event));
    }

    public void invokeEndEvent(RunGameEvent event){
        event.cancel();
        endingEvent(new Playing(event.getPlayingTeams(), event.getPlayingPlayers()));
    }
    private void endingEvent(Playing playing) {
        state = GameState.ENDING;


        final var event = new EndGameEvent(this, playing.teams, playing.players);
        handlers.forEach(handler -> handler.onEnd(event));
    }

    private void stopEvent(Player player) {
        final var playing = getPlaying(player);
        if (playing == null) {
            return;
        }

        state = GameState.ENDING;

        final var event = new EndGameEvent(this, playing.teams, playing.players);
        if(gameEvent != null)
            handlers.forEach(handler -> handler.onEnd(event, gameEvent));
        else
            handlers.forEach(handler -> handler.onEnd(event));
    }

    private void pausedEvent(Player player){
        final var playing = getPlaying(player);
        if (playing == null) {
            return;
        }

        state = GameState.PAUSED;

        final var event = new PauseGameEvent(this, playing.teams, playing.players);
        handlers.forEach(handler -> handler.onPause(event));
    }

    private void resumedEvent(Player player) {
        final var playing = getPlaying(player);
        if (playing == null) {
            return;
        }

        state = GameState.RUNNING;

        final var event = new ResumeGameEvent(this, playing.teams, playing.players);
        handlers.forEach(handler -> handler.onResume(event));
    }

    public String getName() {
        return name;
    }

    public GameProperties getProperties() {
        return properties;
    }

    public GameCommandManager getCommandManager() {
        return gameCommandManager;
    }

    public GameState getState() {
        return state;
    }


    private record Playing(List<Team> teams, List<Player> players) {}

    @Nullable
    private Playing getPlaying(Player player) {
        final var teams = TeamManager.getTeams();
        if (teams == null) {
            SenderHelper.sendError(player, "no teams active.");
            return null;
        }
        if (teams.size() < 2) {
            SenderHelper.sendError(player, "There is not enough teams to start the game.");
            return null;
        }
        final var players = new ArrayList<Player>();
        teams.forEach(team -> players.addAll(team.getPlayers()));
        return new Playing(teams, players);
    }
}
