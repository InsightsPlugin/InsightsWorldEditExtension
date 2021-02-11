package dev.frankheijden.insights.extensions.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import dev.frankheijden.insights.api.objects.math.Vector3;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class InsightsAbstractDelegateExtent<B, V> extends AbstractDelegateExtent {

    protected final Player player;
    protected final EditSession.Stage stage;
    protected final ExtentDelegate delegate;

    protected InsightsAbstractDelegateExtent(Extent extent, Player player, EditSession.Stage stage, ExtentDelegate delegate) {
        super(extent);
        this.player = player;
        this.stage = stage;
        this.delegate = delegate;
    }

    protected abstract Material getMaterial(B block);

    protected abstract Material getMaterialFromVector(V vector);

    protected abstract boolean setBlockInternal(V vector, B block) throws WorldEditException;

    protected abstract boolean setBlockInternal(V vector, Material material) throws WorldEditException;

    protected abstract Vector3 createInsightsVector(V vector);

    protected boolean checkBlock(B block, V vector) throws WorldEditException {
        Material from = getMaterialFromVector(vector);
        Material to = getMaterial(block);
        Vector3 vec3 = createInsightsVector(vector);
        InsightsBlock insightsBlock = delegate.setBlock(player, vec3, to);

        if (insightsBlock == null) {
            tryCallChange(player, vec3, from, to);
            return setBlockInternal(vector, block);
        }

        tryCallChange(player, vec3, from, insightsBlock.getMaterial());
        return setBlockInternal(vector, insightsBlock.getMaterial());
    }

    protected void tryCallChange(Player player, Vector3 vector, Material from, Material to) {
        if (stage == EditSession.Stage.BEFORE_CHANGE) {
            delegate.onChange(player, vector, from, to);
        }
    }

    @Override
    protected Operation commitBefore() {
        if (stage == EditSession.Stage.BEFORE_HISTORY) {
            delegate.onCommit();
        }
        return super.commitBefore();
    }
}
