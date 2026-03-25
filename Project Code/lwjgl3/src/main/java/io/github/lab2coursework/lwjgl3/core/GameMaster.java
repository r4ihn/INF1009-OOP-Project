package io.github.lab2coursework.lwjgl3.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.lab2coursework.lwjgl3.managers.*;
import io.github.lab2coursework.lwjgl3.screens.TitleScreen;

/**
 * Main LibGDX application entry point for the desktop module.
 * It wires engine managers once, then forwards frame events to the active screen.
 */
public class GameMaster extends ApplicationAdapter {

    // Managers
    protected IOManager        ioManager;
    protected EntityManager    entityManager;
    protected ScreenManager    screenManager;
    protected CollisionManager collisionManager;
    protected MovementManager  movementManager;   // FIX: now properly instantiated

    // Engine State
    protected boolean isRunning;
    protected float   deltaTime;

    @Override
    public void create() {
        // Build shared services before any screen starts using them.
        ioManager        = new IOManager();
        entityManager    = new EntityManager();
        screenManager    = new ScreenManager();
        collisionManager = new CollisionManager();

        // FIX: instantiate with the engine-level entity list.
        // Each Screen also creates its own local EntityManager + MovementManager,
        // so this engine-level one is a fallback / extension point.
        movementManager  = new MovementManager(entityManager.getEntities());

        isRunning = true;

        // Set initial screen to TitleScreen
        screenManager.set(new TitleScreen(screenManager));
    }

    @Override
    public void render() {
        // Keep delta time centralized so the active screen gets consistent frame timing.
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
    }
}
