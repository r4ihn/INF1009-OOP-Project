package io.github.lab2coursework.lwjgl3.screens;

public abstract class AbstractScreen {
    // The functions that all screens will have, but will be overridden
    // in the specific screen classes
    public void show() {}
    public void hide()  {}
    public void pause() {}
    public void resume() {}
    public void dispose() {}

    // Abstract functions that MUST be implemented in the child classes
    public abstract void render();
    public abstract void update(float delta);
}
