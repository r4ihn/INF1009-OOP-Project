package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.LetterBlock;
import io.github.lab2coursework.lwjgl3.wordgame.GameScore;

/**
 * Stores per-word stack height and last landed X position.
 * This keeps landing rules small and focused on decision-making.
 */
public class BlockStackTracker {

    private final float stackBaseY;
    private final float blockHeight;

    private final int[] stackCountPerWord;
    private final float[] stackXPerWord;

    public BlockStackTracker(float stackBaseY, float blockHeight) {
        this.stackBaseY = stackBaseY;
        this.blockHeight = blockHeight;
        this.stackCountPerWord = new int[GameScore.TARGET_WORD_COUNT];
        this.stackXPerWord = new float[GameScore.TARGET_WORD_COUNT];
        resetAll();
    }

    public void recordLanding(int wordIdx, LetterBlock block) {
        if (!isValidWordIndex(wordIdx)) {
            return;
        }
        stackCountPerWord[wordIdx]++;
        stackXPerWord[wordIdx] = block.getX();
    }

    public void resetWordStack(int wordIdx) {
        if (!isValidWordIndex(wordIdx)) {
            return;
        }
        stackCountPerWord[wordIdx] = 0;
        stackXPerWord[wordIdx] = -1f;
    }

    public void resetAll() {
        for (int i = 0; i < GameScore.TARGET_WORD_COUNT; i++) {
            stackCountPerWord[i] = 0;
            stackXPerWord[i] = -1f;
        }
    }

    public boolean hasStack(int wordIdx) {
        return isValidWordIndex(wordIdx) && stackCountPerWord[wordIdx] > 0;
    }

    public boolean hasStackX(int wordIdx) {
        return isValidWordIndex(wordIdx) && stackXPerWord[wordIdx] >= 0f;
    }

    public float getStackX(int wordIdx) {
        if (!hasStackX(wordIdx)) {
            return -1f;
        }
        return stackXPerWord[wordIdx];
    }

    public int getStackCount(int wordIdx) {
        if (!isValidWordIndex(wordIdx)) {
            return 0;
        }
        return stackCountPerWord[wordIdx];
    }

    public boolean isTopBlock(LetterBlock block, int wordIdx) {
        if (!hasStack(wordIdx)) {
            return false;
        }

        // Expected Y for the current top-most block in this tower.
        float expectedTopY = stackBaseY + (stackCountPerWord[wordIdx] - 1) * blockHeight;
        return Math.abs(block.getY() - expectedTopY) < 0.5f;
    }

    public float estimateGroundError(LetterBlock block, int wordIdx) {
        if (!hasStackX(wordIdx)) {
            return Float.MAX_VALUE;
        }

        float blockCenterX = block.getX() + block.getWidth() / 2f;
        float expectedCenterX = stackXPerWord[wordIdx] + block.getWidth() / 2f;
        return Math.abs(blockCenterX - expectedCenterX);
    }

    private boolean isValidWordIndex(int wordIdx) {
        return wordIdx >= 0 && wordIdx < GameScore.TARGET_WORD_COUNT;
    }
}
