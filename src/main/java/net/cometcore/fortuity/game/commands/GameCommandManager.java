package net.cometcore.fortuity.game.commands;

import net.cometcore.fortuity.commands.SubCommand;
import net.cometcore.fortuity.game.Game;
import net.cometcore.fortuity.game.GameProperties;
import net.cometcore.fortuity.game.commands.general.*;

import java.util.ArrayList;
import java.util.List;

public class GameCommandManager {
    private final List<SubCommand> subcommands;
    public final GameProperties gameProperties;
    public final Game game;

    public GameCommandManager(Game game, GameProperties properties) {
        subcommands = generateList(game,properties);
        gameProperties = properties;
        this.game = game;
    }

    /**
     * Register the class GameCommands
     * You must use this if you want to use the custom game commands!
     * @return GameCommands to register in the main file
     */
    public GameCommands registerGameCommands() {
        return new GameCommands(subcommands);
    }

    /**
     * Add a subcommand to the list
     * @param subcommand Subcommand to add to the list
     */
    public void registerSubcommand(SubCommand subcommand) {
        subcommands.add(subcommand);
    }

    /**
     * Remove a subcommand from the list
     * @param subcommand Subcommand to remove from the list
     */
    public void unregisterSubcommand(SubCommand subcommand) {
        subcommands.remove(subcommand);
    }

    public List<SubCommand> getSubcommands() {
        return subcommands;
    }

    /**
     * Generate a list of subcommands
     * @return List of subcommands
     */
    private static List<SubCommand> generateList(Game game, GameProperties properties) {
        final List<SubCommand> list = new ArrayList<>();
        list.add(new JoinSubCmd(game,properties));
        list.add(new LeaveSubCmd(game,properties));
        list.add(new StartSubCmd(game, properties));
        list.add(new StopSubCmd(game,properties));
        list.add(new PauseSubCmd(game,properties));
        list.add(new ResumeSubCmd(game,properties));
        return list;
    }
}
