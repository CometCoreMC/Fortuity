package net.cometcore.fortuity.event.events;

import net.cometcore.fortuity.event.GameEvent;
import net.cometcore.fortuity.event.generic.GenericStateGameEvent;
import net.cometcore.fortuity.game.Game;
import net.cometcore.fortuity.team.Team;
import org.bukkit.entity.Player;

import java.util.List;

public class ResumeGameEvent extends GameEvent implements GenericStateGameEvent {
    public final List<Team> teams;
    public final List<Player> players;
    public final Game game;

    public ResumeGameEvent(Game game, List<Team> teams, List<Player> players) {
        this.teams = teams;
        this.players = players;
        this.game = game;
    }

    @Override
    public void cancel() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot cancel a start game event");
    }

    @Override
    public List<Team> getPlayingTeams() {
        return teams;
    }

    @Override
    public List<Player> getPlayingPlayers() {
        return players;
    }

    @Override
    public Game getGame() {
        return game;
    }
}
