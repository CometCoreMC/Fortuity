package net.cometcore.fortuity.game.commands;

import net.cometcore.fortuity.commands.SubCommand;
import net.cometcore.fortuity.commands.general.GeneralCommand;
import net.cometcore.fortuity.utils.SenderHelper;
import org.bukkit.entity.Player;

import java.util.List;

public class GameCommands extends GeneralCommand {
    public GameCommands(List<SubCommand> subcommands) {
        super(subcommands);
    }

    @Override
    public boolean command(Player player, String label, String[] args) {
        if (!onSubcommand(player, label, args)) {
            SenderHelper.sendWarning(player, "Unknown subcommand: " + args[0]);
        }

        return true;
    }
}
