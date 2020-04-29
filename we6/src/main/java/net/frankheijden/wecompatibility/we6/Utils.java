package net.frankheijden.wecompatibility.we6;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Utils {

    public static BukkitImplAdapter getAdapter(WorldEditPlugin plugin) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = plugin.getClass().getDeclaredMethod("getBukkitImplAdapter");
        m.setAccessible(true);
        return (BukkitImplAdapter) m.invoke(plugin);
    }

    public static net.frankheijden.wecompatibility.core.Vector adapt(Vector v) {
        return new net.frankheijden.wecompatibility.core.Vector(v.getBlockX(), v.getBlockY(), v.getBlockZ());
    }
}
