package io.github.lab2coursework.lwjgl3.managers;

public class DebugManager {

    private boolean debugRenderingEnabled;
    private boolean fpsDisplayEnabled;
    private boolean collisionOverlayEnabled;

    public void toggleDebugRendering() {
        debugRenderingEnabled = !debugRenderingEnabled;
    }

    public void toggleFpsDisplay() {
        fpsDisplayEnabled = !fpsDisplayEnabled;
    }

    public void toggleCollisionOverlay() {
        collisionOverlayEnabled = !collisionOverlayEnabled;
    }

    public boolean isDebugRenderingEnabled() {
        return debugRenderingEnabled;
    }

    public boolean isFpsDisplayEnabled() {
        return fpsDisplayEnabled;
    }

    public boolean isCollisionOverlayEnabled() {
        return collisionOverlayEnabled;
    }

}
