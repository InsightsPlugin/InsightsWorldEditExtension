package dev.frankheijden.insights.extensions.worldedit;

import dev.frankheijden.insights.api.objects.math.Vector3;
import org.bukkit.Material;

public class InsightsBlock {
    private Vector3 vector;
    private Material material;

    public InsightsBlock(Vector3 vector, Material material) {
        this.vector = vector;
        this.material = material;
    }

    public Vector3 getVector() {
        return vector;
    }

    public void setVector(Vector3 vector) {
        this.vector = vector;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
