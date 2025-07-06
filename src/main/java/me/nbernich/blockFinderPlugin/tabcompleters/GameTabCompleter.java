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
 * Tab completer to suggest arguments for the /bfinder command.
 */
public class GameTabCompleter implements TabCompleter {

    private final BlockFinderPlugin plugin;

    public GameTabCompleter(BlockFinderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args
    ) {
        ArrayList<String> completions = new ArrayList<>();
        if (args.length != 1) { // player is not typing the first argument
            return completions;
        }

        completions.add("help");
        completions.add("targets");

        if (!sender.isOp()) {
            return completions;
        }

        if (plugin.getBlockManager().hasCurrentTargets()) {
            completions.add("stop");
            completions.add("skip");
        } else {
            completions.add("start");
        }

        return completions;
    }
}
