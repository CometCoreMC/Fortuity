package net.cometcore.fortuity.game.commands.general;

import net.cometcore.fortuity.commands.Permission;
import net.cometcore.fortuity.commands.SubCommand;
import net.cometcore.fortuity.game.Game;
import net.cometcore.fortuity.game.GameProperties;
import net.cometcore.fortuity.utils.SenderHelper;
import org.bukkit.entity.Player;

import java.util.List;

public class JoinSubCmd extends SubCommand {
    private final Game game;

    public JoinSubCmd(Game game, GameProperties gameProperties) {
        super("join","Join a game",new Permission(gameProperties.name() + ".game.join"));
        this.game = game;
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        if (args.length != 1) {
            SenderHelper.sendWarning(player, "Usage: /fortuity join");
            return true;
        }
        game.join(player);
        return true;
    }

    @Override
    public List<String> getTabCompleter(Player player, String[] args) {
        return null;
    }
}
