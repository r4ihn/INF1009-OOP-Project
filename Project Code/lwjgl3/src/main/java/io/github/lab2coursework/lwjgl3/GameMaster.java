package io.github.lab2coursework.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

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
    }
    public void start() {
    }
    public void stop() {
    }

    public void update(float deltaTime) {
        entityManager.update(); // entities move first

        float worldW = Gdx.graphics.getWidth();
        float worldH = Gdx.graphics.getHeight();

        collisionManager.keepEntitiesInBounds(entityManager.getEntities(), worldW, worldH);
    }

    public void render() {
    }

    // Cleanup
    public void dispose() {
    }
}
