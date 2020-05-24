package net.frankheijden.wecompatibility.we6;

import com.sk89q.worldedit.Vector;

public class Utils {

    public static net.frankheijden.wecompatibility.core.Vector adapt(Vector v) {
        return new net.frankheijden.wecompatibility.core.Vector(v.getBlockX(), v.getBlockY(), v.getBlockZ());
    }
}
