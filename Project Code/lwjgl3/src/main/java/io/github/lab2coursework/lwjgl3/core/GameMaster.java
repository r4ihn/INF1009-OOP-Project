package io.github.lab2coursework.lwjgl3.core;

import com.badlogic.gdx.ApplicationAdapter;
import io.github.lab2coursework.lwjgl3.EntityManager;
import io.github.lab2coursework.lwjgl3.MovementManager;
import io.github.lab2coursework.lwjgl3.managers.CollisionManager;
import io.github.lab2coursework.lwjgl3.managers.IOManager;
import io.github.lab2coursework.lwjgl3.managers.ScreenManager;

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
    public void createGame() {
    }
    public void start() {
    }
    public void stop() {
    }

    // Game Loop
    public void update(float deltaTime) {
    }
    public void render() {
    }

    // Cleanup
    public void dispose() {
    }
}
