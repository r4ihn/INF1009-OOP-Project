package io.github.lab2coursework.lwjgl3;

public abstract class GameMaster {

    // Managers
    protected InterfaceManager interfaceManager;
    protected IOManager ioManager;
    protected EntityManager entityManager;
    protected ScreenManager screenManager;
    protected CollisionManager collisionManager;
    protected MovementManager movementManager;

    // Engine State
    protected boolean isRunning;
    protected float deltaTime;

    // Engine Lifecycle
    public abstract void createGame();
    public abstract void start();
    public abstract void stop();
    
    // Game Loop
    public abstract void update(float deltaTime);
    public abstract void render();

    // Cleanup
    public abstract void dispose();
}