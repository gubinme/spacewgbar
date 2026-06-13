package top.gubinmc.spacewgbar.service;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import top.gubinmc.spacewgbar.config.PluginConfig;
import top.gubinmc.spacewgbar.config.RegionBarEntry;
import top.gubinmc.spacewgbar.integration.WorldGuardHook;

import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tracks configured WorldGuard regions and delegates ActionBar activation to {@link ActionBarService}.
 */
public final class RegionTrackingService {

    private final PluginConfig pluginConfig;
    private final WorldGuardHook worldGuardHook;
    private final ActionBarService actionBarService;

    public RegionTrackingService(
            PluginConfig pluginConfig,
            WorldGuardHook worldGuardHook,
            ActionBarService actionBarService
    ) {
        this.pluginConfig = pluginConfig;
        this.worldGuardHook = worldGuardHook;
        this.actionBarService = actionBarService;
    }

    /**
     * Re-evaluates region membership after a player moves or teleports.
     */
    public void updateRegions(Player player, Location from, Location to) {
        Set<String> previousConfigured = getConfiguredRegionsAt(from);
        Set<String> currentConfigured = getConfiguredRegionsAt(to);

        if (previousConfigured.equals(currentConfigured)) {
            return;
        }

        applyBestRegion(player, currentConfigured);
    }

    /**
     * Evaluates region membership for a player at their current location (join / reload).
     */
    public void evaluateCurrentLocation(Player player) {
        Set<String> currentConfigured = getConfiguredRegionsAt(player.getLocation());
        applyBestRegion(player, currentConfigured);
    }

    public void clearPlayer(Player player) {
        actionBarService.deactivate(player);
    }

    private void applyBestRegion(Player player, Set<String> configuredRegionIds) {
        Optional<RegionBarEntry> bestEntry = configuredRegionIds.stream()
                .map(pluginConfig::findByRegionId)
                .flatMap(Optional::stream)
                .max(Comparator.comparingInt(RegionBarEntry::priority)
                        .thenComparing(entry -> entry.regionId().toLowerCase(Locale.ROOT)));

        if (bestEntry.isPresent()) {
            actionBarService.activate(player, bestEntry.get());
        } else {
            actionBarService.deactivate(player);
        }
    }

    private Set<String> getConfiguredRegionsAt(Location location) {
        return worldGuardHook.getRegionIdsAt(location).stream()
                .filter(regionId -> pluginConfig.findByRegionId(regionId).isPresent())
                .collect(Collectors.toUnmodifiableSet());
    }
}
