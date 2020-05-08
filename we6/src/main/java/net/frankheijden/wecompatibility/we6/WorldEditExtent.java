package net.frankheijden.wecompatibility.we6;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import net.frankheijden.wecompatibility.core.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class WorldEditExtent extends AbstractDelegateExtent {

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
        Material from = adapter.getMaterial(getBlock(location).getId());
        Material to = adapter.getMaterial(block.getId());
        net.frankheijden.wecompatibility.core.Vector vector = Utils.adapt(location);
        CustomBlock customBlock = delegate.setBlock(player, vector, to);

        if (customBlock == null) {
            delegate.onChange(player, vector, from, to);
            return super.setBlock(location, block);
        }

        BaseBlock replaceBlock = new BaseBlock(adapter.getBlockId(customBlock.getMaterial()));
        delegate.onChange(player, vector, from, customBlock.getMaterial());
        super.setBlock(location, replaceBlock);
        return false;
    }

    @Override
    protected Operation commitBefore() {
        delegate.onCommit(player);
        return super.commitBefore();
    }
}
