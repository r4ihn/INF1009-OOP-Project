package io.github.lab2coursework.lwjgl3.input;

import java.util.HashMap;
import java.util.Map;

public abstract class Input {
    protected Map<Key, Action> inputBindings;

    public Input() {
        this.inputBindings = new HashMap<>();
    }

    public abstract void processInput(int key);

    public abstract void bindKey(Key key, Action action);
}
