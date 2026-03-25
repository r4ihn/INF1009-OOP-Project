package io.github.lab2coursework.lwjgl3.input;

import java.util.HashMap;
import java.util.Map;

/**
 * Base input binding map that converts raw key/button codes into actions.
 */
public abstract class Input {
    protected Map<Key, Action> inputBindings;

    public Input() {
        this.inputBindings = new HashMap<>();
    }

    public abstract void processInput(int key);

    public abstract void bindKey(Key key, Action action);
}
