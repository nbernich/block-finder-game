package me.nbernich.blockFinderPlugin.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scoreboard.*;

/**
 * Manages the display of team scores in the sidebar scoreboard.
 * This manager should be enabled when the plugin is enabled, assuming useSidebarScoreboard in the config is true.
 * It should then be started/stopped when the game starts/stops.
 */
public class ScoreboardDisplayManager {

    private final String OBJECTIVE_NAME = "blockFinderScoreDisplay";

    private final Scoreboard scoreboard;
    private final boolean enabled;
    private Objective objective;

    /**
     * Initialzie the ScoreboardDisplayManager, which then initializes an empty scoreboard.
     * If disabled in the configuration, future calls on the ScoreboardDisplayManager will do nothing.
     * @param config The configuration file containing user-defined game settings.
     */
    public ScoreboardDisplayManager(FileConfiguration config) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getMainScoreboard();
        this.objective = null;
        this.enabled = config.getBoolean("useSidebarScoreboard", false);
    }

    /**
     * Starts the on-screen scoreboard display, if enabled.
     * Should be used when the game starts.
     */
    public void start() {
        if (!enabled || objective != null) {
            return;
        }
        Objective existingObjective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (existingObjective != null) {
            existingObjective.unregister();
        }
        this.objective = scoreboard.registerNewObjective(
            OBJECTIVE_NAME,
            Criteria.DUMMY,
            Component.text("Team Scores")
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Turns off the on-screen scoreboard display, if enabled.
     * Should be used when the game stops.
     */
    public void stop() {
        if (!enabled || objective == null) {
            return;
        }
        objective.unregister();
        this.objective = null;
    }

    /**
     * Sync the score for a team with the on-screen scoreboard.
     * @param team the team whose score to sync
     */
    public void syncScore(Team team) {
        if (!enabled || objective == null) {
            return;
        }
        Score score = objective.getScore(team.getName());
        score.setScore(team.getScore());
    }
    
}
