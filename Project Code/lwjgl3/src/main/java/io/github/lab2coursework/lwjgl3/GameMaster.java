package io.github.lab2coursework.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

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

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // Engine Lifecycle
    public void create() {
        ioManager = new IOManager();
        entityManager = new EntityManager();
        screenManager = new ScreenManager();
        collisionManager = new CollisionManager();

        movementManager = new PlayerMovement(Gdx.input);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        entityManager.addEntity(new Circle(200, 200, 40, Color.CYAN, 2f));
        entityManager.addEntity(new Triangle(500, 200, Color.ORANGE, 2f));
        entityManager.addEntity(new TextureObject("libgdx.png", 140, 210, 0f));

        isRunning = true;
    }

    public void start() {
    }

    public void stop() {
    }

    public void update(float deltaTime) {
        entityManager.update();

        collisionManager.keepEntitiesInBounds(
            entityManager.getEntities(),
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        collisionManager.resolveEntityCollisions(entityManager.getEntities());
    }



    public void render() {
        deltaTime = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.12f, 0.12f, 0.16f, 1f);

        if (isRunning) {
            update(deltaTime);
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        entityManager.drawShapes(shapeRenderer);
        shapeRenderer.end();

        batch.begin();
        entityManager.drawSprites(batch);
        batch.end();
    }

    // Cleanup
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
