package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.LetterBlock;

public class BlockPlacementService {

    public void markDiscarded(LetterBlock block) {
        block.setDiscarded(true);
        block.setMovementStrategy(null);
    }

    public void placeOnTopOfBlock(LetterBlock block, LetterBlock stacked, int wordIdx) {
        block.setY(stacked.getTop());
        block.setMovementStrategy(null);
        block.setLanded(true);
        block.setWordIndex(wordIdx);
    }

    public void placeOnGround(LetterBlock block, float groundY, int wordIdx) {
        block.setY(groundY);
        block.setMovementStrategy(null);
        block.setLanded(true);
        block.setWordIndex(wordIdx);
    }

    public void stopAtTopAndDiscard(LetterBlock block, LetterBlock stacked) {
        block.setY(stacked.getTop());
        block.setMovementStrategy(null);
        block.setDiscarded(true);
    }
}
