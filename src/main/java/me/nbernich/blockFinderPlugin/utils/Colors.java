package me.nbernich.blockFinderPlugin.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Map;

/**
 * Utility class for common color values and color parsing.
 */
public class Colors {

    public static final NamedTextColor HEADER = NamedTextColor.WHITE;
    public static final NamedTextColor DEFAULT = NamedTextColor.GRAY;
    public static final NamedTextColor WARNING = NamedTextColor.YELLOW;
    public static final NamedTextColor SUCCESS = NamedTextColor.GREEN;
    public static final NamedTextColor ERROR = NamedTextColor.RED;
    public static final NamedTextColor COMMAND = NamedTextColor.AQUA;
    public static final NamedTextColor TARGET_BLOCK = NamedTextColor.GOLD;
    public static final NamedTextColor FOUND_BLOCK = NamedTextColor.GOLD;

    private static final Map<String, NamedTextColor> colorMap = NamedTextColor.NAMES.keyToValue();

    /**
     * Get a TextColor from a color string.
     * @param colorString a hex color string or color name (e.g., "RED", "dark_gray", etc.).
     * @return a TextColor corresponding to the provided name.
     */
    public static TextColor parseColor(String colorString) {
        if (colorString == null) {
            return DEFAULT;
        }

        if (colorString.startsWith("#")) {
            return TextColor.fromHexString(colorString);
        }

        return colorMap.getOrDefault(colorString.toLowerCase(), DEFAULT);
    }

}
