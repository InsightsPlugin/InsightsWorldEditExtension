package net.frankheijden.wecompatibility.we6;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Utils {

    public static Method getBukkitImplAdapter;

    static {
        try {
            getBukkitImplAdapter = WorldEditPlugin.class.getDeclaredMethod("getBukkitImplAdapter");
            getBukkitImplAdapter.setAccessible(true);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public static BukkitImplAdapter getAdapter(WorldEditPlugin plugin) throws InvocationTargetException, IllegalAccessException {
        return (BukkitImplAdapter) getBukkitImplAdapter.invoke(plugin);
    }

    public static net.frankheijden.wecompatibility.core.Vector adapt(Vector v) {
        return new net.frankheijden.wecompatibility.core.Vector(v.getBlockX(), v.getBlockY(), v.getBlockZ());
    }
}
