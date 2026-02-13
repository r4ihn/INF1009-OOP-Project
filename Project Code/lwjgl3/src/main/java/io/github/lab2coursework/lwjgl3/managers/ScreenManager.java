package io.github.lab2coursework.lwjgl3.managers;

// Import statements
import io.github.lab2coursework.lwjgl3.screens.AbstractScreen;
import java.util.Stack;

public class ScreenManager {
    // Create a stack to hold screens
    private final Stack<AbstractScreen> screenStack = new Stack<>();

    // Method to push new screen onto the stack
    public void pushScreen(AbstractScreen screen) {
        if (!screenStack.isEmpty()) {
            screenStack.peek().pause();
        }
        screenStack.push(screen);
        screen.show();
    }

    // Method to pop the current screen from stack
    public void popScreen() {
        if (!screenStack.isEmpty()) {
            AbstractScreen topScreen = screenStack.pop();
            topScreen.hide();
            topScreen.dispose();
        }
        if (!screenStack.isEmpty()) {
            screenStack.peek().resume();
        }
    }

    // Method to get current screen
    public AbstractScreen getCurrentScreen() {
        return screenStack.isEmpty() ? null : screenStack.peek();
    }

    // Method to render current screen
    public void render() {
        if (!screenStack.isEmpty()) {
            screenStack.peek().render();
        }
    }

    // Method to update current screen
    public void update(float delta) {
        if (!screenStack.isEmpty()) {
            screenStack.peek().update(delta);
        }
    }

    // Method to dispose all screens (for memory management)
    public void dispose() {
        while (!screenStack.isEmpty()) { // Check if stack is not empty
            AbstractScreen screen = screenStack.pop(); // If not empty, pop every screen out of the stack
            screen.dispose(); // Dispose each screen
        }
    }
}
