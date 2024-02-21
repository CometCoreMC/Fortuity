package net.cometcore.fortuity.configs.gameConfig;

import net.cometcore.fortuity.configs.Config;
import net.cometcore.fortuity.game.Game;
import net.cometcore.fortuity.game.team.Team;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class GameConfigManager {
    private static List<GameConfigParser> customMessageParsers;

    /**
     * Set up the message object
     * @param config The configuration
     */
    public static GameConfig setupGameConfig(Config config) {
        return new GameConfig("game-commands");
    }

    /**
     * Parse a message
     * @param message Message to parse
     * @param player Player who is parsing the message
     * @param game Game
     * @param team Team
     * @return Parsed message
     */
    public static String parseConfig(String message, @Nullable Player player, Game game, @Nullable Team team) {
        return message;
    }

    /**
     * Parse a message
     * @param message Message to parse
     * @param player Player who is parsing the message
     * @param game Game
     * @return Parsed message
     */
    public static String parseConfig(String message, Player player, Game game) {
        return parseConfig(message, player, game, null);
    }
    /**
     * Parse a message
     * @param message Message to parse
     * @param game Game
     * @return Parsed message
     */
    public static String parseConfig(String message, Game game) {
        return parseConfig(message, game, null);
    }
    /**
     * Parse a message
     * @param message Message to parse
     * @param game Game
     * @param team Team
     * @return Parsed message
     */
    public static String parseConfig(String message, Game game, Team team) {
        return parseConfig(message, null, game, team);
    }

    private static String replace(String message, String target, String replacement) {
        while (message.contains(target)) {
            message = message.replace(target, replacement);
        }
        return message;
    }

    private static String replace(String message, String target, int replacement) {
        return replace(message, target, String.valueOf(replacement));
    }
}
