package top.gubinmc.spacewgbar.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Loads and exposes plugin settings from {@code config.yml}.
 */
public final class PluginConfig {

    private static final String REGIONS_PATH = "regions";
    private static final String UPDATE_INTERVAL_PATH = "settings.update-interval-ticks";

    private final JavaPlugin plugin;
    private int updateIntervalTicks;
    private Map<String, RegionBarEntry> regionsById = Map.of();

    public PluginConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        updateIntervalTicks = Math.max(1, config.getInt(UPDATE_INTERVAL_PATH, 20));
        regionsById = loadRegions(config.getConfigurationSection(REGIONS_PATH));
    }

    public int updateIntervalTicks() {
        return updateIntervalTicks;
    }

    public Map<String, RegionBarEntry> regionsById() {
        return regionsById;
    }

    public Optional<RegionBarEntry> findByRegionId(String regionId) {
        if (regionId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(regionsById.get(regionId.toLowerCase(Locale.ROOT)));
    }

    private Map<String, RegionBarEntry> loadRegions(ConfigurationSection section) {
        if (section == null) {
            plugin.getLogger().warning("No regions configured in config.yml (section 'regions' is missing).");
            return Map.of();
        }

        Map<String, RegionBarEntry> loaded = new HashMap<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection regionSection = section.getConfigurationSection(key);
            if (regionSection == null) {
                plugin.getLogger().warning("Skipping invalid region entry: " + key);
                continue;
            }

            String message = regionSection.getString("message", "");
            if (message.isBlank()) {
                plugin.getLogger().warning("Region '" + key + "' has an empty message and will be ignored.");
                continue;
            }

            int priority = regionSection.getInt("priority", 0);
            String regionId = key.toLowerCase(Locale.ROOT);

            try {
                loaded.put(regionId, new RegionBarEntry(regionId, message, priority));
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning("Skipping region '" + key + "': " + exception.getMessage());
            }
        }

        if (loaded.isEmpty()) {
            plugin.getLogger().warning("No valid region entries were loaded from config.yml.");
        }

        return Collections.unmodifiableMap(loaded);
    }
}
