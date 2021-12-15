package dev.frankheijden.insights.extensions.worldedit;

import com.sk89q.worldedit.EditSession;
import dev.frankheijden.insights.api.InsightsPlugin;
import dev.frankheijden.insights.api.concurrent.storage.Distribution;
import dev.frankheijden.insights.api.concurrent.storage.Storage;
import dev.frankheijden.insights.api.config.LimitEnvironment;
import dev.frankheijden.insights.api.config.Limits;
import dev.frankheijden.insights.api.config.limits.Limit;
import dev.frankheijden.insights.api.objects.math.Vector3;
import dev.frankheijden.insights.api.objects.wrappers.ScanObject;
import dev.frankheijden.insights.api.reflection.RTileEntityTypes;
import dev.frankheijden.insights.api.utils.ChunkUtils;
import dev.frankheijden.insights.api.utils.EnumUtils;
import dev.frankheijden.insights.api.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class InsightsExtentDelegate implements ExtentDelegate {

    private final InsightsPlugin insights = InsightsPlugin.getInstance();
    private final Limits limits = insights.getLimits();
    private final Settings settings;
    private final World world;
    private final UUID worldUid;
    private final Player player;
    private final LimitEnvironment env;
    private final Map<String, Boolean> permissionCache = new HashMap<>();
    private final Map<Material, Optional<Limit>> limitCache = new EnumMap<>(Material.class);
    private final Distribution<Material> replacedBlocks = new Distribution<>(new EnumMap<>(Material.class));
    private boolean notified = false;

    public InsightsExtentDelegate(InsightsWorldEditExtension plugin, World world, Player player) {
        this.settings = plugin.getSettings();
        this.world = world;
        this.worldUid = world.getUID();
        this.player = player;
        this.env = new LimitEnvironment(player, world.getName());
    }

    private boolean hasPermission(String permission) {
        return permissionCache.computeIfAbsent(permission, player::hasPermission);
    }

    private Optional<Limit> getLimit(Material material) {
        return limitCache.computeIfAbsent(material, m -> limits.getFirstLimit(m, env));
    }

    @Override
    public InsightsBlock setBlock(Player player, EditSession.Stage stage, Vector3 vector, Material from, Material to) {
        boolean replace = getLimit(to).isPresent();
        if (!replace && settings.DISABLE_TILES && RTileEntityTypes.isTileEntity(to) && !hasPermission("insights.worldedit.bypass.tiles")) {
            replace = true;
        } else if (!replace) {
            if (settings.EXTRA_BLOCKED_MATERIALS.contains(to)) {
                if (!settings.EXTRA_BLOCKED_WHITELIST && !hasPermission("insights.worldedit.bypass." + to.name())) {
                    replace = true;
                }
            } else {
                if (settings.EXTRA_BLOCKED_WHITELIST && !hasPermission("insights.worldedit.bypass." + to.name())) {
                    replace = true;
                }
            }
        }

        if (stage == EditSession.Stage.BEFORE_CHANGE) {
            onChange(vector, from, to);
        }

        if (replace) {
            replacedBlocks.modify(to, 1);
            return new InsightsBlock(vector, settings.TYPE == Settings.WorldEditType.REPLACEMENT ? settings.REPLACEMENT_BLOCK : from);
        }
        return null;
    }

    public void onChange(Vector3 vector, Material from, Material to) {
        Consumer<Storage> storageConsumer = storage -> {
            storage.modify(ScanObject.of(from), -1);
            storage.modify(ScanObject.of(to), 1);
        };
        insights.getWorldStorage().getWorld(worldUid).get(ChunkUtils.getKey(vector.getX(), vector.getZ()))
                .ifPresent(storageConsumer);
        insights.getAddonManager().getRegion(new Location(world, vector.getX(), vector.getY(), vector.getZ()))
                .flatMap(region -> insights.getAddonStorage().get(region.getKey()))
                .ifPresent(storageConsumer);
    }

    @Override
    public void onCommit() {
        Set<Material> keys = replacedBlocks.keys();
        long totalCount = replacedBlocks.count(keys::contains);
        if (notified || totalCount == 0) return;
        notified = true;

        settings.sendMessage(player, "summary.header");
        for (Material material : replacedBlocks.keys()) {
            settings.sendMessage(
                    player,
                    "summary.format",
                    "entry", EnumUtils.pretty(material),
                    "count", StringUtils.pretty(replacedBlocks.count(material))
            );
        }
        if (settings.TYPE == Settings.WorldEditType.REPLACEMENT) {
            settings.sendMessage(
                    player,
                    "summary.replaced",
                    "count", StringUtils.pretty(totalCount),
                    "replacement", EnumUtils.pretty(settings.REPLACEMENT_BLOCK)
            );
        } else {
            settings.sendMessage(player, "summary.unchanged", "count", StringUtils.pretty(totalCount));
        }
        settings.sendMessage(player, "summary.footer");
    }
}
