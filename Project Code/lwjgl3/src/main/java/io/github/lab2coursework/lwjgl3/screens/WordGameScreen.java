package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;
import io.github.lab2coursework.lwjgl3.wordgame.WordBank;

public class WordGameScreen extends AbstractScreen {
    private final WordGameController controller;
    private final WordGameRenderer renderer;

    public WordGameScreen(ScreenManager screenManager, WordBank wordBank) {
        super(screenManager);
        this.controller = new WordGameController(screenManager, viewport, wordBank);
        this.renderer = new WordGameRenderer(controller);
    }

    @Override
    public void show() {
        super.show();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        controller.onShow();
    }

    @Override
    protected void update(float delta) {
        controller.update(delta);
    }

    @Override
    protected void draw(float delta) {
        renderer.draw(WORLD_WIDTH, WORLD_HEIGHT, batch, font, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        super.dispose();
        renderer.dispose();
    }
}
