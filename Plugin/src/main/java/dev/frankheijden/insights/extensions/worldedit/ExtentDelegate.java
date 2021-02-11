package dev.frankheijden.insights.extensions.worldedit;

import dev.frankheijden.insights.api.objects.math.Vector3;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface ExtentDelegate {

    InsightsBlock setBlock(Player player, Vector3 vector, Material material);

    void onChange(Player player, Vector3 vector, Material from, Material to);

    void onCommit();

}
