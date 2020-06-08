package net.frankheijden.wecompatibility.we7;

import com.sk89q.worldedit.EditSession;
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
    private final EditSession.Stage stage;

    public WorldEditExtent(Player player, Extent extent, EditSession.Stage stage, ExtentDelegate delegate) {
        super(extent);

        this.player = player;
        this.delegate = delegate;
        this.stage = stage;
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        return checkBlock(location, block);
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(int x, int y, int z, T block) throws WorldEditException {
        return checkBlock(BlockVector3.at(x, y, z), block);
    }

    private <T extends BlockStateHolder<T>> boolean checkBlock(BlockVector3 location, T block) {
        Material from = BukkitAdapter.adapt(getBlock(location)).getMaterial();
        Material to = BukkitAdapter.adapt(block).getMaterial();
        Vector vector = Utils.from(location);
        CustomBlock customBlock = delegate.setBlock(player, vector, to);
        if (customBlock == null) {
            callChange(player, vector, from, to);
            return super.setBlock(location, block);
        }

        BlockState blockState = BukkitAdapter.adapt(Bukkit.createBlockData(customBlock.getMaterial()));
        callChange(player, vector, from, to);
        return super.setBlock(location, blockState);
    }

    private void callChange(Player player, Vector vector, Material from, Material to) {
        if (stage == EditSession.Stage.BEFORE_CHANGE) {
            delegate.onChange(player, vector, from, to);
        }
    }

    @Override
    protected Operation commitBefore() {
        if (stage == EditSession.Stage.BEFORE_HISTORY) {
            delegate.onCommit(player);
        }
        return super.commitBefore();
    }
}
