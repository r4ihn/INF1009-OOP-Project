package io.github.lab2coursework.lwjgl3;

import io.github.lab2coursework.lwjgl3.entities.Circle;
import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.Triangle;

public class KeepInBoundsRule implements CollisionRule {

    private final float worldW;
    private final float worldH;

    public KeepInBoundsRule(float worldW, float worldH) {
        this.worldW = worldW;
        this.worldH = worldH;
    }

    @Override
    public boolean matches(Entity a, Entity b) {
        return (a != null && b == null);
    }

    @Override
    public void resolve(Entity a, Entity b) {
        float minX = 0, minY = 0;
        float maxX = worldW, maxY = worldH;

        if (a instanceof Circle) {
            Circle c = (Circle) a;
            float r = c.getRadius();
            minX = r; minY = r;
            maxX = worldW - r;
            maxY = worldH - r;
        } else if (a instanceof Triangle) {
            // Triangle drawn as 100x100 footprint
            minX = 0; minY = 0;
            maxX = worldW - 100f;
            maxY = worldH - 100f;
        }

        a.setX(clamp(a.getX(), minX, maxX));
        a.setY(clamp(a.getY(), minY, maxY));
    }

    private float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
