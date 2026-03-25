package io.github.lab2coursework.lwjgl3.wordgame;

import com.badlogic.gdx.graphics.Color;
import io.github.lab2coursework.lwjgl3.entities.LetterBlock;

import java.util.Random;

/**
 * Factory Method pattern.
 * Produces LetterBlock entities whose letters are biased toward the 3 target words
 * so the game is playable, while still injecting random noise blocks.
 */
public class LetterBlockFactory {

    private static final float BLOCK_W  = 90f;
    private static final float BLOCK_H  = 60f;
    private static final float SPAWN_X  = 600f; // centre of screen, under crane
    private static final float SPAWN_Y  = 580f; // just below the crane arm

    // Probability that the spawned letter belongs to one of the 3 target words
    private static final float WORD_BIAS = 0.70f;

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
        Color color = Color.LIGHT_GRAY;  // Neutral color - no hints
        return new LetterBlock(letter, SPAWN_X, SPAWN_Y, BLOCK_W, BLOCK_H, color);
    }



    // ── Letter selection ──────────────────────────────────────────────────────

    private char pickLetter() {
        if (rng.nextFloat() < WORD_BIAS) {
            // Pick a random unplaced letter from one of the 3 target words
            return pickFromTargetWords();
        } else {
            // Random A-Z noise letter
            return (char) ('A' + rng.nextInt(26));
        }
    }

    private char pickFromTargetWords() {
        // Collect all remaining letters from all 3 incomplete words
        StringBuilder remaining = new StringBuilder();

        for (int wordIdx = 0; wordIdx < GameScore.TARGET_WORD_COUNT; wordIdx++) {
            if (state.getGameScore().isWordCompleted(wordIdx)) {
                continue;
            }

            char nextExpected = state.getNextExpectedLetter(wordIdx);
            if (nextExpected != 0) {
                remaining.append(nextExpected);
            }
        }

        if (remaining.length() == 0) {
            // All words complete, pick random
            return (char) ('A' + rng.nextInt(26));
        }

        return remaining.charAt(rng.nextInt(remaining.length()));
    }

    }
