package io.github.lab2coursework.lwjgl3.movement;

import io.github.lab2coursework.lwjgl3.entities.Entity;

/**
 * Strategy: moves the crane arm left/right from user input within X boundaries.
 */
public class CraneMovement extends Movement {

    private final float minX;
    private final float maxX;
    private boolean movingLeft;
    private boolean movingRight;
    private float direction; // +1 = right, -1 = left, 0 = idle

    public CraneMovement(float minX, float maxX) {
        this.minX = minX;
        this.maxX = maxX;
    }

    /**
     * Applies horizontal movement for this frame using current input flags,
     * then clamps the crane inside configured bounds.
     */
    @Override
    public void update(Entity entity, float deltaTime) {
        if (movingLeft == movingRight) {
            direction = 0f;
        } else {
            direction = movingLeft ? -1f : 1f;
        }

        float nextX = entity.getX() + entity.getSpeed() * direction * deltaTime;
        if (nextX < minX) {
            nextX = minX;
        } else if (nextX > maxX) {
            nextX = maxX;
        }
        entity.setX(nextX);
    }

    /** Sets whether leftward motion should be applied this frame. */
    public void setMovingLeft(boolean movingLeft) { this.movingLeft = movingLeft; }
    /** Sets whether rightward motion should be applied this frame. */
    public void setMovingRight(boolean movingRight) { this.movingRight = movingRight; }

    /** Returns -1, 0, or +1 representing last applied movement direction. */
    public float getDirection() { return direction; }
}
