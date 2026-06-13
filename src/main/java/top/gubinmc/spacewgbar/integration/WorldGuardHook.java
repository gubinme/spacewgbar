package top.gubinmc.spacewgbar.integration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Thin wrapper around the WorldGuard region query API.
 */
public final class WorldGuardHook {

    private final JavaPlugin plugin;
    private final boolean enabled;

    public WorldGuardHook(JavaPlugin plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns lowercase WorldGuard region IDs at the given location.
     * The global region is excluded.
     */
    public Set<String> getRegionIdsAt(Location location) {
        if (!enabled || location == null || location.getWorld() == null) {
            return Set.of();
        }

        try {
            RegionQuery query = WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .createQuery();

            ApplicableRegionSet regions = query.getApplicableRegions(BukkitAdapter.adapt(location));
            Set<String> ids = new HashSet<>();
            for (ProtectedRegion region : regions) {
                if (region.getId().equalsIgnoreCase("__global__")) {
                    continue;
                }
                ids.add(region.getId().toLowerCase(Locale.ROOT));
            }
            return Set.copyOf(ids);
        } catch (Exception exception) {
            plugin.getLogger().warning("Failed to query WorldGuard regions: " + exception.getMessage());
            return Set.of();
        }
    }
}
