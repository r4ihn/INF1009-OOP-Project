package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;
import io.github.lab2coursework.lwjgl3.wordgame.WordBank;

public class WordGameEndScreen extends AbstractScreen {

    private final int levelReached;

    public WordGameEndScreen(ScreenManager screenManager, int levelReached) {
        super(screenManager);
        this.levelReached = levelReached;
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            screenManager.set(new WordGameScreen(screenManager, new WordBank()));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            screenManager.set(new TitleScreen(screenManager));
        }
    }

    @Override
    protected void draw(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.04f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        font.getData().setScale(2f);
        font.setColor(Color.RED);
        font.draw(batch, "GAME OVER", 460, 440);

        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        font.draw(batch, "You reached Level " + levelReached, 480, 370);

        font.getData().setScale(1f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Press R to Play Again", 490, 300);
        font.draw(batch, "Press ESC to Title Screen", 470, 265);

        font.getData().setScale(1f);
        batch.end();
    }
}
