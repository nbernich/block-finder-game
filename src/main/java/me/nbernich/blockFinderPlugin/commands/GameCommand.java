package me.nbernich.blockFinderPlugin.commands;

import me.nbernich.blockFinderPlugin.BlockFinderPlugin;
import me.nbernich.blockFinderPlugin.utils.Colors;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Main game command to view targets, start or stop the game, and skip rounds.
 */
public class GameCommand implements CommandExecutor {

    private static final Component USAGE = Component.text("Usage: ", Colors.DEFAULT)
        .append(Component.text("/bfinder", Colors.COMMAND))
        .append(Component.text(" to view current target blocks or ", Colors.DEFAULT))
        .append(Component.text("/bfinder <start | stop | skip>", Colors.COMMAND))
        .append(Component.text(" to control the game.", Colors.DEFAULT));

    private final BlockFinderPlugin plugin;

    public GameCommand(BlockFinderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            handleTargetInfo(sender);
            return true;
        }

        switch (args[0]) {
            case "targets" -> handleTargetInfo(sender);
            case "start" -> handleStart(sender);
            case "stop" -> handleStop(sender);
            case "skip" -> handleSkip(sender);
            case "help" -> handleHelp(sender);
            default -> sender.sendMessage(USAGE);
        }
        return true;
    }

    /**
     * Handle a request to list target block types.
     * @param sender The command sender, typically a player or console.
     */
    private void handleTargetInfo(CommandSender sender) {
        if (!plugin.getBlockManager().hasCurrentTargets()) {
            sender.sendMessage(
                Component.text("No active targets.", Colors.WARNING)
                    .appendNewline()
                    .append(Component.text("Use ", Colors.DEFAULT))
                    .append(Component.text("/bfinder help", Colors.COMMAND))
                    .append(Component.text(" to see available commands.", Colors.DEFAULT))
            );
            return;
        }

        sender.sendMessage(
            Component.text("Current target blocks:", Colors.HEADER)
                .appendNewline()
                .append(plugin.getBlockManager().createTargetListMessage())
        );
    }

    /**
     * Handle a request to start the game.
     * @param sender The command sender, typically a player or console.
     */
    private void handleStart(CommandSender sender) {
        if (!sender.isOp()) {
            sender.sendMessage(
                Component.text("You do not have permission to start the game.", Colors.ERROR)
            );
            return;
        }

        if (plugin.getBlockManager().hasCurrentTargets()) {
            sender.sendMessage(
                Component.text("The game is already running.", Colors.ERROR)
            );
            return;
        }

        plugin.getTeamManager().startScoreboardDisplay();
        plugin.getBlockFindListener().start();
        plugin.getBlockManager().selectNewTargets();

        plugin.getServer().sendMessage(
            Component.text("The Block Finder game has started!", Colors.SUCCESS)
                .appendNewline()
                .append(Component.text(
                    "Find and sneak above a target to earn a point.",
                    Colors.DEFAULT
                ))
                .appendNewline()
                .append(Component.text("Use ", Colors.DEFAULT))
                .append(Component.text("/bfinder help", Colors.COMMAND))
                .append(Component.text(" to learn about available commands.", Colors.DEFAULT))
                .appendNewline()
                .appendNewline()
                .append(Component.text("First target blocks:", Colors.HEADER))
                .appendNewline()
                .append(plugin.getBlockManager().createTargetListMessage())
        );
    }

    /**
     * Handle a request to stop the game.
     * @param sender The command sender, typically a player or console.
     */
    private void handleStop(CommandSender sender) {
        if (!sender.isOp()) {
            sender.sendMessage(
                Component.text("You do not have permission to stop the game.", Colors.ERROR)
            );
            return;
        }

        if (!plugin.getBlockManager().hasCurrentTargets()) {
            sender.sendMessage(
                Component.text("The game is not currently running.", Colors.ERROR)
            );
            return;
        }

        plugin.getTeamManager().stopScoreboardDisplay();
        plugin.getBlockFindListener().stop();
        plugin.getBlockManager().clearTargets();

        plugin.getServer().sendMessage(
            Component.text("The Block Finder game has been stopped.", Colors.SUCCESS)
                .appendNewline()
                .appendNewline()
                .append(Component.text("Final scores:", Colors.HEADER))
                .appendNewline()
                .append(plugin.getTeamManager().createScoresListMessage())
        );
        plugin.getTeamManager().resetScores();
    }

    /**
     * Handle a request to skip the current round, selecting new target block types instead.
     * @param sender The command sender, typically a player or console.
     */
    private void handleSkip(CommandSender sender) {
        if (!sender.isOp()) {
            sender.sendMessage(
                Component.text("You do not have permission to skip the round.", Colors.ERROR)
            );
            return;
        }

        if (!plugin.getBlockManager().hasCurrentTargets()) {
            sender.sendMessage(
                Component.text("The game is not currently running.", Colors.ERROR)
            );
            return;
        }

        plugin.getBlockManager().selectNewTargets();

        plugin.getServer().sendMessage(
            Component.text("The current round of Block Finder has been skipped.", Colors.SUCCESS)
                .appendNewline()
                .appendNewline()
                .append(Component.text("New target blocks:", Colors.HEADER))
                .appendNewline()
                .append(plugin.getBlockManager().createTargetListMessage())
        );
    }

    /**
     * Handle a request for help, displaying available commands and their usage.
     * @param sender The command sender, typically a player or console.
     */
    private void handleHelp(CommandSender sender) {
        sender.sendMessage(
            Component.text("Available commands:", Colors.HEADER)
                .appendNewline()
                .append(Component.text("  * ", Colors.DEFAULT))
                .append(Component.text("/bfinder", Colors.COMMAND))
                .append(Component.text( " to view current targets", Colors.DEFAULT))
                .appendNewline()
                .append(Component.text("  * ", Colors.DEFAULT))
                .append(Component.text("/bfteams", Colors.COMMAND))
                .append(Component.text(" to list teams", Colors.DEFAULT))
                .appendNewline()
                .append(Component.text("  * ", Colors.DEFAULT))
                .append(Component.text("/bfteams <join | leave> <team name>", Colors.COMMAND))
                .append(Component.text(" to change your team membership", Colors.DEFAULT))
                .appendNewline()
                .append(Component.text("  * ", Colors.DEFAULT))
                .append(Component.text("/bfscores", Colors.COMMAND))
                .append(Component.text(" to view team scores", Colors.DEFAULT))
        );
    }
}