package net.frankheijden.wecompatibility.core;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface ExtentDelegate {

    SetResult setBlock(Player player, Vector vector, Material material);

    void onCommit(Player player);
}
