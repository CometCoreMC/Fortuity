package net.cometcore.fortuity;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.Logger;

import net.cometcore.fortuity.configs.gameConfig.GameConfig;
import net.cometcore.fortuity.configs.gameConfig.GameConfigManager;
import net.cometcore.fortuity.configs.gameMessages.GameMessage;
import net.cometcore.fortuity.configs.gameMessages.GameMessageManager;
import net.cometcore.fortuity.configs.Config;
import net.cometcore.fortuity.configs.ConfigAPI;

import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.scheduler.BukkitRunnable;

import net.minestom.server.MinecraftServer;


public final class FortuitySpigot extends JavaPlugin {

    private static FortuitySpigot INSTANCE;
    private static GameMessage GAME_MESSAGES;
    private static GameConfig GAME_CONFIG;
    private static ConfigAPI CONFIG_API;
    private static MinecraftServer SERVER;
    private static Logger LOGGER;

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
        this.registerCommands();

        LOGGER.info("GameLibrary has been loaded!");

        // Initialize the server
        SERVER = MinecraftServer.init();

        // REGISTER EVENTS (set spawn instance, teleport player at spawn)

        // Start the server
        SocketAddress socketAddress = new InetSocketAddress("fortuity", 0);
        SERVER.start(socketAddress);

    }

    @Override
    public void onDisable() {
        LOGGER.info("Fortuity has been unloaded!");
    }

    public static FortuitySpigot getINSTANCE() {
        return INSTANCE;
    }
    public static MinecraftServer getSERVER() {
        return SERVER;
    }
    public static Logger getLOGGER() {
        return LOGGER;
    }
    public static GameMessage getMESSAGE() {
        return GAME_MESSAGES;
    }
    public static GameConfig getCONFIG() {
        return GAME_CONFIG;
    }


    private void generateConfigs() {
        final Config messagesConfig = CONFIG_API.getConfig("messages");
        GAME_MESSAGES = GameMessageManager.setupGameMessage(messagesConfig);

        final Config gameConfig = CONFIG_API.getConfig("config");
        GAME_CONFIG = GameConfigManager.setupGameConfig(gameConfig);
    }

    private void registerCommands() {
        final PluginCommand gameCommand = getCommand("fortuity");
        if (gameCommand != null) {

        }
    }


}
