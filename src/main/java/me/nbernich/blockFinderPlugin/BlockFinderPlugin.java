package me.nbernich.blockFinderPlugin;

import me.nbernich.blockFinderPlugin.commands.GameCommand;
import me.nbernich.blockFinderPlugin.commands.ScoresCommand;
import me.nbernich.blockFinderPlugin.commands.TeamsCommand;
import me.nbernich.blockFinderPlugin.game.BlockManager;
import me.nbernich.blockFinderPlugin.game.TeamManager;
import me.nbernich.blockFinderPlugin.listeners.BlockFindListener;
import me.nbernich.blockFinderPlugin.tabcompleters.GameTabCompleter;
import me.nbernich.blockFinderPlugin.tabcompleters.ScoresTabCompleter;
import me.nbernich.blockFinderPlugin.tabcompleters.TeamsTabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The plugin for a game about finding blocks.
 * Initializes all commands, listeners, and managers that control the game state.
 * Commands and managers can interact with each other through this plugin instance, if they need to.
 */
public final class BlockFinderPlugin extends JavaPlugin {

    private BlockManager blockManager;
    private TeamManager teamManager;
    private BlockFindListener blockFindListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        this.blockManager = new BlockManager(this, config);
        this.teamManager = new TeamManager(this, config);
        this.blockFindListener = new BlockFindListener(this);

        blockManager.loadAllBlockTypes();

        getCommand("bfinder").setExecutor(new GameCommand(this));
        getCommand("bfteams").setExecutor(new TeamsCommand(this));
        getCommand("bfscores").setExecutor(new ScoresCommand(this));

        getCommand("bfinder").setTabCompleter(new GameTabCompleter(this));
        getCommand("bfteams").setTabCompleter(new TeamsTabCompleter(this));
        getCommand("bfscores").setTabCompleter(new ScoresTabCompleter(this));

        getLogger().info("Block Finder minigame plugin enabled!");
    }

    @Override
    public void onDisable() {
        blockManager.clearTargets();
        blockManager.clearBlockTypes();
        teamManager.resetTeams();
        teamManager.stopScoreboardDisplay();
        blockFindListener.stop();

        getLogger().info("Block Finder minigame plugin disabled.");
    }

    /**
     * Get the BlockManager for this plugin.
     * @return the BlockManager for this plugin.
     */
    public BlockManager getBlockManager() {
        return blockManager;
    }

    /**
     * Get the TeamManager for this plugin.
     * @return the TeamManager for this plugin.
     */
    public TeamManager getTeamManager() {
        return teamManager;
    }

    /**
     * Get the BlockFindListener for this plugin.
     * @return the BlockFindListener for this plugin.
     */
    public BlockFindListener getBlockFindListener() {
        return blockFindListener;
    }
}
