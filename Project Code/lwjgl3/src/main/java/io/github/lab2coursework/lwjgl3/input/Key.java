package io.github.lab2coursework.lwjgl3.input;

/**
 * Logical input keys used by the custom input binding system.
 */
public enum Key {
    // Keyboard keys
    W(87), A(65), S(83), D(68),
    UP(38), DOWN(40), LEFT(37), RIGHT(39),
    SPACE(32), ENTER(10), ESCAPE(27),

    // Mouse buttons
    MOUSE_LEFT(0), MOUSE_RIGHT(1), MOUSE_MIDDLE(2);

    private final int code;

    Key(int code) {
        this.code = code;
    }

    public static Key fromCode(int code) {
        // Linear scan is fine because the enum is small.
        for (Key key : values()) {
            if (key.code == code) {
                return key;
            }
        }
        return null;
    }

    public static Key fromMouseButton(int button) {
        switch (button) {
            case 0: return MOUSE_LEFT;
            case 1: return MOUSE_RIGHT;
            case 2: return MOUSE_MIDDLE;
            default: return null;
        }
    }

    public int getCode() {
        return code;
    }
}
