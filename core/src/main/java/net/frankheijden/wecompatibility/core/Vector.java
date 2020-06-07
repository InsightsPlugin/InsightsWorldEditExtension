package net.frankheijden.wecompatibility.core;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class Vector {

    private int x,y,z;

    public Vector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vector of(Location loc) {
        return new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return x == vector.x &&
                y == vector.y &&
                z == vector.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
