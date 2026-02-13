package io.github.lab2coursework.lwjgl3;

import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import java.util.Map;

public class IOManager {
    private Map<Integer, Runnable> keyBindings;
    private Map<Integer, Runnable> mouseBindings;

    public IOManager() {
        this.keyBindings = new HashMap<>();
        this.mouseBindings = new HashMap<>();
        setupDefaultBindings();
    }

    private void setupDefaultBindings() {
        // Keyboard bindings
        bindKey(com.badlogic.gdx.Input.Keys.W, () -> System.out.println("Move Up"));
        bindKey(com.badlogic.gdx.Input.Keys.S, () -> System.out.println("Move Down"));
        bindKey(com.badlogic.gdx.Input.Keys.A, () -> System.out.println("Move Left"));
        bindKey(com.badlogic.gdx.Input.Keys.D, () -> System.out.println("Move Right"));
        bindKey(com.badlogic.gdx.Input.Keys.SPACE, () -> System.out.println("Jump"));
        bindKey(com.badlogic.gdx.Input.Keys.ESCAPE, () -> Gdx.app.exit());

        // Mouse bindings
        bindMouse(com.badlogic.gdx.Input.Buttons.LEFT, () ->
            System.out.println("Left click at: " + Gdx.input.getX() + ", " + Gdx.input.getY()));
        bindMouse(com.badlogic.gdx.Input.Buttons.RIGHT, () ->
            System.out.println("Right click!"));
    }

    public void bindKey(int keyCode, Runnable action) {
        keyBindings.put(keyCode, action);
    }

    public void bindMouse(int button, Runnable action) {
        mouseBindings.put(button, action);
    }

    public void processInput() {
        // Process keyboard
        for (Map.Entry<Integer, Runnable> entry : keyBindings.entrySet()) {
            if (Gdx.input.isKeyPressed(entry.getKey())) {
                entry.getValue().run();
            }
        }

        // Process mouse buttons
        for (Map.Entry<Integer, Runnable> entry : mouseBindings.entrySet()) {
            if (Gdx.input.isButtonPressed(entry.getKey())) {
                entry.getValue().run();
            }
        }
    }

    public boolean isKeyPressed(int keyCode) {
        return Gdx.input.isKeyPressed(keyCode);
    }

    public boolean isMouseButtonPressed(int button) {
        return Gdx.input.isButtonPressed(button);
    }

    public int getMouseX() {
        return Gdx.input.getX();
    }

    public int getMouseY() {
        return Gdx.input.getY();
    }
}
