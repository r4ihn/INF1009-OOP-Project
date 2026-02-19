package lwjgl3.src.main.java.io.github.lab2coursework.lwjgl3.managers;

// Import statements
import com.badlogic.gdx.Screen;
import java.util.ArrayDeque;
import java.util.Deque;

public class ScreenManager {
    private final Deque<Screen> screenStack = new ArrayDeque<>();

    // Push new screen onto stack
    public void push(Screen screen) {
        if (screen == null) return;
        if (!screenStack.isEmpty()) {
            screenStack.peek().pause(); // Pause current screen
        }
        screenStack.push(screen);
        screen.show(); // Show new screen
    }

    // Pop current screen from stack
    public void pop() {
        if (screenStack.isEmpty()) return;
        Screen top = screenStack.pop();
        top.hide(); // Hide popped screen
        top.dispose(); // Dispose popped screen
        if (!screenStack.isEmpty()) {
            screenStack.peek().resume(); // Resume previous screen
        }
    }

    public void set(Screen screen) {
        while (!screenStack.isEmpty()) {
            Screen top = screenStack.pop();
            top.hide();
            top.dispose();
        }
        if (screen != null) {
            screenStack.push(screen);
            screen.show();
        }
    }

    public Screen peak() {
        return screenStack.peek();
    }

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
