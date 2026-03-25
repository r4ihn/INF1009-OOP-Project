package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Entity;

// Keeps an entity inside the world bounds
public class KeepInBoundsRule implements CollisionRule {

    private final float worldW;
    private final float worldH;

    public KeepInBoundsRule(float worldW, float worldH) {
        this.worldW = worldW;
        this.worldH = worldH;
    }

    @Override
    public boolean matches(Entity a, Entity b) {
        // Used for one entity at a time
        return a != null && b == null;
    }

    @Override
    public void resolve(Entity a, Entity b) {
        // Clamp x and y within the world
        a.setX(clamp(a.getX(), 0f, worldW));
        a.setY(clamp(a.getY(), 0f, worldH));
    }

    // Restrict a value between min and max
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
