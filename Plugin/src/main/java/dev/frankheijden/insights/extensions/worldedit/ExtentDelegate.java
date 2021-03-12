package dev.frankheijden.insights.extensions.worldedit;

import com.sk89q.worldedit.EditSession;
import dev.frankheijden.insights.api.objects.math.Vector3;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface ExtentDelegate {

    InsightsBlock setBlock(Player player, EditSession.Stage stage, Vector3 vector, Material from, Material to);

    void onCommit();

}
