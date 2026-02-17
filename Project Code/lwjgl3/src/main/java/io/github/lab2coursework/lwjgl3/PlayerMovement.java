package io.github.lab2coursework.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PlayerMovement extends Movement {

    @Override
    public void update(Entity entity) {
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)) {
            UpMovement(entity);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)) {
            DownMovement(entity);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)) {
            LeftMovement(entity);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) {
            RightMovement(entity);
        }
    }
}
