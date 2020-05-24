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

public class WorldEditExtent extends AbstractDelegateExtent {

    private final Player player;
    private final ExtentDelegate delegate;

    public WorldEditExtent(Player player, Extent extent, ExtentDelegate delegate) {
        super(extent);

        this.player = player;
        this.delegate = delegate;
    }

    @Override
    public boolean setBlock(Vector location, BaseBlock block) throws WorldEditException {
        Material from = Material.getMaterial(getBlock(location).getId());
        Material to = Material.getMaterial(block.getId());
        net.frankheijden.wecompatibility.core.Vector vector = Utils.adapt(location);
        CustomBlock customBlock = delegate.setBlock(player, vector, to);

        if (customBlock == null) {
            delegate.onChange(player, vector, from, to);
            return super.setBlock(location, block);
        }

        BaseBlock replaceBlock = new BaseBlock(customBlock.getMaterial().getId());
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
