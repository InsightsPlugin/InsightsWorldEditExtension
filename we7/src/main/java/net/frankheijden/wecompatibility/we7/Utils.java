package net.frankheijden.wecompatibility.we7;

import com.sk89q.worldedit.math.BlockVector3;
import net.frankheijden.wecompatibility.core.Vector;

public class Utils {

    public static Vector from(BlockVector3 v) {
        return new Vector(v.getX(), v.getY(), v.getZ());
    }
}
