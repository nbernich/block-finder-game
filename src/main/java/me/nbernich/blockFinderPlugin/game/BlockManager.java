package me.nbernich.blockFinderPlugin.game;

import me.nbernich.blockFinderPlugin.BlockFinderPlugin;
import me.nbernich.blockFinderPlugin.utils.Colors;
import me.nbernich.blockFinderPlugin.utils.Formatting;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * The manager responsible for selecting and tracking target block types.
 */
public class BlockManager {

    private final static int DEFAULT_TARGET_COUNT = 3;
    private final static int MAX_TARGET_COUNT = 8;

    private final BlockFinderPlugin plugin;
    private final ArrayList<Material> blockTypes;
    private final HashSet<Material> currentTargets;
    private final Random rng;
    private final int targetCount;
    private final boolean shouldPreventRepeatBlocks;
    private final boolean showScoresOnFound;
    private boolean processingFound; // flag to (potentially) prevent double counting

    /**
     * Initialize the BlockManager with user-defined settings.
     * @param plugin The game plugin this manager is connected to.
     * @param config The configuration file containing game settings.
     */
    public BlockManager(BlockFinderPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.blockTypes = new ArrayList<>();
        this.currentTargets = new HashSet<>();

        int configTargetCount = config.getInt("targetCount", DEFAULT_TARGET_COUNT);
        if (configTargetCount < 1) {
            plugin.getLogger().warning("Invalid target count. Cannot be less than 1. Using default.");
            this.targetCount = DEFAULT_TARGET_COUNT;
        } else if (configTargetCount > MAX_TARGET_COUNT) {
            plugin.getLogger().warning(
                String.format("Invalid target count. Cannot be greater than %d. Using maximum.", MAX_TARGET_COUNT)
            );
            this.targetCount = MAX_TARGET_COUNT;
        } else {
            this.targetCount = configTargetCount;
        }

        this.rng = new Random();
        this.shouldPreventRepeatBlocks = config.getBoolean("shouldPreventRepeatBlocks", false);
        this.showScoresOnFound = config.getBoolean("showScoresOnFound", false);
        this.processingFound = false;
    }

    /**
     * Load all block types in the game as candidates for target selection.
     */
    public void loadAllBlockTypes() {
        if (!blockTypes.isEmpty()) {
           blockTypes.clear();
        }
        for (Material material : Material.values()) {
            if (material.isBlock()) {
                blockTypes.add(material);
            }
        }
    }

    /**
     * Clear the collection of block types that are candidates for target selection.
     */
    public void clearBlockTypes() {
        blockTypes.clear();
    }

    /**
     * Select a new set of target block types from the collection of available block types.
     * If shouldPreventRepeatBlocks is true, the block types will not be selected again until all have been used.
     */
    public void selectNewTargets() throws IllegalStateException {
        if (!currentTargets.isEmpty()) {
            currentTargets.clear();
        }

        if (blockTypes.size() < targetCount) {
            if (shouldPreventRepeatBlocks) {
                plugin.getLogger().info("Too many block types have been used. Resetting possible block types...");
                loadAllBlockTypes();
            } else {
                throw new IllegalStateException(
                    String.format("Not enough block types available to select %d targets.", targetCount)
                );
            }
        }

        while (currentTargets.size() < targetCount) {
            int randomIndex = rng.nextInt(blockTypes.size());
            Material randomBlockType = blockTypes.get(randomIndex);
            currentTargets.add(randomBlockType); // no effect on HashSet if already present

            if (shouldPreventRepeatBlocks) {
                blockTypes.remove(randomIndex);
            }
        }
    }

    /**
     * Clear the current target block types.
     */
    public void clearTargets() {
        currentTargets.clear();
    }

    /**
     * Check if there are currently active target block types.
     * @return True if there are active targets, false otherwise.
     */
    public boolean hasCurrentTargets() {
        return !currentTargets.isEmpty();
    }

    /**
     * Check if a block type is one of the current target block types.
     * @param blockType The block material to check.
     * @return True if the block type is a target, false otherwise.
     */
    public boolean isTarget(Material blockType) {
        return currentTargets.contains(blockType);
    }

    /**
     * Handle a player finding one of the current target block types.
     * @param finder The player who found the block type.
     * @param foundBlockType The type of block that was found.
     */
    public void handleFoundBlock(Player finder, Material foundBlockType) {
        String foundBlockDisplayName = Formatting.toTitleCase(foundBlockType.name());
        if (processingFound) {
            plugin.getLogger().warning(
                String.format(
                    "Double count detected and prevented for player %s finding block %s.",
                    finder.getName(), foundBlockDisplayName
                )
            );
            return;
        }
        this.processingFound = true;
        boolean status = plugin.getTeamManager().addPoint(finder);
        if (!status) {
            finder.sendMessage(
                Component.text("You found a target block, but you need to join a team first!", Colors.WARNING)
                    .appendNewline()
                    .append(Component.text("Try ", Colors.DEFAULT))
                    .append(Component.text("/bfteams", Colors.COMMAND))
                    .append(Component.text(" to list teams and ", Colors.DEFAULT))
                    .append(Component.text("/bfteams join <team name>", Colors.COMMAND))
                    .append(Component.text(" to join one.", Colors.DEFAULT))
            );
            this.processingFound = false;
            return;
        }

        selectNewTargets();

        Component playerDisplayName = plugin.getTeamManager().getPlayerDisplayName(finder);
        Component teamDisplayName = plugin.getTeamManager().getPlayerTeam(finder).getDisplayName();
        Component message = createFoundBlockMessage(playerDisplayName, teamDisplayName, foundBlockDisplayName);
        plugin.getServer().sendMessage(message);
        this.processingFound = false;
    }

    /**
     * Helper to create an alert message when a player finds a target block.
     * @param playerDisplayName the name of the player who found the block
     * @param teamDisplayName the name of the team the player belongs to
     * @param foundBlockDisplayName the display name of the found block type
     * @return A Component containing the formatted message.
     */
    private Component createFoundBlockMessage(
        Component playerDisplayName, Component teamDisplayName, String foundBlockDisplayName
    ) {
        Component message =  Component.text("Player ", Colors.HEADER)
            .append(playerDisplayName)
            .append(Component.text(" of team ", Colors.HEADER))
            .append(teamDisplayName)
            .append(Component.text(" has found ", Colors.HEADER))
            .append(Component.text(foundBlockDisplayName, Colors.FOUND_BLOCK))
            .append(Component.text("!", Colors.HEADER))
            .appendNewline();

        if (showScoresOnFound) {
            message = message.append(plugin.getTeamManager().createScoresListMessage()).appendNewline();
        }

        return message.append(Component.text("New target blocks:", Colors.HEADER))
            .appendNewline()
            .append(createTargetListMessage());
    }

    /**
     * Create a component displaying a list of the current target block types.
     * @return A Component containing the target block types.
     */
    public Component createTargetListMessage() {
        if (currentTargets.isEmpty()) {
            return Component.empty();
        }

        Component message = Component.empty();
        for (Material blockType : currentTargets) {
            message = message.append(Component.text("  * ", Colors.DEFAULT))
                .append(Component.text(Formatting.toTitleCase(blockType.name()), Colors.TARGET_BLOCK))
                .appendNewline();
        }
        return message;
    }

}
