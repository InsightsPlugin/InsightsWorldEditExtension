package net.frankheijden.wecompatibility.we7;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import net.frankheijden.wecompatibility.core.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WorldEditExtent extends AbstractDelegateExtent {

    private final Player player;
    private final ExtentDelegate delegate;

    public WorldEditExtent(Player player, Extent extent, ExtentDelegate delegate) {
        super(extent);

        this.player = player;
        this.delegate = delegate;
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        Material from = BukkitAdapter.adapt(getBlock(location)).getMaterial();
        Material to = BukkitAdapter.adapt(block).getMaterial();
        Vector vector = Utils.from(location);
        CustomBlock customBlock = delegate.setBlock(player, vector, to);
        if (customBlock == null) {
            delegate.onChange(player, vector, from, to);
            return super.setBlock(location, block);
        }

        BlockState blockState = BukkitAdapter.adapt(Bukkit.createBlockData(customBlock.getMaterial()));
        delegate.onChange(player, vector, from, customBlock.getMaterial());
        super.setBlock(location, blockState);
        return false;
    }

    @Override
    protected Operation commitBefore() {
        delegate.onCommit(player);
        return super.commitBefore();
    }
}
