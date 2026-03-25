package io.github.lab2coursework.lwjgl3.managers;

import io.github.lab2coursework.lwjgl3.input.Input;
import io.github.lab2coursework.lwjgl3.input.Key;
import io.github.lab2coursework.lwjgl3.input.Action;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores global key bindings and forwards raw input to the active input handler.
 */
public class IOManager {
    private Input currentInput;
    private Map<Key, Action> inputBindings;

    public IOManager() {
        this.inputBindings = new HashMap<>();
        this.currentInput = null;
    }

    public void processInput(int key) {
        if (currentInput != null) {
            currentInput.processInput(key);
        }
    }

    public void bindKey(Key key, Action action) {
        inputBindings.put(key, action);
        if (currentInput != null) {
            currentInput.bindKey(key, action);
        }
    }

    public void setCurrentInput(Input input) {
        this.currentInput = input;
        if (currentInput != null) {
            // Re-apply existing bindings whenever the input handler changes.
            for (Map.Entry<Key, Action> entry : inputBindings.entrySet()) {
                currentInput.bindKey(entry.getKey(), entry.getValue());
            }
        }
    }

    public Input getCurrentInput() {
        return currentInput;
    }
}
