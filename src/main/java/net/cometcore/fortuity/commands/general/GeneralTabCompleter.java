package net.cometcore.fortuity.commands.general;

import net.cometcore.fortuity.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public abstract class GeneralTabCompleter implements TabCompleter {
    protected final List<SubCommand> subcommands;

    protected GeneralTabCompleter(List<SubCommand> subcommands) {
        this.subcommands = subcommands;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias, String[] args) {
        if (!(sender instanceof final Player player)) {
            return null;
        }

        // Return list of subcommands
        if (args.length == 1) {
            return subcommands.stream().map(SubCommand::getIdentifier).toList();
        }

        for (SubCommand subcommand : subcommands) {
            if (subcommand.identifier.equals(args[0])) {
                return complete(player, subcommand, args);
            }
        }

        return null;
    }

    protected abstract List<String> complete(Player player, SubCommand subcommand, String[] args);

    /**
     * Complete the subcommand
     * @param player The player who executed the command
     * @param subcommand The subcommand to complete
     * @param args The arguments passed to the command
     * @return A list of possible completions
     */
    @Nullable
    protected List<String> onSubcommand(Player player, SubCommand subcommand, String[] args) {
        for (SubCommand sub : subcommands) {
            if (sub.getIdentifier().equals(subcommand.getIdentifier())) {
                return sub.getTabCompleter(player, args);
            }
        }
        return null;
    }
}