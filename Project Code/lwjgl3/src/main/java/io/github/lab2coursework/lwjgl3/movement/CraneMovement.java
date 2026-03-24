package io.github.lab2coursework.lwjgl3.movement;

import io.github.lab2coursework.lwjgl3.entities.Entity;

/**
 * Strategy: Allows player to control crane arm movement.
 * This version is decoupled from LibGDX Input to keep engine and game logic separate.
 */
public class CraneMovement extends Movement {

    private final float minX;
    private final float maxX;

    // Control flags updated by the IOManager/Input system
    private boolean movingLeft;
    private boolean movingRight;

    public CraneMovement(float minX, float maxX) {
        this.minX = minX;
        this.maxX = maxX;
    }

    public void setMovingLeft(boolean active) { this.movingLeft = active; }
    public void setMovingRight(boolean active) { this.movingRight = active; }

    @Override
    public void update(Entity entity, float deltaTime) {

        if (movingLeft) {
            LeftMovement(entity, deltaTime);
        }
        if (movingRight) {
            RightMovement(entity, deltaTime);
        }

        // 2. Perform boundary clamping
        clampPosition(entity);
    }

    private void clampPosition(Entity entity) {
        if (entity.getX() < minX) {
            entity.setX(minX);
        } else if (entity.getX() > maxX) {
            entity.setX(maxX);
        }
    }
}
