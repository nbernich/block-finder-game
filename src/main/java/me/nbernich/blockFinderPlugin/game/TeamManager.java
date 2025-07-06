package me.nbernich.blockFinderPlugin.game;

import me.nbernich.blockFinderPlugin.BlockFinderPlugin;
import me.nbernich.blockFinderPlugin.utils.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to manage teams, their memebers, and their scores.
 * Also owns a ScoreboardDisplayManager to optionally sync scores with a sidebar scoreboard.
 */
public class TeamManager  {

    private final ScoreboardDisplayManager scoreboardDisplayManager;
    private final HashMap<String, Team> teamsByName;
    private final HashMap<Player, Team> memberships;

    /**
     * Initialize the TeamManager with user-defined settings, including a list of teams.
     * Also creates an associated ScoreboardDisplayManager.
     * @param config The configuration file containing game settings.
     */
    public TeamManager(BlockFinderPlugin plugin, FileConfiguration config) {
        this.scoreboardDisplayManager = new ScoreboardDisplayManager(config);
        this.teamsByName = new HashMap<>();
        this.memberships = new HashMap<>();

        List<Map<?, ?>> teamList = config.getMapList("teams");
        for (Map<?, ?> teamData : teamList) {
            String name = String.valueOf(teamData.get("name"));
            if (name.contains(" ")) {
                plugin.getLogger().warning(
                    String.format(
                        "Team name '%s' contains multiple words, which is not allowed. It will be ignored.",
                        name
                    )
                );
                continue;
            }
            String colorString = String.valueOf(teamData.get("color"));
            TextColor color = Colors.parseColor(colorString);
            Team team = new Team(name, color);
            teamsByName.put(name, team);
        }
    }

    /**
     * Add a player to a team.
     * @param player the player to add
     * @param teamName the name of the team to add the player to
     * @return true if the player was added, false if the team does not exist
     */
    public boolean addPlayer(Player player, String teamName) {
        Team team = teamsByName.get(teamName);
        if (team == null) {
            return false;
        }

        Team oldTeam = memberships.get(player);
        if (oldTeam != null) {
            oldTeam.removePlayer(player);
        }

        team.addPlayer(player);
        memberships.put(player, team);
        return true;
    }

    /**
     * Remove a player from their team.
     * @param player the player to remove
     * @return true if the player was removed, false if the player was not in a team
     */
    public boolean removePlayer(Player player) {
        Team team = memberships.get(player);
        if (team == null) {
            return false;
        }

        team.removePlayer(player);
        memberships.remove(player);
        return true;
    }

    /**
     * Get the team a player is on.
     * @param player the player to get the team for
     * @return the team the player is on, or null if the player is not on a team
     */
    public Team getPlayerTeam(Player player) {
        return memberships.get(player);
    }

    /**
     * Get all team names.
     * @return a set of all team names
     */
    public Set<String> getTeamNames() {
        return teamsByName.keySet();
    }

    /**
     * Reset all teams, clearing their members and scores.
     */
    public void resetTeams() {
        for (Team team : teamsByName.values()) {
            team.clearMembers();
            team.setScore(0);
            scoreboardDisplayManager.syncScore(team);
        }
        teamsByName.clear();
        memberships.clear();
    }

    /**
     * Add a point to the player's team.
     * @param player the player to add a point for
     * @return true if the point was added, false if the player is not on a team
     */
    public boolean addPoint(Player player) {
        Team team = memberships.get(player);
        if (team == null) {
            return false;
        }
        team.addPoint();
        scoreboardDisplayManager.syncScore(team);
        return true;
    }

    /**
     * Set the score for a team.
     * @param teamName the name of the team to set the score for
     * @param newScore the new score to set
     * @return true if the score was set, false if the team does not exist
     */
    public boolean setScore(String teamName, int newScore) {
        Team team = teamsByName.get(teamName);
        if (team == null) {
            return false;
        }
        team.setScore(newScore);
        scoreboardDisplayManager.syncScore(team);
        return true;
    }

    /**
     * Reset the scores of all teams to zero.
     */
    public void resetScores() {
        for (Team team : teamsByName.values()) {
            team.clearScore();
            scoreboardDisplayManager.syncScore(team);
        }
    }

    /**
     * Start the scoreboard display manager, syncing scores for all teams.
     * This should be called when the game starts.
     */
    public void startScoreboardDisplay() {
        scoreboardDisplayManager.start();
        for (Team team : teamsByName.values()) {
            scoreboardDisplayManager.syncScore(team);
        }
    }

    /**
     * Stop the scoreboard display manager.
     * This should be called when the game stops.
     */
    public void stopScoreboardDisplay() {
        scoreboardDisplayManager.stop();
    }

    /**
     * Get the display name for a player. This will include their team color if they are on a team.
     * @param player the player to get the display name for
     * @return a Component containing the player's name, colored by their team if applicable
     */
    public Component getPlayerDisplayName(Player player) {
        Team team = memberships.get(player);
        if (team != null) {
            return Component.text(player.getName(), team.getColor());
        }
        return Component.text(player.getName(), Colors.DEFAULT);
    }

    /**
     * Create a Component listing the teams and their members.
     * @return a Component displaying membership information
     */
    public Component createMemberListMessage() {
        Component message = Component.empty();
        for (Team team : teamsByName.values()) {
            message = message.append(Component.text("  * ", Colors.DEFAULT))
                .append(team.getDisplayName())
                .append(Component.text(":", Colors.DEFAULT))
                .appendNewline();

            if (team.getMembers().isEmpty()) {
                message = message.append(Component.text("      [No Members]", Colors.DEFAULT))
                    .appendNewline();
                continue;
            }

            for (Player player : team.getMembers()) {
                message = message.append(Component.text("      - ", Colors.DEFAULT))
                    .append(Component.text(player.getName(), team.getColor()))
                    .appendNewline();
            }
        }
        return message;
    }

    /**
     * Create a Component listing the scores of all teams.
     * @return a Component displaying the scores of all teams
     */
    public Component createScoresListMessage() {
        Component message = Component.empty();
        for (Team team : teamsByName.values()) {
            String pointsLabel = team.getScore() == 1 ? " point" : " points";
            message = message.append(Component.text("  * ", Colors.DEFAULT))
                .append(team.getDisplayName())
                .append(Component.text(": ", Colors.DEFAULT))
                .append(Component.text(team.getScore(), Colors.HEADER))
                .append(Component.text(pointsLabel, Colors.DEFAULT))
                .appendNewline();
        }
        return message;
    }

}
