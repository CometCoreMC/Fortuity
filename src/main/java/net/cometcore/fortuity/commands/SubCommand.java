package net.cometcore.fortuity.commands;

import net.cometcore.fortuity.commands.general.GeneralCommand;
import net.cometcore.fortuity.game.GameProperties;
import net.cometcore.fortuity.team.Team;
import net.cometcore.fortuity.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SubCommand {
    /**
     * Identifier of the subcommand.
     * Example: hey for /game hey
     */
    public final String identifier;
    public final String description;
    protected final GameProperties properties;
    public final Permission permission;
    protected GeneralCommand command;

    public SubCommand(String identifier, String description, GameProperties properties) {
        this.identifier = identifier;
        this.description = description;
        this.properties = properties;
        this.permission = new Permission("");
    }

    public SubCommand(String identifier, String description, Permission permission) {
        this.identifier = identifier;
        this.description = description;
        this.permission = permission;
        this.properties = null;
    }

    public SubCommand(String identifier, String description, GameProperties properties, Permission permission) {
        this.identifier = identifier;
        this.description = description;
        this.permission = permission;
        this.properties = properties;
    }

    public GeneralCommand getCommand() {
        return command;
    }

    public void setCommand(GeneralCommand command) {
        this.command = command;
    }

    /**
     * When the subcommand is executed.
     * @param player Player who executed the subcommand.
     * @param args Arguments of the subcommand. The first args is the identifier of the subcommand.
     * @return True if the subcommand was executed successfully like spigot CommandsExecutor::onCommand.
     */
    public abstract boolean onCommand(Player player, String[] args);

    /**
     * Tab completer for the subcommand.
     * @param player Player who executed the subcommand.
     * @param args Arguments of the subcommand. The first args is the identifier of the subcommand.
     * @return List of possible completions.
     */
    public abstract List<String> getTabCompleter(Player player, String[] args);

    /**
     * Get the list of player's name
     * @return List of player's name
     */
    protected List<String> playerListName() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    /**
     * Get the list of team's name
     * @return List of team's name
     */
    @Nullable
    protected List<String> teamListName() {
        final List<Team> teams = TeamManager.getTeams();
        if (teams == null || teams.isEmpty()) {
            return null;
        }
        return teams.stream().map(Team::getName).collect(Collectors.toList());
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDescription() {
        return description;
    }
}
