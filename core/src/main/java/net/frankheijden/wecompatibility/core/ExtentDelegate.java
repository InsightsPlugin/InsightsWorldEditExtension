package net.frankheijden.wecompatibility.core;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface ExtentDelegate {

    void handleStage(String stage);

    CustomBlock setBlock(Player player, Vector vector, Material material);

    void onChange(Player player, Vector vector, Material from, Material to);

    void onCommit(Player player);
}
