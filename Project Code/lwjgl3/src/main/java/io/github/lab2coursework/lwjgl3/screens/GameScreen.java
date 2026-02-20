package io.github.lab2coursework.lwjgl3.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github.lab2coursework.lwjgl3.*;
import io.github.lab2coursework.lwjgl3.entities.Circle;
import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.Triangle;
import io.github.lab2coursework.lwjgl3.entities.Raindrop;
import io.github.lab2coursework.lwjgl3.graphics.TextureObject;
import io.github.lab2coursework.lwjgl3.input.Key;
import io.github.lab2coursework.lwjgl3.input.KeyboardInput;
import io.github.lab2coursework.lwjgl3.managers.*;
import io.github.lab2coursework.lwjgl3.movement.AIMovement;
import io.github.lab2coursework.lwjgl3.movement.PlayerMovement;

public class GameScreen extends AbstractScreen {

    private final IOManager ioManager;
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final MovementManager movementManager;

    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final Texture backgroundTexture = new Texture("GameBG.jpg");

    public GameScreen(ScreenManager screenManager) {
        super(screenManager);

        ioManager = new IOManager();
        entityManager = new EntityManager();
        collisionManager = new CollisionManager();
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

// Produce Droplets (no array needed)
        for (int i = 0; i < 20; i++) {
            float randomX = MathUtils.random(0, 1280);
            float randomY = MathUtils.random(200, 1000);
            float randomSpeed = MathUtils.random(50, 200);

            Raindrop drop = new Raindrop("droplet.png", randomX, randomY, randomSpeed);
            drop.setMovementStrategy(new AIMovement());
            entityManager.addEntities(drop);
        }

        // Add bucket
        Entity bucket = new TextureObject("bucket.png", 300, 0, 0);
        entityManager.addEntities(bucket);


        // Set Circle as player movement
        Circle player1 = new Circle(200, 200, 40, Color.PINK, 240f);
        PlayerMovement p1Movement = new PlayerMovement();
        player1.setMovementStrategy(p1Movement);
        ioManager.setCurrentInput(new KeyboardInput());
        ioManager.bindKey(Key.W, () -> p1Movement.setMovingUp(Gdx.input.isKeyPressed(Input.Keys.W)));
        ioManager.bindKey(Key.S, () -> p1Movement.setMovingDown(Gdx.input.isKeyPressed(Input.Keys.S)));
        ioManager.bindKey(Key.A, () -> p1Movement.setMovingLeft(Gdx.input.isKeyPressed(Input.Keys.A)));
        ioManager.bindKey(Key.D, () -> p1Movement.setMovingRight(Gdx.input.isKeyPressed(Input.Keys.D)));
        entityManager.addEntities(player1);

        // Set Triangle as player movement
        Triangle player2 = new Triangle(500, 200, Color.ORANGE, 240f);
        PlayerMovement p2Movement = new PlayerMovement();
        player2.setMovementStrategy(p2Movement);
        ioManager.bindKey(Key.UP, () -> p2Movement.setMovingUp(Gdx.input.isKeyPressed(Input.Keys.UP)));
        ioManager.bindKey(Key.DOWN, () -> p2Movement.setMovingDown(Gdx.input.isKeyPressed(Input.Keys.DOWN)));
        ioManager.bindKey(Key.LEFT, () -> p2Movement.setMovingLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT)));
        ioManager.bindKey(Key.RIGHT, () -> p2Movement.setMovingRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT)));
        entityManager.addEntities(player2);

        movementManager = new MovementManager(entityManager.getEntities());

    }


    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            screenManager.push(new PauseScreen(screenManager));
        }

        ioManager.processInput(Key.W.getCode());
        ioManager.processInput(Key.S.getCode());
        ioManager.processInput(Key.A.getCode());
        ioManager.processInput(Key.D.getCode());
        ioManager.processInput(Key.UP.getCode());
        ioManager.processInput(Key.DOWN.getCode());
        ioManager.processInput(Key.LEFT.getCode());
        ioManager.processInput(Key.RIGHT.getCode());

        movementManager.updateMovement(delta);

        collisionManager.applyAll(
            entityManager.getEntities(),
            (float) Gdx.graphics.getWidth(),
            (float) Gdx.graphics.getHeight()
        );
    }


    @Override
    protected void draw(float delta) {
        // 1. Clear screen
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.16f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2. Draw the background first
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        // 3. Draw other sprites/textures on top
        spriteBatch.begin();
        entityManager.draw(spriteBatch, null);
        spriteBatch.end();

        // 4. Draw shapes (circles/triangles)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        entityManager.draw(null, shapeRenderer);
        shapeRenderer.end();
    }


    @Override
    public void dispose() {
        super.dispose();
        backgroundTexture.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }
}
