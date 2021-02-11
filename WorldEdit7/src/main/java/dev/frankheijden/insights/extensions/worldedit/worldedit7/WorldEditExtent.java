package dev.frankheijden.insights.extensions.worldedit.worldedit7;

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

public class WorldEditExtent<T extends BlockStateHolder<T>> extends InsightsAbstractDelegateExtent<T, BlockVector3> {

    public WorldEditExtent(Extent extent, Player player, EditSession.Stage stage, ExtentDelegate delegate) {
        super(extent, player, stage, delegate);
    }

    @Override
    protected Material getMaterial(T block) {
        return BukkitAdapter.adapt(block.getBlockType());
    }

    @Override
    protected Material getMaterialFromVector(BlockVector3 vector) {
        return BukkitAdapter.adapt(getBlock(vector).getBlockType());
    }

    @Override
    protected boolean setBlockInternal(BlockVector3 vector, T block) throws WorldEditException {
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

    /**
     * WorldEdit.
     */
    @Override
    public <W extends BlockStateHolder<W>> boolean setBlock(BlockVector3 location, W block) throws WorldEditException {
        return checkBlock((T) block, location);
    }

    /**
     * FastAsyncWorldEdit.
     */
    public boolean setBlock(int x, int y, int z, T block) throws WorldEditException {
        return checkBlock(block, BlockVector3.at(x, y, z));
    }
}
