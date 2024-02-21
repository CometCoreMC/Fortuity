package net.cometcore.fortuity.configs.gameMessages;

import net.cometcore.fortuity.game.Game;
import org.bukkit.entity.Player;

public abstract class GameMessageParser {
    final String placeholder;

    /**
     *
     * @param placeholder Placeholder to be replaced
     */
    public GameMessageParser(String placeholder) {
        this.placeholder = placeholder;
    }

    /**
     * Parse the message and replace the placeholders with the values.
     * @param message Message to parse
     * @param player Player who is parsing the message
     * @param game Game's name
     * @return Parsed message
     */
    public abstract String parseMessage(String message, Player player, Game game);
}
