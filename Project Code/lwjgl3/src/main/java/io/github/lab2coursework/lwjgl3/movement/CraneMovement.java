package io.github.lab2coursework.lwjgl3.movement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.lab2coursework.lwjgl3.entities.Entity;

/**
 * Strategy: allows player to control crane arm movement with A/D or LEFT/RIGHT keys.
 * Replaces the previous auto-bounce behavior with keyboard input.
 */
public class CraneMovement extends Movement {

    private final float minX;
    private final float maxX;
    private final float moveSpeed;

    public CraneMovement(float minX, float maxX) {
        this.minX = minX;
        this.maxX = maxX;
        this.moveSpeed = 300f; // pixels per second
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        float direction = 0f;

        // Check keyboard input (A/LEFT = move left, D/RIGHT = move right)
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            direction = -1f;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            direction = 1f;
        }

        // Update position
        float newX = entity.getX() + moveSpeed * direction * deltaTime;

        // Clamp to boundaries
        if (newX < minX) {
            newX = minX;
        } else if (newX > maxX) {
            newX = maxX;
        }

        entity.setX(newX);
    }
}
