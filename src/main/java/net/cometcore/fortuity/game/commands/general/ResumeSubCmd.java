package net.cometcore.fortuity.game.commands.general;

import net.cometcore.fortuity.commands.Permission;
import net.cometcore.fortuity.commands.SubCommand;
import net.cometcore.fortuity.game.Game;
import net.cometcore.fortuity.game.GameProperties;
import net.cometcore.fortuity.game.GameState;
import net.cometcore.fortuity.utils.SenderHelper;
import org.bukkit.entity.Player;

import java.util.List;

public class ResumeSubCmd extends SubCommand {
    private final Game game;

    public ResumeSubCmd(Game game, GameProperties gameProperties) {
        super("resume", "Resume the game", gameProperties, new Permission(gameProperties.name() + ".game.resume"));
        this.game = game;
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        if (game.getState() != GameState.PAUSED) {
            SenderHelper.sendWarning(player, "The game is not paused");
            return true;
        }
        game.resume(player);
        return true;
    }

    @Override
    public List<String> getTabCompleter(Player player, String[] args) {
        return null;
    }
}
