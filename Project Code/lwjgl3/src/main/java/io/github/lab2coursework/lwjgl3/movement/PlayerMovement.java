package io.github.lab2coursework.lwjgl3.movement;
import io.github.lab2coursework.lwjgl3.entities.Entity;

public class PlayerMovement extends Movement {
    private boolean movingUp;
    private boolean movingDown;
    private boolean movingLeft;
    private boolean movingRight;

    // Setters called by IOManager Actions
    public void setMovingUp(boolean active) { this.movingUp = active; }
    public void setMovingDown(boolean active) { this.movingDown = active; }
    public void setMovingLeft(boolean active) { this.movingLeft = active; }
    public void setMovingRight(boolean active) { this.movingRight = active; }

    @Override
    public void update(Entity entity, float deltaTime) {
        // Apply the actual movement based on the action
        if (movingUp) UpMovement(entity, deltaTime);
        if (movingDown) DownMovement(entity, deltaTime);
        if (movingLeft) LeftMovement(entity, deltaTime);
        if (movingRight) RightMovement(entity, deltaTime);
    }
}
