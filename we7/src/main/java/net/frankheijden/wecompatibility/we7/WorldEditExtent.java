package net.frankheijden.wecompatibility.we7;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import net.frankheijden.wecompatibility.core.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class WorldEditExtent extends AbstractDelegateExtent {

    private final Player player;
    private final ExtentDelegate delegate;
    private final WorldEditPlugin plugin;

    public WorldEditExtent(WorldEditPlugin plugin, Player player, Extent extent, ExtentDelegate delegate) {
        super(extent);

        this.plugin = plugin;
        this.player = player;
        this.delegate = delegate;
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        SetResult result = delegate.setBlock(player, from(location), BukkitAdapter.adapt(block).getMaterial());
        Super superCall = result.getSuperCall();
        if (superCall == null) return result.isResult();

        BlockState blockState = BukkitAdapter.adapt(Bukkit.createBlockData(superCall.getMaterial()));
        super.setBlock(location, blockState);
        return result.isResult();
    }

    private Vector from(BlockVector3 v) {
        return new Vector(v.getX(), v.getY(), v.getZ());
    }

    @Nullable
    @Override
    public Operation commit() {
        Operation o = super.commit();
        delegate.onCommit(player);
        return o;
    }
}
