package net.frankheijden.wecompatibility.we6;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import net.frankheijden.wecompatibility.core.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class WorldEditExtent extends AbstractDelegateExtent {

    private final Player player;
    private final ExtentDelegate delegate;
    private final Map<Vector, Integer> duplicateCatcher;

    public WorldEditExtent(Player player, Extent extent, ExtentDelegate delegate) {
        super(extent);

        this.player = player;
        this.delegate = delegate;
        this.duplicateCatcher = new HashMap<>();
    }

    @Override
    public boolean setBlock(Vector location, BaseBlock block) throws WorldEditException {
        Material from = Material.getMaterial(getBlock(location).getId());
        Material to = Material.getMaterial(block.getId());
        net.frankheijden.wecompatibility.core.Vector vector = Utils.adapt(location);
        CustomBlock customBlock = delegate.setBlock(player, vector, to);

        if (customBlock == null) {
            callChange(player, location, from, to);
            return super.setBlock(location, block);
        }

        BaseBlock replaceBlock = new BaseBlock(customBlock.getMaterial().getId());
        callChange(player, location, from, customBlock.getMaterial());
        super.setBlock(location, replaceBlock);
        return false;
    }

    private void callChange(Player player, Vector vector, Material from, Material to) {
        Integer id = duplicateCatcher.get(vector);
        if (id == null || id != to.getId()) {
            delegate.onChange(player, Utils.adapt(vector), from, to);
            duplicateCatcher.put(vector, to.getId());
        }
    }

    @Override
    protected Operation commitBefore() {
        delegate.onCommit(player);
        return super.commitBefore();
    }
}
