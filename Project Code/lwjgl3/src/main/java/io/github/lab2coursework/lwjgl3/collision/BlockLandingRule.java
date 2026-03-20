package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;
import io.github.lab2coursework.lwjgl3.wordgame.WordGameState;

/**
 * CollisionRule: detects when a falling LetterBlock reaches the top of the
 * stack (or the ground) and snaps it into place.
 *
 * Receives WordGameState by constructor injection so it can update game logic.
 */
public class BlockLandingRule implements CollisionRule {

    private static final float GROUND_Y    = 60f;  // y of the ground platform top
    private static final float BLOCK_H     = 60f;

    private final WordGameState state;
    private final float stackBaseY;

    // Tracks how many blocks have landed so far (for stacking height)
    private int stackCount = 0;

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

        // Snap to top of stack
        float landY = stackBaseY + stackCount * BLOCK_H;
        block.setY(landY);
        block.setMovementStrategy(null); // stop moving
        block.setLanded(true);

        // Notify game state
        boolean correct = state.placeNextLetter(block.getLetter());
        if (correct) {
            stackCount++;
        }
        // If wrong, state.loseLife() was already called inside placeNextLetter
    }

    private boolean hasLanded(LetterBlock block) {
        float landY = stackBaseY + stackCount * BLOCK_H;
        return block.getY() <= landY;
    }

    public int getStackCount() { return stackCount; }

    public void resetStack() {
        stackCount = 0;
    }
}
