package io.github.lab2coursework.lwjgl3.core;

import com.badlogic.gdx.ApplicationListener; // Changed to Interface
import com.badlogic.gdx.Gdx;
import io.github.lab2coursework.lwjgl3.managers.*;
import io.github.lab2coursework.lwjgl3.screens.TitleScreen;

public class GameMaster implements ApplicationListener {

    // Managers as defined in your UML
    protected IOManager ioManager;
    protected EntityManager entityManager;
    protected ScreenManager screenManager;
    protected CollisionManager collisionManager;
    protected MovementManager movementManager;

    // Engine State
    protected boolean isRunning;
    protected float deltaTime;

    @Override
    public void create() {
        // Initialize managers according to UML structure
        ioManager = new IOManager();
        entityManager = new EntityManager();
        screenManager = new ScreenManager();
        collisionManager = new CollisionManager();

        isRunning = true;

        // Set the initial screen to TitleScreen
        screenManager.set(new TitleScreen(screenManager));
    }

    @Override
    public void render() {
        deltaTime = Gdx.graphics.getDeltaTime();

        if (isRunning) {
            // ScreenManager handles update() and draw() for the active screen
            screenManager.render(deltaTime);
        }
    }

    @Override
    public void resize(int width, int height) {
        screenManager.resize(width, height);
    }

    @Override
    public void pause() {
        // Required by ApplicationListener interface
        isRunning = false;
    }

    @Override
    public void resume() {
        // Required by ApplicationListener interface
        isRunning = true;
    }

    @Override
    public void dispose() {
        // Clean up resources
        if (screenManager != null) {
            screenManager.dispose();
        }
    }
}
