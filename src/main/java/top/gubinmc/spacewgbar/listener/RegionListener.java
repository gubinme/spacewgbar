package top.gubinmc.spacewgbar.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import top.gubinmc.spacewgbar.service.RegionTrackingService;

/**
 * Listens to movement-related events to detect WorldGuard region transitions.
 * <p>
 * WorldGuard does not expose native enter/leave events, so block-level movement is tracked manually.
 */
public final class RegionListener implements Listener {

    private final RegionTrackingService regionTrackingService;

    public RegionListener(RegionTrackingService regionTrackingService) {
        this.regionTrackingService = regionTrackingService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        regionTrackingService.updateRegions(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        regionTrackingService.updateRegions(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        regionTrackingService.evaluateCurrentLocation(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        regionTrackingService.clearPlayer(event.getPlayer());
    }
}
