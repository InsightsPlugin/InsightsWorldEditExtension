package dev.frankheijden.insights.extensions.worldedit;

import dev.frankheijden.insights.api.config.Monad;
import dev.frankheijden.insights.api.config.parser.PassiveYamlParser;
import dev.frankheijden.insights.api.config.parser.YamlParser;
import dev.frankheijden.insights.api.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Settings {

    public final WorldEditType TYPE;
    public final Material REPLACEMENT_BLOCK;
    public final boolean USE_LIMITS;
    public final boolean DISABLE_TILES;
    public final boolean EXTRA_BLOCKED_WHITELIST;
    public final Set<Material> EXTRA_BLOCKED_MATERIALS;
    private final YamlParser parser;

    private Settings(YamlParser parser) {
        TYPE = parser.getEnum("settings.type", WorldEditType.REPLACEMENT);
        REPLACEMENT_BLOCK = parser.getEnum("settings.replacement-block", Material.BEDROCK);
        USE_LIMITS = parser.getBoolean("settings.use-limits", true);
        DISABLE_TILES = parser.getBoolean("settings.disable-tiles", true);
        EXTRA_BLOCKED_WHITELIST = parser.getBoolean("settings.extra-blocked.whitelist", false);
        List<Material> extraBlockedMaterials = parser.getEnums("settings.extra-blocked.materials", Material.class, "materials");
        EXTRA_BLOCKED_MATERIALS = extraBlockedMaterials.isEmpty() ? Collections.emptySet() : EnumSet.copyOf(extraBlockedMaterials);
        this.parser = parser;
    }

    public static Monad<Settings> load(File file, InputStream defaultSettings) throws IOException {
        PassiveYamlParser parser = PassiveYamlParser.load(file, defaultSettings);
        Settings settings = new Settings(parser);
        return parser.toMonad(settings);
    }

    public void sendMessage(Player player, String path, String... replacements) {
        getMessage(path, replacements).ifPresent(player::sendMessage);
    }

    public Optional<String> getMessage(String path, String... replacements) {
        return getRawMessage(path)
                .filter(str -> !str.isEmpty())
                .map(str -> StringUtils.replace(str, replacements))
                .map(str -> ChatColor.translateAlternateColorCodes('&', str));
    }

    public Optional<String> getRawMessage(String path) {
        return Optional.ofNullable(parser.getString("messages." + path, null, false));
    }

    public enum WorldEditType {
        REPLACEMENT,
        UNCHANGED
    }
}
