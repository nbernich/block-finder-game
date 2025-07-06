package me.nbernich.blockFinderPlugin.commands;

import me.nbernich.blockFinderPlugin.BlockFinderPlugin;
import me.nbernich.blockFinderPlugin.game.Team;
import me.nbernich.blockFinderPlugin.utils.Colors;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command to view, join, or leave teams.
 */
public class TeamsCommand implements CommandExecutor {

    private static final Component USAGE = Component.text("Usage: ", Colors.DEFAULT)
        .append(Component.text("/bfteams", Colors.COMMAND))
        .append(Component.text(" to view teams or ", Colors.DEFAULT))
        .append(Component.text("/bfteams <join | leave> <team name>", Colors.COMMAND))
        .append(Component.text(" to join or leave a team.", Colors.DEFAULT));

    private final BlockFinderPlugin plugin;

    public TeamsCommand(BlockFinderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equals("list"))) {
            handleList(sender);
            return true;
        }

        if (args.length == 1 && args[0].equals("leave")) {
            handleLeave(sender);
            return true;
        }

        if (args.length == 2 && args[0].equals("join")) {
            handleJoin(sender, args[1]);
            return true;
        }

        sender.sendMessage(USAGE);
        return true;
    }

    /**
     * Handle a request to list all teams and their memebers.
     * @param sender the command sender, typically a player or console.
     */
    private void handleList(CommandSender sender) {
        sender.sendMessage(
            Component.text("Teams:", Colors.HEADER)
                .appendNewline()
                .append(plugin.getTeamManager().createMemberListMessage())
        );
    }

    /**
     * Handle a request to join a team.
     * @param sender the command sender, who must be a player.
     * @param teamName the name of the team to join.
     */
    private void handleJoin(CommandSender sender, String teamName) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can join teams.", Colors.ERROR));
            return;
        }

        boolean status = plugin.getTeamManager().addPlayer(player, teamName);
        if (!status) {
            player.sendMessage(
                Component.text("Team not found.", Colors.ERROR)
                    .appendNewline()
                    .append(Component.text("Use ", Colors.DEFAULT))
                    .append(Component.text("/bfteams", Colors.COMMAND))
                    .append(Component.text(" to view teams.", Colors.DEFAULT))
            );
            return;
        }

        Team joinedTeam = plugin.getTeamManager().getPlayerTeam(player);
        player.sendMessage(
            Component.text("You have joined team ", Colors.SUCCESS)
                .append(joinedTeam.getDisplayName())
                .append(Component.text(".", Colors.SUCCESS))
        );
    }

    /**
     * Handle a request to leave the current team.
     * @param sender the command sender, who must be a player.
     */
    private void handleLeave(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                Component.text("Only players can leave teams.", Colors.ERROR)
            );
            return;
        }

        boolean status = plugin.getTeamManager().removePlayer(player);
        if (!status) {
            player.sendMessage(
                Component.text("You are not a member of any team.", Colors.ERROR)
            );
            return;
        }

        player.sendMessage(Component.text("You have left your team.", Colors.SUCCESS));
    }
}
