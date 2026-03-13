package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;
import io.github.lab2coursework.lwjgl3.wordgame.WordBank;

public class TitleScreen extends AbstractScreen {

    public TitleScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            screenManager.push(new GameScreen(screenManager));  // original game
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            screenManager.push(new WordGameScreen(screenManager, new WordBank())); // word game
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    protected void draw(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        float screenWidth  = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        font.getData().setScale(1f);
        font.setColor(Color.CYAN);
        font.draw(batch, "OOP SIMULATION", screenWidth / 2 - 75, screenHeight - 200);

        font.setColor(Color.WHITE);
        font.draw(batch, ">> Press ENTER to Start Rain Game <<", screenWidth / 2 - 140, 320);

        font.setColor(Color.YELLOW);
        font.draw(batch, ">> Press W to Start Word Builder Game <<", screenWidth / 2 - 155, 280);

        font.setColor(Color.GRAY);
        font.draw(batch, "Press ESCAPE to Exit", screenWidth / 2 - 80, 230);

        batch.end();
    }
}
