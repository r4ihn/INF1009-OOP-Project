package io.github.lab2coursework.lwjgl3;

import com.badlogic.gdx.Input;

// 2. The Player Implementation (From your UML)
public class PlayerMovement extends MovementManager {

    private Input currentInput; // Passed in from IOManager

    public PlayerMovement(Input input) {
        this.currentInput = input;
    }

    @Override
    public void update(Entity entity) {
        // Read the input and apply the simple movement functions
        if (currentInput.isKeyPressed(Input.Keys.W)) {
            moveUp(entity);
        }
        if (currentInput.isKeyPressed(Input.Keys.S)) {
            moveDown(entity);
        }
        if (currentInput.isKeyPressed(Input.Keys.A)) {
            moveLeft(entity);
        }
        if (currentInput.isKeyPressed(Input.Keys.D)) {
            moveRight(entity);
        }
    }

}
