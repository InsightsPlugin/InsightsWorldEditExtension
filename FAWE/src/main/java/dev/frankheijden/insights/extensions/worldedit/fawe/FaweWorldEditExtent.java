package dev.frankheijden.insights.extensions.worldedit.fawe;

import com.fastasyncworldedit.core.math.MutableBlockVector3;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import dev.frankheijden.insights.extensions.worldedit.ExtentDelegate;
import dev.frankheijden.insights.extensions.worldedit.we.WorldEditExtent;
import org.bukkit.entity.Player;

public class FaweWorldEditExtent extends WorldEditExtent {

    public FaweWorldEditExtent(Extent extent, Player player, EditSession.Stage stage, ExtentDelegate delegate) {
        super(extent, player, stage, delegate);
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(int x, int y, int z, T block) throws WorldEditException {
        return checkBlock(block, MutableBlockVector3.get(x, y, z));
    }
}
