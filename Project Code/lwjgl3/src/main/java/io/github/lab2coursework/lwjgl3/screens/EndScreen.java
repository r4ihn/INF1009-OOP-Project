package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;

public class EndScreen extends AbstractScreen {

    public EndScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            screenManager.set(new GameScreen(screenManager));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            screenManager.set(new TitleScreen(screenManager));
        }
    }

    @Override
    protected void draw(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float cx = WORLD_WIDTH / 2f; // centre of x-axis definition

        batch.begin();
        font.draw(batch, "END SCREEN", cx - 100, WORLD_HEIGHT / 2f + 40);
        font.draw(batch, "Press R to Restart", cx - 100, WORLD_HEIGHT / 2f);
        font.draw(batch, "Press ESC to Title", cx - 100, WORLD_HEIGHT / 2f);
        batch.end();
    }
}
