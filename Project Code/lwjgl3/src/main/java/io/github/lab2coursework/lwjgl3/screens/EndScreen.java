package lwjgl3.src.main.java.io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import io.github.lab2coursework.lwjgl3.ScreenManager;
import lwjgl3.src.main.java.io.github.lab2coursework.lwjgl3.managers.ScreenManager;

public class EndScreen extends io.github.lab2coursework.lwjgl3.screens.AbstractScreen {

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

        batch.begin();
        font.draw(batch, "END SCREEN", 100, 300);
        font.draw(batch, "Press R to Restart", 100, 260);
        font.draw(batch, "Press ESC to Title", 100, 230);
        batch.end();
    }
}
