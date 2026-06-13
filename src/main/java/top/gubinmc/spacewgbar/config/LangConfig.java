package top.gubinmc.spacewgbar.config;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import top.gubinmc.spacewgbar.util.TextParser;

import java.io.File;
import java.util.Objects;

/**
 * Loads localized player-facing messages from {@code lang.yml}.
 */
public final class LangConfig {

    private static final String MESSAGES_PATH = "messages.";

    private final JavaPlugin plugin;
    private final TextParser textParser;
    private FileConfiguration lang;

    public LangConfig(JavaPlugin plugin, TextParser textParser) {
        this.plugin = plugin;
        this.textParser = textParser;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "lang.yml");
        if (!file.exists()) {
            plugin.saveResource("lang.yml", false);
        }
        lang = YamlConfiguration.loadConfiguration(file);
    }

    public Component message(String key) {
        String raw = lang.getString(MESSAGES_PATH + key, "<red>Missing message: " + key);
        return textParser.parse(Objects.requireNonNullElse(raw, ""));
    }

    public Component prefixedMessage(String key) {
        String prefix = lang.getString("prefix", "");
        Component prefixComponent = textParser.parse(Objects.requireNonNullElse(prefix, ""));
        return prefixComponent.append(message(key));
    }

    public Component prefixedMessage(String key, String placeholder, String value) {
        String prefix = lang.getString("prefix", "");
        Component prefixComponent = textParser.parse(Objects.requireNonNullElse(prefix, ""));
        return prefixComponent.append(message(key, placeholder, value));
    }

    public Component message(String key, String placeholder, String value) {
        String raw = lang.getString(MESSAGES_PATH + key, "<red>Missing message: " + key);
        String resolved = Objects.requireNonNullElse(raw, "").replace(placeholder, value);
        return textParser.parse(resolved);
    }
}
