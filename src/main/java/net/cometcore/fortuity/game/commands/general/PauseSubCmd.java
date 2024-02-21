package net.cometcore.fortuity.game.commands.general;

import net.cometcore.fortuity.commands.Permission;
import net.cometcore.fortuity.commands.SubCommand;
import net.cometcore.fortuity.game.Game;
import net.cometcore.fortuity.game.GameProperties;
import net.cometcore.fortuity.game.GameState;
import net.cometcore.fortuity.utils.SenderHelper;
import org.bukkit.entity.Player;

import java.util.List;

public class PauseSubCmd extends SubCommand {
    private final Game game;

    public PauseSubCmd(Game game, GameProperties gameProperties) {
        super("pause", "Pause the game", gameProperties, new Permission(gameProperties.name() + ".game.pause"));
        this.game = game;
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        if (game.getState() != GameState.RUNNING) {
            SenderHelper.sendWarning(player, "The game is not running");
            return true;
        }
        game.pause(player);
        return true;
    }

    @Override
    public List<String> getTabCompleter(Player player, String[] args) {
        return null;
    }
}
