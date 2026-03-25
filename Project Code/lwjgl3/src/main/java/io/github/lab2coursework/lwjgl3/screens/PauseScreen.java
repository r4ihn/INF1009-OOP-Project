package io.github.lab2coursework.lwjgl3.screens;

// Import statements
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;

public class PauseScreen extends AbstractScreen {

    public PauseScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            screenManager.pop(); // return to previous screen
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            screenManager.push(new TitleScreen(screenManager));
        }
    }

    @Override
    protected void draw(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float cx = WORLD_WIDTH  / 2f; // centre of x-axis definition

        batch.begin();
        font.draw(batch, "PAUSED", cx - 45f, WORLD_HEIGHT / 2f + 40);
        font.draw(batch, "Press P to Resume", cx - 90f, WORLD_HEIGHT / 2f);
        font.draw(batch, "Press ESC to Title", cx - 90f, WORLD_HEIGHT / 2f);
        batch.end();
    }
}
