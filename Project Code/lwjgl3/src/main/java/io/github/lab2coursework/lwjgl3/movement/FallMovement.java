package io.github.lab2coursework.lwjgl3.movement;

import io.github.lab2coursework.lwjgl3.entities.Entity;

/**
 * Strategy: simulates gravity for a dropped block.
 * Velocity increases over time (acceleration) until the block lands.
 */
public class FallMovement extends Movement {

    private static final float GRAVITY = 500f; // px/s²
    private float velocityY = 0f;

    @Override
    public void update(Entity entity, float deltaTime) {
        velocityY -= GRAVITY * deltaTime;
        entity.setY(entity.getY() + velocityY * deltaTime);
    }

    public float getVelocityY() { return velocityY; }
}
