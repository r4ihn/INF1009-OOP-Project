package io.github.lab2coursework.lwjgl3.managers;

// Import statements
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages screens as a stack so gameplay can pause and resume cleanly.
 */
public class ScreenManager {
    private final Deque<Screen> screenStack = new ArrayDeque<>();

    /** Places a new screen on top and pauses the previous one. */
    public void push(Screen screen) {
        if (screen == null) return;
        if (!screenStack.isEmpty()) {
            screenStack.peek().pause(); // Pause current screen
        }
        screenStack.push(screen);
        screen.show(); // Show new screen
        screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /** Removes the current screen and resumes the previous one if present. */
    public void pop() {
        if (screenStack.isEmpty()) return;
        Screen top = screenStack.pop();
        top.hide(); // Hide popped screen

        if (!screenStack.isEmpty()) {
            Screen resumed = screenStack.peek();
            screenStack.peek().resume(); // Resume previous screen
            resumed.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Resize resumed screen
        }
    }

    /** Replaces the whole stack with a single screen. */
    public void set(Screen screen) {
        while (!screenStack.isEmpty()) {
            Screen top = screenStack.pop();
            top.hide();
            top.dispose();
        }
        if (screen != null) {
            screenStack.push(screen);
            screen.show();
            screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    /** Returns the current top screen without removing it. */
    public Screen peak() {
        return screenStack.peek();
    }

    /** Forwards frame rendering to the top screen only. */
    public void render(float delta) {
        Screen top = screenStack.peek();
        if (top != null) {
            top.render(delta);
        }
    }

    public void resize ( int width, int height) {
        Screen top = screenStack.peek();
        if (top != null) {
            top.resize(width, height);
        }
    }

    public void dispose() {
        while (!screenStack.isEmpty()) {
            Screen top = screenStack.pop();
            top.hide();
            top.dispose();
        }
    }
}
