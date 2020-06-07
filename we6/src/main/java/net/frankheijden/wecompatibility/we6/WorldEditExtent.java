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
    private final EditSession.Stage stage;

    public WorldEditExtent(Player player, Extent extent, EditSession.Stage stage, ExtentDelegate delegate) {
        super(extent);

        this.player = player;
        this.delegate = delegate;
        this.stage = stage;

        this.delegate.handleStage(stage.name());
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
        if (stage == EditSession.Stage.BEFORE_CHANGE) {
            delegate.onChange(player, Utils.adapt(vector), from, to);
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
