package me.nbernich.blockFinderPlugin.tabcompleters;

import me.nbernich.blockFinderPlugin.BlockFinderPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab completer to suggest arguments for the /bfteams command.
 */
public class TeamsTabCompleter implements TabCompleter {

    private final BlockFinderPlugin plugin;

    public TeamsTabCompleter(BlockFinderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args
    ) {
        ArrayList<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("list");
            completions.add("join");
            completions.add("leave");
            return completions;
        }

        if (args.length == 2 && args[0].equals("join")) { // working on argument after "join"
            completions.addAll(plugin.getTeamManager().getTeamNames());
        }

        return completions;
    }
}
