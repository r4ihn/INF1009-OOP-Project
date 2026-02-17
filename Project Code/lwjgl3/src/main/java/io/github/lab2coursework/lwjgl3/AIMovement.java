package io.github.lab2coursework.lwjgl3;

public class AIMovement extends Movement {

    @Override
    public void update(Entity entity) {
        DownMovement(entity);

        // Loop when object falls off the bottom of the screen (Y < 0)
        if (entity.getY() < 0) {
            // Reset it back to the top of the screen - using 1920 x 1080
            entity.setY(1080);
        }
    }
}
