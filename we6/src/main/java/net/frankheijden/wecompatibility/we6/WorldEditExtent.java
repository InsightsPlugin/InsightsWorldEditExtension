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
import java.lang.reflect.Method;

public class WorldEditExtent extends AbstractDelegateExtent {

    private final Player player;
    private final ExtentDelegate delegate;
    private BukkitImplAdapter adapter;

    public WorldEditExtent(WorldEditPlugin plugin, Player player, Extent extent, ExtentDelegate delegate) {
        super(extent);

        this.player = player;
        this.delegate = delegate;

        try {
            adapter = getAdapter(plugin);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    private BukkitImplAdapter getAdapter(WorldEditPlugin plugin) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = plugin.getClass().getDeclaredMethod("getBukkitImplAdapter");
        m.setAccessible(true);
        return (BukkitImplAdapter) m.invoke(plugin);
    }

    @Override
    public boolean setBlock(Vector location, BaseBlock block) throws WorldEditException {
        Material material = adapter.getMaterial(block.getId());
        SetResult result = delegate.setBlock(player, adapt(location), material);
        Super superCall = result.getSuperCall();
        if (superCall == null) return result.isResult();
        block.setId(adapter.getBlockId(superCall.getMaterial()));
        return super.setBlock(location, block);
    }

    private net.frankheijden.wecompatibility.core.Vector adapt(Vector v) {
        return new net.frankheijden.wecompatibility.core.Vector(v.getBlockX(), v.getBlockY(), v.getBlockZ());
    }

    @Nullable
    @Override
    public Operation commit() {
        Operation o = super.commit();
        delegate.onCommit(player);
        return o;
    }
}
