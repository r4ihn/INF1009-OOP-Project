package io.github.lab2coursework.lwjgl3.collision;

import io.github.lab2coursework.lwjgl3.entities.Entity;
import io.github.lab2coursework.lwjgl3.entities.GarbageCan;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;
import io.github.lab2coursework.lwjgl3.wordgame.WordGameState;

/**
 * CollisionRule: when a LetterBlock overlaps the GarbageCan, discard it
 * and notify WordGameState (which may deduct a life if the letter was needed).
 */
public class GarbageCollectionRule implements CollisionRule {

    private final GarbageCan    bin;
    private final WordGameState state;

    public GarbageCollectionRule(GarbageCan bin, WordGameState state) {
        this.bin   = bin;
        this.state = state;
    }

    @Override
    public boolean matches(Entity a, Entity b) {
        if (!(a instanceof LetterBlock)) return false;
        LetterBlock block = (LetterBlock) a;
        return !block.isLanded() && !block.isDiscarded() && bin.overlaps(block);
    }

    @Override
    public void resolve(Entity a, Entity b) {
        LetterBlock block = (LetterBlock) a;
        state.discardLetter(block.getLetter());
        block.setDiscarded(true);
        block.setMovementStrategy(null);
        // Move off-screen so EntityManager stops drawing it
        block.setX(-9999f);
        block.setY(-9999f);
    }
}
