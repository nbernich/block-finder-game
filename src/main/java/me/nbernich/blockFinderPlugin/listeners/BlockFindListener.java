package me.nbernich.blockFinderPlugin.listeners;

import me.nbernich.blockFinderPlugin.BlockFinderPlugin;
import me.nbernich.blockFinderPlugin.game.BlockManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Listener for detecting when a player finds a target block type.
 * This occurs when a player toggles sneak while standing up to two blocks above a target block.
 */
public class BlockFindListener implements Listener {

    private final BlockFinderPlugin plugin;
    private final BlockManager blockManager;

    public BlockFindListener(BlockFinderPlugin plugin) {
        this.plugin = plugin;
        this.blockManager = plugin.getBlockManager();
    }

    /**
     * Starts this event listener.
     */
    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Stops this event listener.
     */
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    /**
     * Handle the PlayerToggleSneakEvent to check for target blocks.
     * @param event the PlayerToggleSneakEvent triggered by a player sneaking
     */
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (!blockManager.hasCurrentTargets()) {
            return;
        }

        Block playerBlock = event.getPlayer().getLocation().getBlock();
        Material[] candidateBlockTypes = {
            playerBlock.getType(),
            playerBlock.getRelative(BlockFace.DOWN).getType(),
            playerBlock.getRelative(BlockFace.DOWN, 2).getType()
        };

        for (Material candidate : candidateBlockTypes) {
            if (blockManager.isTarget(candidate)) {
                blockManager.handleFoundBlock(event.getPlayer(), candidate);
                return;
            }
        }
    }
}