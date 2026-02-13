package io.github.lab2coursework.lwjgl3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;

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

    // Rendering
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // Player variables
    private float playerX = 400;
    private float playerY = 300;
    private final float playerSpeed = 200;
    private final float playerSize = 50;

    // Mouse movement variables
    private float targetX = 400;
    private float targetY = 300;
    private boolean moveToMouse = false;

    // Mouse tracking variables
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private boolean trackMouseMovement = true;
    private final int MOUSE_TRACK_THRESHOLD = 20; // Only print when mouse moves this many pixels

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        ioManager = new IOManager();
        isRunning = true;

        // Setup BOTH keyboard and mouse bindings
        setupKeyboardBindings();
        setupMouseBindings();

        // Initialize mouse position tracking
        lastMouseX = ioManager.getMouseX();
        lastMouseY = ioManager.getMouseY();

        System.out.println("Game created!");
        System.out.println("WASD/Arrows: Move player");
        System.out.println("LEFT CLICK: Move to mouse position");
        System.out.println("RIGHT CLICK: Teleport to mouse position");
        System.out.println("SPACE: Jump action");
        System.out.println("ESCAPE: Quit");
        System.out.println("Mouse movement tracking is ON");
    }

    private void setupKeyboardBindings() {
        if (ioManager == null) return;

        // Bind ESCAPE to exit
        ioManager.bindKey(com.badlogic.gdx.Input.Keys.ESCAPE, () -> {
            System.out.println("Exiting game...");
            Gdx.app.exit();
        });

        // Bind SPACE to a test action
        ioManager.bindKey(com.badlogic.gdx.Input.Keys.SPACE, () -> {
            System.out.println("Space pressed! Player position: " + playerX + ", " + playerY);
        });

        // Bind movement keys to print messages
        ioManager.bindKey(com.badlogic.gdx.Input.Keys.W, () -> {
            System.out.println("W pressed - Moving up");
        });

        ioManager.bindKey(com.badlogic.gdx.Input.Keys.S, () -> {
            System.out.println("S pressed - Moving down");
        });

        ioManager.bindKey(com.badlogic.gdx.Input.Keys.A, () -> {
            System.out.println("A pressed - Moving left");
        });

        ioManager.bindKey(com.badlogic.gdx.Input.Keys.D, () -> {
            System.out.println("D pressed - Moving right");
        });

        // Arrow keys too
        ioManager.bindKey(com.badlogic.gdx.Input.Keys.UP, () -> {
            System.out.println("UP arrow pressed - Moving up");
        });

        ioManager.bindKey(com.badlogic.gdx.Input.Keys.DOWN, () -> {
            System.out.println("DOWN arrow pressed - Moving down");
        });

        ioManager.bindKey(com.badlogic.gdx.Input.Keys.LEFT, () -> {
            System.out.println("LEFT arrow pressed - Moving left");
        });

        ioManager.bindKey(com.badlogic.gdx.Input.Keys.RIGHT, () -> {
            System.out.println("RIGHT arrow pressed - Moving right");
        });

        // Toggle mouse tracking with M key
        ioManager.bindKey(com.badlogic.gdx.Input.Keys.M, () -> {
            trackMouseMovement = !trackMouseMovement;
            System.out.println("Mouse movement tracking: " + (trackMouseMovement ? "ON" : "OFF"));
        });
    }

    private void setupMouseBindings() {
        if (ioManager == null) return;

        // Left click: Move smoothly to mouse position
        ioManager.bindMouse(com.badlogic.gdx.Input.Buttons.LEFT, () -> {
            targetX = ioManager.getMouseX();
            targetY = Gdx.graphics.getHeight() - ioManager.getMouseY(); // Flip Y coordinate
            moveToMouse = true;
            System.out.println("Left click - Moving to: " + targetX + ", " + targetY);
        });

        // Right click: Teleport instantly
        ioManager.bindMouse(com.badlogic.gdx.Input.Buttons.RIGHT, () -> {
            playerX = ioManager.getMouseX();
            playerY = Gdx.graphics.getHeight() - ioManager.getMouseY();
            moveToMouse = false;
            System.out.println("Right click - Teleported to: " + playerX + ", " + playerY);
        });

        // Middle click: Reset position
        ioManager.bindMouse(com.badlogic.gdx.Input.Buttons.MIDDLE, () -> {
            playerX = 400;
            playerY = 300;
            moveToMouse = false;
            System.out.println("Middle click - Reset position to center");
        });
    }

    @Override
    public void render() {
        // Clear screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update
        deltaTime = Gdx.graphics.getDeltaTime();
        update(deltaTime);

        // Process input every frame - this will trigger the console messages
        ioManager.processInput();

        // Track mouse movement if enabled
        if (trackMouseMovement) {
            trackMousePosition();
        }

        // Draw player
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw player as red square
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(playerX, playerY, playerSize, playerSize);

        // Draw target position if moving to mouse
        if (moveToMouse) {
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.circle(targetX + playerSize/2, targetY + playerSize/2, 10);
        }

        shapeRenderer.end();

        // Draw mouse position indicator
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        int mouseX = ioManager.getMouseX();
        int mouseY = Gdx.graphics.getHeight() - ioManager.getMouseY(); // Flip Y
        shapeRenderer.circle(mouseX, mouseY, 15);
        shapeRenderer.end();

        batch.begin();
        // Draw textures here if you have any
        batch.end();
    }

    private void trackMousePosition() {
        int currentMouseX = ioManager.getMouseX();
        int currentMouseY = ioManager.getMouseY();

        // Calculate how far mouse moved
        int deltaX = Math.abs(currentMouseX - lastMouseX);
        int deltaY = Math.abs(currentMouseY - lastMouseY);

        // Only print if mouse moved significantly
        if (deltaX > MOUSE_TRACK_THRESHOLD || deltaY > MOUSE_TRACK_THRESHOLD) {
            // Determine direction
            String direction = "";

            if (currentMouseX > lastMouseX && deltaX > deltaY) {
                direction = "RIGHT";
            } else if (currentMouseX < lastMouseX && deltaX > deltaY) {
                direction = "LEFT";
            } else if (currentMouseY > lastMouseY && deltaY > deltaX) {
                direction = "DOWN";
            } else if (currentMouseY < lastMouseY && deltaY > deltaX) {
                direction = "UP";
            }

            // Print mouse position and movement
            if (!direction.isEmpty()) {
                System.out.println("Mouse moved " + direction +
                    " to (" + currentMouseX + ", " + currentMouseY + ")");
            } else {
                System.out.println("Mouse at: (" + currentMouseX + ", " + currentMouseY + ")");
            }

            // Update last position
            lastMouseX = currentMouseX;
            lastMouseY = currentMouseY;
        }
    }

    private void update(float deltaTime) {
        if (!isRunning) return;

        // Keyboard movement
        float moveX = 0;
        float moveY = 0;

        if (ioManager.isKeyPressed(com.badlogic.gdx.Input.Keys.W) ||
            ioManager.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
            moveY += playerSpeed * deltaTime;
        }
        if (ioManager.isKeyPressed(com.badlogic.gdx.Input.Keys.S) ||
            ioManager.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            moveY -= playerSpeed * deltaTime;
        }
        if (ioManager.isKeyPressed(com.badlogic.gdx.Input.Keys.A) ||
            ioManager.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            moveX -= playerSpeed * deltaTime;
        }
        if (ioManager.isKeyPressed(com.badlogic.gdx.Input.Keys.D) ||
            ioManager.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            moveX += playerSpeed * deltaTime;
        }

        // Apply keyboard movement
        playerX += moveX;
        playerY += moveY;

        // If moving to mouse position, do smooth movement
        if (moveToMouse) {
            // Calculate direction to target
            float dirX = targetX - playerX;
            float dirY = targetY - playerY;

            // Calculate distance
            float distance = (float)Math.sqrt(dirX * dirX + dirY * dirY);

            // If close enough, stop moving
            if (distance < 5) {
                moveToMouse = false;
                playerX = targetX;
                playerY = targetY;
            } else {
                // Normalize direction and move
                dirX /= distance;
                dirY /= distance;

                playerX += dirX * playerSpeed * deltaTime;
                playerY += dirY * playerSpeed * deltaTime;
            }
        }

        // Keep player on screen
        if (playerX < 0) playerX = 0;
        if (playerX > Gdx.graphics.getWidth() - playerSize) playerX = Gdx.graphics.getWidth() - playerSize;
        if (playerY < 0) playerY = 0;
        if (playerY > Gdx.graphics.getHeight() - playerSize) playerY = Gdx.graphics.getHeight() - playerSize;
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        System.out.println("Game disposed!");
    }
}
