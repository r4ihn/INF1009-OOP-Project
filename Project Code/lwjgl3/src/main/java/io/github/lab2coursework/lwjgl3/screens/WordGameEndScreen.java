package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;
import io.github.lab2coursework.lwjgl3.wordgame.WordBank;

public class WordGameEndScreen extends AbstractScreen {

    private final int levelReached;
    private final Texture background;

    public WordGameEndScreen(ScreenManager screenManager, int levelReached) {
        super(screenManager);
        this.levelReached = levelReached;
        this.background   = new Texture("GameOver.jpg");
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
        batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        font.getData().setScale(1.2f);
        font.setColor(Color.GOLD);
        font.draw(batch, "You reached Level " + levelReached, 540, 185);

        font.getData().setScale(1f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Press R to Play Again", 550, 140);
        font.draw(batch, "Press ESC to Title Screen", 535, 110);

        font.getData().setScale(1f);
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        background.dispose();
    }
}
