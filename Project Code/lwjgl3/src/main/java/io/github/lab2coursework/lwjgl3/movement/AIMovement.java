package io.github.lab2coursework.lwjgl3.movement;

import io.github.lab2coursework.lwjgl3.entities.Entity;

/**
 * Simple downward AI movement that respawns entities at the top.
 */
public class AIMovement extends Movement {

    @Override
    public void update(Entity entity, float deltaTime) {
        DownMovement(entity,deltaTime);

        // Loop when object falls off the bottom of the screen (Y < 0)
        if (entity.getY() < 0) {
            // Reset it back to the top of the screen - using 1280 x 720
            entity.setY(720);
            if (entity.getSpeed() < 400f){
                entity.setSpeed(entity.getSpeed() * 1.5f);
            }
        }
    }
}
