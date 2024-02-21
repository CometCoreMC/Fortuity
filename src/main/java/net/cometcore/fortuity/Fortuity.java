package net.cometcore.fortuity;

import net.cometcore.fortuity.event.GameEventHandler;
import net.cometcore.fortuity.game.Game;
import net.cometcore.fortuity.game.GameProperties;
import net.cometcore.fortuity.game.commands.GameCommandManager;
import net.cometcore.fortuity.game.commands.GameCommands;
import net.cometcore.fortuity.messages.Message;
import net.cometcore.fortuity.messages.MessageManager;
import net.cometcore.fortuity.utils.config.Config;
import net.cometcore.fortuity.utils.config.ConfigAPI;
import net.cometcore.fortuity.worlds.game.GameWorld;
import net.cometcore.fortuity.worlds.game.GameWorldGenerator;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.logging.Logger;

public final class Fortuity extends JavaPlugin implements Listener {

    private static Fortuity INSTANCE;
    private static Message GAME_MESSAGES;
    private static ConfigAPI CONFIG_API;
    private static Logger LOGGER;
    private static Game GAME;
    GameProperties gameProperties;

    @Override
    public void onEnable() {
        // REMOVE AFTER DEV - Hot Reload on update
        final long lastModified = getFile().lastModified();

        new BukkitRunnable() {
            public void run() {
                if (getFile().lastModified() > lastModified) {
                    cancel();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload confirm");
                }
            }
        }.runTaskTimer(this, 0, 20);


        INSTANCE = this;
        LOGGER = getLogger();

        CONFIG_API = new ConfigAPI(this);

        this.generateConfigs();

        GAME = new Game(this, "fortuity");
        gameProperties = new GameProperties("fortuity");

        this.registerCommands();

        GAME.registerEventHandler(new GameEventHandler());

        LOGGER.info("GameLibrary has been loaded!");

        World defaultWorld = Bukkit.getWorld("world");
        if(defaultWorld != null) {
            defaultWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            defaultWorld.setGameRule(GameRule.FALL_DAMAGE, false);
            defaultWorld.setGameRule(GameRule.DROWNING_DAMAGE, false);
            defaultWorld.setGameRule(GameRule.FIRE_DAMAGE, false);
            defaultWorld.setGameRule(GameRule.FREEZE_DAMAGE, false);
            defaultWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            defaultWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        LOGGER.info("GameLibrary has been unloaded!");
    }

    public static Fortuity getInstance() {
        return INSTANCE;
    }
    public static Logger getLOGGER() {
        return LOGGER;
    }
    public static Message getGameMessages() {
        return GAME_MESSAGES;
    }
    public static ConfigAPI getConfigApi() {
        return CONFIG_API;
    }

    private void generateConfigs() {
        final Config messagesConfig = CONFIG_API.getConfig("messages");
        GAME_MESSAGES = MessageManager.setupGameMessage(messagesConfig);
    }

    private void registerCommands() {
        final PluginCommand gameCommand = getCommand("fortuity");
        if (gameCommand != null) {
            GameCommandManager gameCommandManager = new GameCommandManager(GAME, gameProperties);
            GameCommands gameCommands = gameCommandManager.registerGameCommands();
            gameCommand.setExecutor(gameCommands);
            gameCommand.setTabCompleter(gameCommands.getGenericTabCompleter());
        }
    }

    // Event handler to set players to join the default world
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World defaultWorld = Bukkit.getWorld("world");
        if(defaultWorld != null)
            player.teleport(defaultWorld.getSpawnLocation());

        grantPermission(player, "fortuity.game.join");
        grantPermission(player, "fortuity.game.leave");
    }

    // Grant a permission to a player
    public void grantPermission(Player player, String permissionNode) {
        // Get or create the PermissionAttachment for the player
        PermissionAttachment attachment = player.addAttachment(this);

        // Grant the permission node
        attachment.setPermission(permissionNode, true);

        // Recalculate permissions
        player.recalculatePermissions();
    }

}
