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
            screenManager.set(new TitleScreen(screenManager));
        }
    }

    @Override
    protected void draw(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "PAUSED", 100, 300);
        font.draw(batch, "Press P to Resume", 100, 260);
        font.draw(batch, "Press ESC to Title", 100, 230);
        batch.end();
    }
}
