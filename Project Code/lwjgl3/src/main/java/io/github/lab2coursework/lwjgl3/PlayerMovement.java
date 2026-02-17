package io.github.lab2coursework.lwjgl3;

import com.badlogic.gdx.Gdx;

public class PlayerMovement extends Movement {
    // Variables to store which keys this specific player uses
    private int upKey;
    private int downKey;
    private int leftKey;
    private int rightKey;

    // The Constructor: We assign the keys when we create the strategy
    public PlayerMovement(int upKey, int downKey, int leftKey, int rightKey) {
        this.upKey = upKey;
        this.downKey = downKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
    }

    @Override
    public void update(Entity entity) {
        // Now it checks the variables instead of hardcoded keys!
        if (Gdx.input.isKeyPressed(upKey)) {
            UpMovement(entity);
        }
        if (Gdx.input.isKeyPressed(downKey)) {
            DownMovement(entity);
        }
        if (Gdx.input.isKeyPressed(leftKey)) {
            LeftMovement(entity);
        }
        if (Gdx.input.isKeyPressed(rightKey)) {
            RightMovement(entity);
        }
    }
}
