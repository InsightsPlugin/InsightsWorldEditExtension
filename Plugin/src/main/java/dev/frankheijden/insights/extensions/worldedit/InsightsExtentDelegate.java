package dev.frankheijden.insights.extensions.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.Extent;
import dev.frankheijden.insights.api.InsightsPlugin;
import dev.frankheijden.insights.api.concurrent.storage.Distribution;
import dev.frankheijden.insights.api.concurrent.storage.DistributionStorage;
import dev.frankheijden.insights.api.config.LimitEnvironment;
import dev.frankheijden.insights.api.config.Limits;
import dev.frankheijden.insights.api.config.limits.Limit;
import dev.frankheijden.insights.api.objects.math.Vector3;
import dev.frankheijden.insights.api.reflection.RTileEntityTypes;
import dev.frankheijden.insights.api.utils.ChunkUtils;
import dev.frankheijden.insights.api.utils.EnumUtils;
import dev.frankheijden.insights.api.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class InsightsExtentDelegate implements ExtentDelegate {

    private final InsightsPlugin insights = InsightsPlugin.getInstance();
    private final Limits limits = insights.getLimits();
    private final Settings settings;
    private final World world;
    private final UUID worldUid;
    private final Player player;
    private final Extent extent;
    private final EditSession.Stage stage;
    private final LimitEnvironment env;
    private final Map<String, Boolean> permissionCache = new HashMap<>();
    private final Map<Material, Optional<Limit>> limitCache = new HashMap<>();
    private final Distribution<Material> replacedBlocks = new Distribution<>(new HashMap<>());
    private boolean notified = false;

    public InsightsExtentDelegate(InsightsWorldEditExtension plugin, World world, Player player, Extent extent, EditSession.Stage stage) {
        this.settings = plugin.getSettings();
        this.world = world;
        this.worldUid = world.getUID();
        this.player = player;
        this.extent = extent;
        this.stage = stage;
        this.env = new LimitEnvironment(player, world.getUID());
    }

    private boolean hasPermission(String permission) {
        return permissionCache.computeIfAbsent(permission, player::hasPermission);
    }

    private Optional<Limit> getLimit(Material material) {
        return limitCache.computeIfAbsent(material, m -> limits.getFirstLimit(m, env));
    }

    @Override
    public InsightsBlock setBlock(Player player, Vector3 vector, Material material) {
        boolean replace = getLimit(material).isPresent();
        if (!replace && settings.DISABLE_TILES && RTileEntityTypes.isTileEntity(material) && !hasPermission("insights.worldedit.bypass.tiles")) {
            replace = true;
        } else if (!replace) {
            if (settings.EXTRA_BLOCKED_MATERIALS.contains(material)) {
                if (!settings.EXTRA_BLOCKED_WHITELIST && !hasPermission("insights.worldedit.bypass." + material.name())) {
                    replace = true;
                }
            } else {
                if (settings.EXTRA_BLOCKED_WHITELIST && !hasPermission("insights.worldedit.bypass." + material.name())) {
                    replace = true;
                }
            }
        }

        if (replace) {
            InsightsBlock block = new InsightsBlock(vector, material);
            replacedBlocks.modify(material, 1);
            if (settings.TYPE == Settings.WorldEditType.REPLACEMENT) {
                block.setMaterial(settings.REPLACEMENT_BLOCK);
            }
            return block;
        }
        return null;
    }

    @Override
    public void onChange(Player player, Vector3 vector, Material from, Material to) {
        Consumer<DistributionStorage> storageConsumer = storage -> {
            Distribution<Material> distribution = storage.materials();
            distribution.modify(from, -1);
            distribution.modify(to, 1);
        };
        insights.getWorldStorage().getWorld(worldUid).get(ChunkUtils.getKey(vector.getX(), vector.getZ()))
                .ifPresent(storageConsumer);
        insights.getAddonManager().getRegion(new Location(world, vector.getX(), vector.getY(), vector.getZ()))
                .flatMap(region -> insights.getAddonStorage().get(region.getKey()))
                .ifPresent(storageConsumer);
    }

    @Override
    public void onCommit() {
        int totalCount = replacedBlocks.count(replacedBlocks.keys());
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
