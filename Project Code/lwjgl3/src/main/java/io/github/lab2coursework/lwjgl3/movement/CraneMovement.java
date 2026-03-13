package io.github.lab2coursework.lwjgl3.movement;

import io.github.lab2coursework.lwjgl3.entities.Entity;

/**
 * Strategy: bounces the crane arm left and right between two X boundaries.
 */
public class CraneMovement extends Movement {

    private final float minX;
    private final float maxX;
    private float direction = 1f; // +1 = right, -1 = left

    public CraneMovement(float minX, float maxX) {
        this.minX = minX;
        this.maxX = maxX;
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        entity.setX(entity.getX() + entity.getSpeed() * direction * deltaTime);

        if (entity.getX() >= maxX) {
            entity.setX(maxX);
            direction = -1f;
        } else if (entity.getX() <= minX) {
            entity.setX(minX);
            direction = 1f;
        }
    }

    public float getDirection() { return direction; }
}
