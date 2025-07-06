package me.nbernich.blockFinderPlugin.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a team in the Block Finder game.
 */
public class Team {

    private final String name;
    private final TextColor color;
    private final HashSet<Player> members;
    private int score;

    public Team(String name, TextColor color) {
        this.name = name;
        this.color = color;
        this.score = 0;
        this.members = new HashSet<>();
    }

    /**
     * Get the name of the team.
     * @return the name of the team
     */
    public String getName() {
        return name;
    }

    /**
     * Get the color of the team.
     * @return the color of the team
     */
    public TextColor getColor() {
        return color;
    }

    /**
     * Add a player to the team.
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        members.add(player);
    }

    /**
     * Remove a player from the team.
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        members.remove(player);
    }

    /**
     * Get a set of members on this team.
     * @return a set of players that are members of this team
     */
    public Set<Player> getMembers() {
        return members;
    }

    /**
     * Clear all members from the team.
     */
    public void clearMembers() {
        members.clear();
    }

    /**
     * Add a point to the team's score.
     */
    public void addPoint() {
        this.score++;
    }

    /**
     * Get the team's score.
     * @return the team's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Set the team's score to a new value.
     * @param newScore the new score to set
     */
    public void setScore(int newScore) {
        this.score = newScore;
    }

    /**
     * Clear the team's score, setting it to zero.
     */
    public void clearScore() {
        this.score = 0;
    }

    /**
     * Get the display name of the team, formatted with its color.
     * @return a Component representing the team's display name
     */
    public Component getDisplayName() {
        return Component.text(name, color);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Team other)) {
            return false;
        }
        return this.name.equals(other.name) && this.color.equals(other.color);
    }
}
