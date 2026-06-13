package top.gubinmc.spacewgbar.config;

/**
 * Configuration entry for a WorldGuard region and its ActionBar message.
 *
 * @param regionId WorldGuard region identifier (case-insensitive)
 * @param message  raw message template (MiniMessage / legacy ampersand codes)
 * @param priority higher value wins when the player is inside multiple configured regions
 */
public record RegionBarEntry(String regionId, String message, int priority) {

    public RegionBarEntry {
        if (regionId == null || regionId.isBlank()) {
            throw new IllegalArgumentException("regionId must not be blank");
        }
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
    }
}
