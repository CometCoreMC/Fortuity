package net.cometcore.fortuity.configs.gameConfig;

import net.cometcore.fortuity.game.Game;
import org.bukkit.entity.Player;

public abstract class GameConfigParser {
    final String placeholder;

    /**
     *
     * @param placeholder Placeholder to be replaced
     */
    public GameConfigParser(String placeholder) {
        this.placeholder = placeholder;
    }

    /**
     * Parse the message and replace the placeholders with the values.
     * @param config Config to parse
     * @param player Player who is parsing the message
     * @param game Game's name
     * @return Parsed message
     */
    public abstract String parseConfig(String config, Player player, Game game);
}
