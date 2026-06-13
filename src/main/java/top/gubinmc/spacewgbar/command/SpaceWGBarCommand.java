package top.gubinmc.spacewgbar.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import top.gubinmc.spacewgbar.SpaceWGBar;
import top.gubinmc.spacewgbar.config.LangConfig;

import java.util.Collections;
import java.util.List;

/**
 * Handles {@code /spacewgbar} admin commands.
 */
public final class SpaceWGBarCommand implements CommandExecutor, TabCompleter {

    private static final String RELOAD_PERMISSION = "spacewgbar.reload";

    private final SpaceWGBar plugin;
    private final LangConfig langConfig;

    public SpaceWGBarCommand(SpaceWGBar plugin, LangConfig langConfig) {
        this.plugin = plugin;
        this.langConfig = langConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(usage(label));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(RELOAD_PERMISSION)) {
                sender.sendMessage(langConfig.prefixedMessage("no-permission"));
                return true;
            }

            boolean success = plugin.reloadPlugin();
            sender.sendMessage(langConfig.prefixedMessage(success ? "reload-success" : "reload-failed"));
            return true;
        }

        sender.sendMessage(usage(label));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission(RELOAD_PERMISSION)) {
            return List.of("reload").stream()
                    .filter(option -> option.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return Collections.emptyList();
    }

    private Component usage(String label) {
        return langConfig.prefixedMessage("usage", "{command}", label);
    }
}
