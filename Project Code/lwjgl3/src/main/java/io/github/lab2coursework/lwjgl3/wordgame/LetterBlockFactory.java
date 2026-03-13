package io.github.lab2coursework.lwjgl3.wordgame;

import com.badlogic.gdx.graphics.Color;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;

import java.util.Random;

/**
 * Factory Method pattern.
 *
 * Produces LetterBlock entities whose letters are biased toward the target
 * word so the game is playable, while still injecting random noise blocks.
 *
 * Receives WordGameState by constructor injection — no global access.
 */
public class LetterBlockFactory {

    private static final float BLOCK_W  = 90f;
    private static final float BLOCK_H  = 60f;
    private static final float SPAWN_X  = 600f; // centre of screen, under crane
    private static final float SPAWN_Y  = 580f; // just below the crane arm

    // Probability that the spawned letter belongs to the target word
    private static final float WORD_BIAS = 0.65f;

    private final WordGameState state;
    private final Random rng;

    public LetterBlockFactory(WordGameState state) {
        this.state = state;
        this.rng   = new Random();
    }

    // Factory Methods

    /** Creates the block that hangs from the crane. */
    public LetterBlock createHangingBlock() {
        char letter = pickLetter();
        Color color = colorForLetter(letter);
        return new LetterBlock(letter, SPAWN_X, SPAWN_Y, BLOCK_W, BLOCK_H, color);
    }

    /** Creates a replacement block after the previous one is placed or discarded. */
    public LetterBlock createNextBlock() {
        return createHangingBlock(); // same logic, just semantically named
    }

    // ── Letter selection ──────────────────────────────────────────────────────

    private char pickLetter() {
        String word = state.getTargetWord();

        if (rng.nextFloat() < WORD_BIAS && !state.isWordComplete()) {
            // Pick a random unplaced letter from the target word
            int remaining = word.length() - state.getNextLetterIndex();
            int offset    = rng.nextInt(remaining);
            return word.charAt(state.getNextLetterIndex() + offset);
        } else {
            // Random A-Z noise letter
            return (char) ('A' + rng.nextInt(26));
        }
    }

    // ── Colour coding ─────────────────────────────────────────────────────────

    /**
     * GREEN  = this is exactly the next needed letter
     * YELLOW = this letter appears in the word but is not next
     * WHITE  = not in the word at all (noise)
     */
    private Color colorForLetter(char letter) {
        String word = state.getTargetWord();
        char   next = state.getNextExpectedLetter();

        if (letter == next) {
            return Color.GREEN;
        } else if (word.indexOf(letter) >= 0) {
            return Color.YELLOW;
        } else {
            return Color.WHITE;
        }
    }
}
