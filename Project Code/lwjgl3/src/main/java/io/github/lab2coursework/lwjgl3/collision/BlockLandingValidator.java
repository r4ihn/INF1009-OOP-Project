package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.LetterBlock;

public class BlockLandingValidator {

    public float getCenterX(LetterBlock block) {
        return block.getX() + block.getWidth() / 2f;
    }

    // General AABB collision so landed blocks behave as solid objects
    public boolean isOverlapping(LetterBlock a, LetterBlock b) {
        return a.getX() < b.getRight()
            && a.getRight() > b.getX()
            && a.getY() < b.getTop()
            && a.getTop() > b.getY();
    }

    // Valid landing means the falling block is coming from above onto the top surface
    public boolean isLandingOnTop(LetterBlock falling, LetterBlock stacked) {
        boolean overlapsX = falling.getRight() > stacked.getX()
            && falling.getX() < stacked.getRight();
        boolean bottomReachedTop = falling.getY() <= stacked.getTop();
        boolean blockIsMostlyAbove = falling.getTop() >= stacked.getTop();

        return overlapsX && bottomReachedTop && blockIsMostlyAbove;
    }
}
