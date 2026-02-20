package io.github.lab2coursework.lwjgl3.entities;

import io.github.lab2coursework.lwjgl3.graphics.TextureObject;

public class Raindrop extends TextureObject {

    public Raindrop() {
        super();
    }

    public Raindrop(String path, float x, float y, float speed) {
        super(path, x, y, speed);
    }

    @Override
    public void update() {
        // no game logic here
    }
}
