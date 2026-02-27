package io.github.lab2coursework.lwjgl3.input;

public class KeyboardInput extends Input {

    @Override
    public void processInput(int keyCode) {
        Key key = Key.fromCode(keyCode);
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
