package io.github.lab2coursework.lwjgl3.movement;

/**
 * Marks movement strategies that need a live anchor point each frame.
 */
public interface AnchoredMovement {
    void setAnchor(float x, float y);
}
