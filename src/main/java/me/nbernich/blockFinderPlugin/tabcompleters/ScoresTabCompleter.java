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
 * Tab completer to suggest arguments for the /bfscores command.
 */
public class ScoresTabCompleter implements TabCompleter {

    private final BlockFinderPlugin plugin;

    public ScoresTabCompleter(BlockFinderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args
    ) {
        ArrayList<String> completions = new ArrayList<>();

        if (args.length == 1) { // working on first argument
            completions.add("list");
            if (sender.isOp()) {
                completions.add("set");
            }
            return completions;
        }

        if (!sender.isOp() || !args[0].equals("set")) {
            return completions;
        }

        if (args.length == 2) { // just "set", waiting for team name
            completions.addAll(plugin.getTeamManager().getTeamNames());
        }

        return completions; // "set <team name>" takes any integer as the next argument. show no further completions
    }
}
