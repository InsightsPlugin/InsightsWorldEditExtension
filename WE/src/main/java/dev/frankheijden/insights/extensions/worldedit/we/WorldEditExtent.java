package dev.frankheijden.insights.extensions.worldedit.we;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import dev.frankheijden.insights.api.objects.math.Vector3;
import dev.frankheijden.insights.extensions.worldedit.ExtentDelegate;
import dev.frankheijden.insights.extensions.worldedit.InsightsAbstractDelegateExtent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WorldEditExtent<B extends BlockStateHolder<B>> extends InsightsAbstractDelegateExtent<B, BlockVector3> {

    public WorldEditExtent(Extent extent, Player player, EditSession.Stage stage, ExtentDelegate delegate) {
        super(extent, player, stage, delegate);
    }

    @Override
    protected Material getMaterial(B block) {
        return BukkitAdapter.adapt(block.getBlockType());
    }

    @Override
    protected Material getMaterialFromVector(BlockVector3 vector) {
        return BukkitAdapter.adapt(getBlock(vector).getBlockType());
    }

    @Override
    protected boolean setBlockInternal(BlockVector3 vector, B block) throws WorldEditException {
        return super.setBlock(vector, block);
    }

    @Override
    protected boolean setBlockInternal(BlockVector3 vector, Material material) throws WorldEditException {
        return super.setBlock(vector, BukkitAdapter.adapt(Bukkit.createBlockData(material)));
    }

    @Override
    protected Vector3 createInsightsVector(BlockVector3 vector) {
        return new Vector3(vector.getX(), vector.getY(), vector.getZ());
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        return checkBlock((B) block, location);
    }
}
