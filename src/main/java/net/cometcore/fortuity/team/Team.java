package net.cometcore.fortuity.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.*;

public class Team {
    public final String name;
    public final UUID uuid;
    public final String prefix;
    public final NamedTextColor color;
    public final Map<Player, PlayerState> players;

    public TeamState teamState = TeamState.ALIVE;

    public Team(String name, String prefix, NamedTextColor color) {
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.prefix = prefix;
        this.color = color;
        this.players = new HashMap<>();
        TeamManager.registerTeam(this);
    }

    /**
     * Generate the chat prefix
     * @return Chat prefix
     */
    public String generateChatPrefix() {
        TextComponent left = Component.text("[").color(NamedTextColor.WHITE);
        TextComponent center = Component.text(name).color(color);
        TextComponent right = Component.text("]").color(NamedTextColor.WHITE);
        return left.content() + center.content() + right.content();
    }

    /**
     * Add a player to the team
     * @param player Player to add
     */
    public void addPlayer(Player player) {
        players.put(player, PlayerState.ALIVE);
    }

    /**
     * Remove a player from the team
     * @param player Player to remove
     */
    public void removePlayer(Player player) {
        players.remove(player);
    }

    /**
     * Check if a player is in the team
     * @return True if the player is in the team
     */
    public boolean hasPlayer(Player player) {
        return players.containsKey(player);
    }

    /**
     * Add players to the team
     * @param players Players to set
     */
    public void addPlayers(List<Player> players) {
        for(Player player : players){
            this.players.put(player, PlayerState.ALIVE);
        }
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public List<Player> getPlayers() {
        return players.keySet().stream().toList();
    }

    public List<Player> getPlayersWithState(PlayerState state) {
        return players.entrySet().stream()
                .filter(entry -> entry.getValue() == state)
                .map(Map.Entry::getKey).toList();
    }

    public void changePlayerState(Player player, PlayerState state) {
        if (players.containsKey(player)) {
            players.put(player, state);
        }
    }

    public void changeTeamState(TeamState state) {
        this.teamState = state;
    }

    public UUID getUUID() {
        return uuid;
    }


    public String toString() {
        return "TEAM " + name;
    }

    public enum PlayerState {
        ALIVE("Alive"),
        ELIMINATED("Eliminated");

        final String name;

        PlayerState(String name) {
            this.name = name;
        }


        public String toString() {
            return this.name;
        }
    }
}
