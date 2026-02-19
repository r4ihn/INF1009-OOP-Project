package io.github.lab2coursework.lwjgl3;

import com.badlogic.gdx.Gdx;
import io.github.lab2coursework.lwjgl3.movement.Movement;

public class PlayerMovement extends Movement {
    // Variables to store which keys this specific player uses
    private int upKey;
    private int downKey;
    private int leftKey;
    private int rightKey;

    //Reference to input system
    private IOManager ioManager;

    // The Constructor: We assign the keys when we create the strategy
    public PlayerMovement(int upKey, int downKey, int leftKey, int rightKey) {
        this.upKey = upKey;
        this.downKey = downKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        // Now it checks the variables instead of hardcoded keys!
        if (Gdx.input.isKeyPressed(upKey)) {
            UpMovement(entity, deltaTime);
        }
        if (Gdx.input.isKeyPressed(downKey)) {
            DownMovement(entity, deltaTime);
        }
        if (Gdx.input.isKeyPressed(leftKey)) {
            LeftMovement(entity, deltaTime);
        }
        if (Gdx.input.isKeyPressed(rightKey)) {
            RightMovement(entity, deltaTime);
        }
    }
}
