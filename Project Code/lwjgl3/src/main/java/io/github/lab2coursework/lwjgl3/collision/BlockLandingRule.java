package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;
import io.github.lab2coursework.lwjgl3.wordgame.WordGameState;

/**
 * CollisionRule: detects when a falling LetterBlock reaches the top of the
 * stack (or the ground) and snaps it into place.
 *
 * Smart Stacking: Each word has its own vertical section in the tower.
 * This prevents words from interrupting each other.
 */
public class BlockLandingRule implements CollisionRule {

    private static final float GROUND_Y    = 60f;  // y of the ground platform top
    private static final float BLOCK_H     = 60f;

    private final WordGameState state;
    private final float stackBaseY;

    // Tracks stack height for each of the 3 words independently
    // This creates 3 vertical "sections" in the tower
    private int[] stackCountPerWord = {0, 0, 0};

    public BlockLandingRule(WordGameState state) {
        this.state      = state;
        this.stackBaseY = GROUND_Y;
    }

    @Override
    public boolean matches(Entity a, Entity b) {
        // We only care about the falling block (b is always null for ground checks)
        return a instanceof LetterBlock
            && !((LetterBlock) a).isLanded()
            && !((LetterBlock) a).isDiscarded()
            && hasLanded((LetterBlock) a);
    }

    @Override
    public void resolve(Entity a, Entity b) {
        LetterBlock block = (LetterBlock) a;

        // Notify game state - returns the word index that matched, or -1 if wrong
        int matchedWordIdx = state.placeNextLetter(block.getLetter());

        if (matchedWordIdx >= 0) {
            // Correct letter - snap to the top of THIS WORD's stack section
            float landY = stackBaseY + stackCountPerWord[matchedWordIdx] * BLOCK_H;
            block.setY(landY);
            block.setMovementStrategy(null); // stop moving
            block.setLanded(true);
            block.setWordIndex(matchedWordIdx); // Mark which word this block belongs to
            stackCountPerWord[matchedWordIdx]++;
        } else {
            // Wrong letter - just mark as discarded (disappears) without resetting tower
            block.setDiscarded(true);
            // Progress is NOT reset - tower stays intact
        }
    }

    private boolean hasLanded(LetterBlock block) {
        // Check if block has landed on the ground or on top of any existing stack
        float totalStackHeight = 0;
        for (int i = 0; i < 3; i++) {
            totalStackHeight = Math.max(totalStackHeight, stackCountPerWord[i]);
        }
        float landY = stackBaseY + totalStackHeight * BLOCK_H;
        return block.getY() <= landY;
    }

    public int getStackCount(int wordIdx) {
        if (wordIdx >= 0 && wordIdx < 3) {
            return stackCountPerWord[wordIdx];
        }
        return 0;
    }

    public int getTotalStackCount() {
        int total = 0;
        for (int count : stackCountPerWord) {
            total += count;
        }
        return total;
    }

    public void resetStack() {
        stackCountPerWord[0] = 0;
        stackCountPerWord[1] = 0;
        stackCountPerWord[2] = 0;
    }
}
