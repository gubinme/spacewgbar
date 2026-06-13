package top.gubinmc.spacewgbar.integration;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Optional PlaceholderAPI integration.
 */
public final class PlaceholderHook {

    private final boolean enabled;

    public PlaceholderHook(JavaPlugin plugin) {
        this.enabled = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Replaces PlaceholderAPI placeholders in the given text for the player.
     */
    public String apply(Player player, String text) {
        if (!enabled || text == null || text.isEmpty()) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
