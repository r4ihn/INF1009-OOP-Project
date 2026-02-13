package io.github.lab2coursework.lwjgl3;

public class AIMovement extends MovementManager {
    @Override
    public void update(Entity entity) {
        moveDown(entity);
        // Loop when droplet falls off the bottom of the screen (Y < 0)
        if (entity.getY() < 0) {

            // Reset it back to the top of the screen - using 1920 x 1080
            entity.setY(1080);
        }
    }
}
