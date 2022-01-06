package dev.frankheijden.insights.extensions.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import dev.frankheijden.insights.api.objects.math.Vector3;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class InsightsAbstractDelegateExtent<V> extends AbstractDelegateExtent {

    protected final Player player;
    protected final EditSession.Stage stage;
    protected final ExtentDelegate delegate;

    protected InsightsAbstractDelegateExtent(Extent extent, Player player, EditSession.Stage stage, ExtentDelegate delegate) {
        super(extent);
        this.player = player;
        this.stage = stage;
        this.delegate = delegate;
    }

    protected abstract <T extends BlockStateHolder<T>> Material getMaterial(T block);

    protected abstract Material getMaterialFromVector(V vector);

    protected abstract <T extends BlockStateHolder<T>> boolean setBlockInternal(V vector, T block) throws WorldEditException;

    protected abstract boolean setBlockInternal(V vector, Material material) throws WorldEditException;

    protected abstract Vector3 createInsightsVector(V vector);

    protected <T extends BlockStateHolder<T>> boolean checkBlock(T block, V vector) throws WorldEditException {
        Material from = getMaterialFromVector(vector);
        Material to = getMaterial(block);
        Vector3 vec3 = createInsightsVector(vector);
        InsightsBlock insightsBlock = delegate.setBlock(player, stage, vec3, from, to);

        if (insightsBlock == null) {
            return setBlockInternal(vector, block);
        }
        return setBlockInternal(vector, insightsBlock.getMaterial());
    }

    @Override
    protected Operation commitBefore() {
        if (stage == EditSession.Stage.BEFORE_HISTORY) {
            delegate.onCommit();
        }
        return super.commitBefore();
    }
}
