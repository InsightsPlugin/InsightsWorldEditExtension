package net.frankheijden.wecompatibility.we7;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import net.frankheijden.wecompatibility.core.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class FAWorldEditExtent extends AbstractDelegateExtent {

    private final Player player;
    private final ExtentDelegate delegate;
    private final WorldEditPlugin plugin;

    public FAWorldEditExtent(WorldEditPlugin plugin, Player player, Extent extent, ExtentDelegate delegate) {
        super(extent);

        this.plugin = plugin;
        this.player = player;
        this.delegate = delegate;
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        CustomBlock customBlock = delegate.setBlock(player, Utils.from(location), BukkitAdapter.adapt(block).getMaterial());
        if (customBlock == null) return super.setBlock(location, block);

        BlockState blockState = BukkitAdapter.adapt(Bukkit.createBlockData(customBlock.getMaterial()));
        super.setBlock(location, blockState);
        return false;
    }

    @Nullable
    @Override
    public Operation commit() {
        Operation o = super.commit();
        delegate.onCommit(player);
        return o;
    }
}
