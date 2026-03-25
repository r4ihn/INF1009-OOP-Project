package io.github.lab2coursework.lwjgl3.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.lab2coursework.lwjgl3.managers.*;
import io.github.lab2coursework.lwjgl3.screens.TitleScreen;

public class GameMaster extends ApplicationAdapter {

    // Managers
    protected IOManager        ioManager;
    protected EntityManager    entityManager;
    protected ScreenManager    screenManager;
    protected CollisionManager collisionManager;
    protected MovementManager  movementManager;
    protected AudioManager audioManager;

    // Engine State
    protected boolean isRunning;
    protected float   deltaTime;

    @Override
    public void create() {
        ioManager        = new IOManager();
        entityManager    = new EntityManager();
        screenManager    = new ScreenManager();
        collisionManager = new CollisionManager();
        audioManager = new AudioManager();

        // Load music and sounds before setting the screen
        audioManager.loadMusic("title_bgm", "title_bgm.mp3");
        audioManager.loadMusic("game_bgm", "game_bgm.mp3");
        audioManager.loadSound("correct", "correct.wav");
        audioManager.loadSound("wrong", "wrong.wav");
        audioManager.loadSound("levelup", "levelup.wav");
        audioManager.loadSound("pause", "pause.wav");

        movementManager  = new MovementManager(entityManager.getEntities());

        isRunning = true;

        // Set initial screen to TitleScreen
        screenManager.set(new TitleScreen(screenManager, audioManager));
    }

    @Override
    public void render() {
        deltaTime = Gdx.graphics.getDeltaTime();
        if (isRunning) {
            screenManager.render(deltaTime);
        }
    }

    @Override // Override method to resize the screen
    public void resize(int width, int height) {
        if (screenManager != null) {
            screenManager.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (screenManager != null) {
            screenManager.dispose();
        }
        if (audioManager != null) {
            audioManager.dispose();
        }
    }
}
