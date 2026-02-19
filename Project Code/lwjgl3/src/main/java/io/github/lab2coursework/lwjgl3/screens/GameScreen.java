package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.lab2coursework.lwjgl3.*;
import io.github.lab2coursework.lwjgl3.entities.Circle;
import io.github.lab2coursework.lwjgl3.entities.Triangle;
import io.github.lab2coursework.lwjgl3.graphics.TextureObject;
import io.github.lab2coursework.lwjgl3.managers.*;
import io.github.lab2coursework.lwjgl3.movement.PlayerMovement;

public class GameScreen extends AbstractScreen {

    private final IOManager ioManager;
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final MovementManager movementManager;

    private  SpriteBatch spriteBatch;
    private  ShapeRenderer shapeRenderer;

    public GameScreen(ScreenManager screenManager) {
        super(screenManager);

        ioManager = new IOManager();
        entityManager = new EntityManager();
        collisionManager = new CollisionManager();
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Demo entities (same as GameMaster)
        Circle player1 = new Circle(200, 200, 40, Color.CYAN, 240f);
        player1.setMovementStrategy(new PlayerMovement(
            Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D
        ));
        entityManager.addEntities(player1);

        Triangle player2 = new Triangle(500, 200, Color.ORANGE, 240f);
        player2.setMovementStrategy(new PlayerMovement(
            Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT
        ));
        entityManager.addEntities(player2);

        entityManager.addEntities(new TextureObject("libgdx.png", 140, 210, 240f));

        movementManager = new MovementManager(entityManager.getEntities());

    }



    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            screenManager.push(new PauseScreen(screenManager));
        }
        entityManager.update();
        movementManager.updateMovement(delta);

        collisionManager.applyAll(
            entityManager.getEntities(),
            (float) Gdx.graphics.getWidth(),
            (float) Gdx.graphics.getHeight()
        );
    }

    @Override
    protected void draw(float delta) {
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.16f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        entityManager.draw(null, shapeRenderer);
        shapeRenderer.end();

        spriteBatch.begin();
        entityManager.draw(spriteBatch, null);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }
}
