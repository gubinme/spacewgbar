package top.gubinmc.spacewgbar.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import top.gubinmc.spacewgbar.config.PluginConfig;
import top.gubinmc.spacewgbar.config.RegionBarEntry;
import top.gubinmc.spacewgbar.integration.PlaceholderHook;
import top.gubinmc.spacewgbar.util.TextParser;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Periodically sends ActionBar messages to players inside configured regions.
 * <p>
 * Action bars expire on the client after a short time, so the message is refreshed on a fixed interval.
 */
public final class ActionBarService {

    private final JavaPlugin plugin;
    private final PluginConfig pluginConfig;
    private final PlaceholderHook placeholderHook;
    private final TextParser textParser;
    private final Map<UUID, RegionBarEntry> activeSessions = new ConcurrentHashMap<>();
    private BukkitTask refreshTask;

    public ActionBarService(
            JavaPlugin plugin,
            PluginConfig pluginConfig,
            PlaceholderHook placeholderHook,
            TextParser textParser
    ) {
        this.plugin = plugin;
        this.pluginConfig = pluginConfig;
        this.placeholderHook = placeholderHook;
        this.textParser = textParser;
    }

    public void start() {
        restartTask();
    }

    public void shutdown() {
        if (refreshTask != null) {
            refreshTask.cancel();
            refreshTask = null;
        }

        for (UUID playerId : activeSessions.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendActionBar(net.kyori.adventure.text.Component.empty());
            }
        }
        activeSessions.clear();
    }

    public void reload() {
        restartTask();
        activeSessions.entrySet().removeIf(entry -> {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) {
                return true;
            }
            if (!pluginConfig.findByRegionId(entry.getValue().regionId()).isPresent()) {
                player.sendActionBar(net.kyori.adventure.text.Component.empty());
                return true;
            }
            return false;
        });
    }

    public void activate(Player player, RegionBarEntry entry) {
        RegionBarEntry previous = activeSessions.put(player.getUniqueId(), entry);
        if (previous != null && previous.equals(entry)) {
            return;
        }
        sendActionBar(player, entry);
    }

    public void deactivate(Player player) {
        if (activeSessions.remove(player.getUniqueId()) != null) {
            player.sendActionBar(net.kyori.adventure.text.Component.empty());
        }
    }

    public boolean isActive(Player player) {
        return activeSessions.containsKey(player.getUniqueId());
    }

    private void restartTask() {
        if (refreshTask != null) {
            refreshTask.cancel();
        }

        long interval = pluginConfig.updateIntervalTicks();
        refreshTask = Bukkit.getScheduler().runTaskTimer(plugin, this::refreshAll, interval, interval);
    }

    private void refreshAll() {
        activeSessions.forEach((playerId, entry) -> {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null || !player.isOnline()) {
                activeSessions.remove(playerId);
                return;
            }
            sendActionBar(player, entry);
        });
    }

    private void sendActionBar(Player player, RegionBarEntry entry) {
        String resolved = placeholderHook.apply(player, entry.message());
        player.sendActionBar(textParser.parse(resolved));
    }
}
