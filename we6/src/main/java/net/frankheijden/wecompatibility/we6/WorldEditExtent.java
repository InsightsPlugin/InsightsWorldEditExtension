package net.frankheijden.wecompatibility.we6;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import net.frankheijden.wecompatibility.core.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;

public class WorldEditExtent extends IAbstractDelegateExtent {

    private final Player player;
    private final ExtentDelegate delegate;
    private BukkitImplAdapter adapter;

    public WorldEditExtent(WorldEditPlugin plugin, Player player, Extent extent, ExtentDelegate delegate) {
        super(extent);

        this.player = player;
        this.delegate = delegate;

        try {
            adapter = Utils.getAdapter(plugin);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean setBlock(Vector location, BaseBlock block) throws WorldEditException {
        Material material = adapter.getMaterial(block.getId());
        CustomBlock customBlock = delegate.setBlock(player, Utils.adapt(location), material);
        if (customBlock == null) return super.setBlock(location, block);

        BaseBlock baseBlock = new BaseBlock(adapter.getBlockId(customBlock.getMaterial()));
        super.setBlock(location, baseBlock);
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
