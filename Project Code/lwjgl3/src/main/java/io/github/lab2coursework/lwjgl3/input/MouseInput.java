package io.github.lab2coursework.lwjgl3.input;

/**
 * Mouse-button implementation of the generic input binding map.
 */
public class MouseInput extends Input {

    @Override
    public void processInput(int buttonCode) {
        Key key = Key.fromMouseButton(buttonCode);
        if (key != null) {
            Action action = inputBindings.get(key);
            if (action != null) {
                action.execute();
            }
        }
    }

    @Override
    public void bindKey(Key key, Action action) {
        if (key != null) {
            inputBindings.put(key, action);
        }
    }
}
