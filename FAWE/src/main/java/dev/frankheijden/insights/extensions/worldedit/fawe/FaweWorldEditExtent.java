package dev.frankheijden.insights.extensions.worldedit.fawe;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import dev.frankheijden.insights.extensions.worldedit.ExtentDelegate;
import dev.frankheijden.insights.extensions.worldedit.we.WorldEditExtent;
import org.bukkit.entity.Player;

public class FaweWorldEditExtent<B extends BlockStateHolder<B>> extends WorldEditExtent<B> {

    public FaweWorldEditExtent(Extent extent, Player player, EditSession.Stage stage, ExtentDelegate delegate) {
        super(extent, player, stage, delegate);
    }
}
