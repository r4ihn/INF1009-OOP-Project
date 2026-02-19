package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;

public class TitleScreen extends AbstractScreen {

    public TitleScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            screenManager.push(new GameScreen(screenManager));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    protected void draw(float delta) {
        // 1. Set background color
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // 2. Center the Title
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Scaling the font for the Title
        font.getData().setScale(1f);
        font.setColor(Color.CYAN);
        font.draw(batch, "OOP SIMULATION", screenWidth / 2 - 75, screenHeight - 200);

        // 3. Stylize the Menu Options
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
        font.draw(batch, ">> Press ENTER to Start <<", screenWidth / 2 - 100, 300);

        font.setColor(Color.GRAY);
        font.draw(batch, "Press ESCAPE to Exit", screenWidth / 2 - 80, 250);

        batch.end();
    }
}
