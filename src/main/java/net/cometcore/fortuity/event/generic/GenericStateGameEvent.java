package net.cometcore.fortuity.event.generic;

import net.cometcore.fortuity.game.Game;
import net.cometcore.fortuity.team.Team;
import org.bukkit.entity.Player;

import java.util.List;

public interface GenericStateGameEvent {
    List<Team> getPlayingTeams();
    List<Player> getPlayingPlayers();
    Game getGame();
}
