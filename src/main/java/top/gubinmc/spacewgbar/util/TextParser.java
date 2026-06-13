package top.gubinmc.spacewgbar.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Converts raw configuration strings into Adventure {@link Component} instances.
 */
public final class TextParser {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_AMPERSAND =
            LegacyComponentSerializer.legacyAmpersand();

    /**
     * Parses MiniMessage when the input contains angle brackets, otherwise legacy ampersand codes.
     */
    public Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        if (input.indexOf('<') >= 0) {
            return MINI_MESSAGE.deserialize(input);
        }

        return LEGACY_AMPERSAND.deserialize(input);
    }
}
