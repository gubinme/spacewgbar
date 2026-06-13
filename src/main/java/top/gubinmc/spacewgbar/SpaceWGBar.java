package top.gubinmc.spacewgbar;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import top.gubinmc.spacewgbar.command.SpaceWGBarCommand;
import top.gubinmc.spacewgbar.config.LangConfig;
import top.gubinmc.spacewgbar.config.PluginConfig;
import top.gubinmc.spacewgbar.integration.PlaceholderHook;
import top.gubinmc.spacewgbar.integration.WorldGuardHook;
import top.gubinmc.spacewgbar.listener.RegionListener;
import top.gubinmc.spacewgbar.service.ActionBarService;
import top.gubinmc.spacewgbar.service.RegionTrackingService;
import top.gubinmc.spacewgbar.util.TextParser;

/**
 * Displays persistent ActionBar messages when players enter configured WorldGuard regions.
 */
public final class SpaceWGBar extends JavaPlugin {

    private TextParser textParser;
    private PluginConfig pluginConfig;
    private LangConfig langConfig;
    private WorldGuardHook worldGuardHook;
    private PlaceholderHook placeholderHook;
    private ActionBarService actionBarService;
    private RegionTrackingService regionTrackingService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("lang.yml", false);

        textParser = new TextParser();
        pluginConfig = new PluginConfig(this);
        langConfig = new LangConfig(this, textParser);

        if (!loadConfiguration()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        worldGuardHook = new WorldGuardHook(this);
        if (!worldGuardHook.isEnabled()) {
            getLogger().severe("WorldGuard is required but was not found. Disabling SpaceWGBar.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        placeholderHook = new PlaceholderHook(this);
        if (placeholderHook.isEnabled()) {
            getLogger().info("PlaceholderAPI integration enabled.");
        }

        actionBarService = new ActionBarService(this, pluginConfig, placeholderHook, textParser);
        regionTrackingService = new RegionTrackingService(pluginConfig, worldGuardHook, actionBarService);

        actionBarService.start();
        registerListeners();
        registerCommands();

        Bukkit.getOnlinePlayers().forEach(regionTrackingService::evaluateCurrentLocation);
        getLogger().info("SpaceWGBar enabled.");
    }

    @Override
    public void onDisable() {
        if (actionBarService != null) {
            actionBarService.shutdown();
        }
        getLogger().info("SpaceWGBar disabled.");
    }

    /**
     * Reloads configuration files and refreshes active ActionBar sessions.
     *
     * @return {@code true} when reload completed successfully
     */
    public boolean reloadPlugin() {
        if (!loadConfiguration()) {
            return false;
        }

        actionBarService.reload();
        Bukkit.getOnlinePlayers().forEach(regionTrackingService::evaluateCurrentLocation);
        return true;
    }

    private boolean loadConfiguration() {
        try {
            pluginConfig.reload();
            langConfig.reload();
            return true;
        } catch (Exception exception) {
            getLogger().severe("Failed to load configuration: " + exception.getMessage());
            return false;
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new RegionListener(regionTrackingService), this);
    }

    private void registerCommands() {
        SpaceWGBarCommand commandExecutor = new SpaceWGBarCommand(this, langConfig);
        var command = getCommand("spacewgbar");
        if (command == null) {
            getLogger().warning("Command 'spacewgbar' is not defined in plugin.yml.");
            return;
        }
        command.setExecutor(commandExecutor);
        command.setTabCompleter(commandExecutor);
    }
}
