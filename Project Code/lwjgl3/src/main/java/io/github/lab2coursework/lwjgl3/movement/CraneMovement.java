package io.github.lab2coursework.lwjgl3.movement;

import io.github.lab2coursework.lwjgl3.entities.Entity;

/**
 * Strategy: bounces the crane arm left and right between two X boundaries.
 */
public class CraneMovement extends Movement {

    // ======= Instantiations start here =======
    // Instantiations for the left and right screen boundaries for the crane
    private final float minX;
    private final float maxX;

    // Instantiations for user controlled left and right movements
    private boolean movingLeft;
    private boolean movingRight;
    private float direction = 0f; // +1 = right, -1 = left

    // ======== Methods start here =======
    public CraneMovement(float minX, float maxX) {
        this.minX = minX;
        this.maxX = maxX;
    }

    public void setMovingLeft(boolean movingLeft) { // Setter method for moving left
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) { // Setter method for moving right
        this.movingRight = movingRight;
    }

    @Override
    public void update(Entity entity, float deltaTime) { // Update method to move the crane arm based on user input and boundaries
        if (movingLeft == movingRight) { // If both buttons are pressed then no movements
            direction = 0f;
        } else {
            direction = movingLeft ? -1f : 1f;
        }

        // Logic to calculate the speed and direction of the crane
        float nextX = entity.getX() + entity.getSpeed() * direction * deltaTime;
        // Setting the boundaries for the crane movement
        if (nextX > maxX) nextX = maxX;
        if (nextX < minX) nextX = minX;
        entity.setX(nextX);
    }

    public float getDirection() { return direction; }
}
