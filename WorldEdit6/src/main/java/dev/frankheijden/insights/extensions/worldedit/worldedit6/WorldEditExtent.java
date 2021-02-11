package dev.frankheijden.insights.extensions.worldedit.worldedit6;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.Extent;
import dev.frankheijden.insights.api.objects.math.Vector3;
import dev.frankheijden.insights.extensions.worldedit.ExtentDelegate;
import dev.frankheijden.insights.extensions.worldedit.InsightsAbstractDelegateExtent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WorldEditExtent extends InsightsAbstractDelegateExtent<BaseBlock, Vector> {

    public WorldEditExtent(Extent extent, Player player, EditSession.Stage stage, ExtentDelegate delegate) {
        super(extent, player, stage, delegate);
    }

    @Override
    protected Material getMaterial(BaseBlock block) {
        return Material.getMaterial(block.getId());
    }

    @Override
    protected Material getMaterialFromVector(Vector vector) {
        return getMaterial(getBlock(vector));
    }

    @Override
    protected boolean setBlockInternal(Vector vector, BaseBlock block) throws WorldEditException {
        return super.setBlock(vector, block);
    }

    @Override
    protected boolean setBlockInternal(Vector vector, Material material) throws WorldEditException {
        return setBlockInternal(vector, new BaseBlock(material.getId()));
    }

    @Override
    protected Vector3 createInsightsVector(Vector vector) {
        return new Vector3(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    /**
     * WorldEdit.
     */
    @Override
    public boolean setBlock(Vector location, BaseBlock block) throws WorldEditException {
        return checkBlock(block, location);
    }

    /**
     * FastAsyncWorldEdit.
     */
    public boolean setBlock(int x, int y, int z, BaseBlock block) throws WorldEditException {
        return checkBlock(block, new Vector(x, y, z));
    }
}
