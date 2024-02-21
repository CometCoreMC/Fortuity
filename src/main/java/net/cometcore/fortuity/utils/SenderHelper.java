package net.cometcore.fortuity.utils;

import net.cometcore.fortuity.Fortuity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SenderHelper {
    public static NamedTextColor SUCCESS = NamedTextColor.GREEN;
    public static NamedTextColor ERROR = NamedTextColor.RED;
    public static NamedTextColor WARNING = NamedTextColor.YELLOW;
    public static NamedTextColor INFO = NamedTextColor.BLUE;
    public static NamedTextColor DEFAULT = NamedTextColor.WHITE;
    public static NamedTextColor EXAMPLE = NamedTextColor.DARK_GREEN;

    /**
     * Send a message to a player
     * @param player Player to send the message
     * @param message Message to send
     * @param color Color of the message
     */
    public static void sendMessage(Player player, NamedTextColor color, String message) {
        player.sendMessage(Component.text(message).color(color));
    }

    /**
     * Send a message to multiple players
     * @param players Players to send the message
     * @param message Message to send
     * @param color Color of the message
     */
    public static void sendMessage(List<Player> players, NamedTextColor color, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(Fortuity.getInstance(), () -> {
            for(Player player : players)
                sendMessage(player, color, message);
        });
    }

    /**
     * Send a success message to a player
     * @param player Player to send the message
     * @param message Message to send
     */
    public static void sendSuccess(Player player, String message) {
        sendMessage(player, SUCCESS, message);
    }

    /**
     * Send an error message to a player
     * @param player Player to send the message
     * @param message Message to send
     */
    public static void sendError(Player player, String message) {
        sendMessage(player, ERROR, message);
    }

    /**
     * Send a warning message to a player
     * @param player Player to send the message
     * @param message Message to send
     */
    public static void sendWarning(Player player, String message) {
        sendMessage(player, WARNING, message);
    }

    /**
     * Send an info message to a player
     * @param player Player to send the message
     * @param message Message to send
     */
    public static void sendInfo(Player player, String message) {
        sendMessage(player, INFO, message);
    }

    /**
     * Send an example message to a player
     * @param player Player to send the message
     * @param message Message to send
     */
    public static void sendExample(Player player, String message) {
        sendMessage(player, EXAMPLE, message);
    }

    /**
     * Send a message to a player
     * @param player Player to send the message
     * @param message Message to send
     */
    public static void sendMessage(Player player, String message) {
        sendMessage(player, DEFAULT, message);
    }

    /**
     * Send a broadcast
     * @param color Color of the message
     * @param message Message to send
     */
    private static void broadcastMessage(NamedTextColor color, String message) {
        Fortuity.getInstance().getServer().broadcast(Component.text(message).color(color));
    }

    /**
     * Send a success broadcast
     * @param message Message to send
     */
    public static void broadcastSuccess(String message) {
        broadcastMessage(SUCCESS, message);
    }

    /**
     * Send an error broadcast
     * @param message Message to send
     */
    public static void broadcastError(String message) {
        broadcastMessage(ERROR, message);
    }

    /**
     * Send a warning broadcast
     * @param message Message to send
     */
    public static void broadcastWarning(String message) {
        broadcastMessage(WARNING, message);
    }

    /**
     * Send an info broadcast
     * @param message Message to send
     */
    public static void broadcastInfo(String message) {
        broadcastMessage(INFO, message);
    }

    /**
     * Send an example broadcast
     * @param message Message to send
     */
    public static void broadcastExample(String message) {
        broadcastMessage(EXAMPLE, message);
    }

    /**
     * Send a broadcast
     * @param message Message to send
     */
    public static void broadcastMessage(String message) {
        broadcastMessage(DEFAULT, message);
    }

}