package me.nbernich.blockFinderPlugin.commands;

import me.nbernich.blockFinderPlugin.BlockFinderPlugin;
import me.nbernich.blockFinderPlugin.utils.Colors;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Command to view and set team scores.
 */
public class ScoresCommand implements CommandExecutor {

    private static final Component USAGE = Component.text("Usage: ", Colors.DEFAULT)
        .append(Component.text("/bfscores", Colors.COMMAND))
        .append(Component.text(" to view the scoreboard or ", Colors.DEFAULT))
        .append(Component.text("/bfscores set <team name> <score>", Colors.COMMAND))
        .append(Component.text(" to change a team's score.", Colors.DEFAULT));

    private final BlockFinderPlugin plugin;

    public ScoresCommand(BlockFinderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equals("list"))) {
            sender.sendMessage(
                Component.text("Current scores:", Colors.HEADER)
                    .appendNewline()
                    .append(plugin.getTeamManager().createScoresListMessage())
            );
            return true;
        }

        if (!args[0].equals("set")) {
            sender.sendMessage(USAGE);
            return true;
        }

        if (!sender.isOp()) {
            sender.sendMessage(
                Component.text("You do not have permission to set team scores.", Colors.ERROR)
            );
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage(USAGE);
            return true;
        }

        String teamName = args[1];
        int newScore;
        try {
            newScore = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(
                Component.text("Invalid score. Please provide a valid integer.", Colors.ERROR)
            );
            return true;
        }

        if (newScore < 0) {
            sender.sendMessage(
                Component.text("Invalid score. Score cannot be negative.", Colors.ERROR)
            );
            return true;
        }

        boolean status = plugin.getTeamManager().setScore(teamName, newScore);
        if (!status) {
            sender.sendMessage(
                Component.text("Team not found.", Colors.ERROR)
                    .appendNewline()
                    .append(Component.text("Use ", Colors.DEFAULT))
                    .append(Component.text("/bfteams", Colors.COMMAND))
                    .append(Component.text(" to view teams.", Colors.DEFAULT))
            );
            return true;
        }

        sender.sendMessage(Component.text("Set score successfully.", Colors.SUCCESS));
        return true;
    }

}