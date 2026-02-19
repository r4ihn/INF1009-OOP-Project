package io.github.lab2coursework.lwjgl3.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.lab2coursework.lwjgl3.managers.*;
import io.github.lab2coursework.lwjgl3.screens.TitleScreen;

public class GameMaster extends ApplicationAdapter {

    // Managers
    protected IOManager ioManager;
    protected EntityManager entityManager;
    protected ScreenManager screenManager;
    protected CollisionManager collisionManager;
    protected MovementManager movementManager;

    // Engine State
    protected boolean isRunning;
    protected float deltaTime;


    // Engine Lifecycle
    public void create() {
        ioManager = new IOManager();
        entityManager = new EntityManager();
        screenManager = new ScreenManager();
        collisionManager = new CollisionManager();

        isRunning = true;

        // set initial screen to TitleScreen
        screenManager.set(new TitleScreen(screenManager));
    }


    public void render() {
        deltaTime = Gdx.graphics.getDeltaTime();


        if (isRunning) {
            screenManager.render(deltaTime);
        }
    }

    // Cleanup
    public void dispose() {
        if(screenManager != null){
            screenManager.dispose();
        }
    }
}
