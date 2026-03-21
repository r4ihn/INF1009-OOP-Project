package io.github.lab2coursework.lwjgl3.wordgame;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all mutable state for one playthrough of the word game.
 * Now supports 3 simultaneous target words with combo scoring.
 */
public class WordGameState {

    public static final int MAX_LIVES = 3;

    private final WordBank wordBank;
    private final GameScore gameScore;
    private int level;           // 0-based index
    private int lives;

    // 3 target words for simultaneous spelling
    private final List<String> targetWords = new ArrayList<>();
    private final List<List<Character>> stackedLettersPerWord = new ArrayList<>();

    // How many blocks the player has discarded this attempt
    private int discardedThisAttempt;

    public WordGameState(WordBank wordBank) {
        this.wordBank = wordBank;
        this.gameScore = new GameScore();
        this.level = 0;
        this.lives = MAX_LIVES;
        loadWordsForLevel();
    }

    // ── Level management ──────────────────────────────────────────────────────

    private void loadWordsForLevel() {
        targetWords.clear();
        stackedLettersPerWord.clear();

        // Get a random category each level for variety
        int categoryIndex = (int)(Math.random() * wordBank.getCategories().size());
        WordCategory cat = wordBank.getCategory(categoryIndex);
        List<String> words = new ArrayList<>(cat.getWords());

        // Shuffle to get random words
        java.util.Collections.shuffle(words);

        // Pick 3 distinct random words from the shuffled list
        for (int i = 0; i < 3 && i < words.size(); i++) {
            String word = words.get(i).toUpperCase();
            targetWords.add(word);
            stackedLettersPerWord.add(new ArrayList<>());
        }

        gameScore.setTargetWords(targetWords);
        discardedThisAttempt = 0;
    }

    public void advanceLevel() {
        level++;
        lives = MAX_LIVES;   // restore lives for new level
        gameScore.resetForNextLevel();
        loadWordsForLevel();
    }

    // ── Attempt management ────────────────────────────────────────────────────

    /** Called when the player discards a NEEDED letter or stacks a wrong one. */
    public void loseLife() {
        lives--;
        resetAttempt();
    }

    /** Clears the current stacked progress for a fresh attempt. */
    public void resetAttempt() {
        for (List<Character> stack : stackedLettersPerWord) {
            stack.clear();
        }
        discardedThisAttempt = 0;
    }

    // ── Letter stacking ───────────────────────────────────────────────────────

    /**
     * Try to place a letter onto any of the 3 word stacks.
     * IMPORTANT: Each letter can only match ONE word. If multiple words need the same letter,
     * only the FIRST incomplete word in the loop will accept it. Other words cannot use it.
     * Returns the index of the word it matched (0-2), or -1 if no match.
     * If wrong, costs a life.
     */
    public int placeNextLetter(char letter) {
        letter = Character.toUpperCase(letter);

        // Try to match against any of the 3 words
        // ONLY the first word that needs this letter will accept it
        for (int wordIdx = 0; wordIdx < 3; wordIdx++) {
            if (gameScore.isWordCompleted(wordIdx)) {
                continue; // skip already-completed words
            }

            String word = targetWords.get(wordIdx);
            List<Character> stacked = stackedLettersPerWord.get(wordIdx);

            if (stacked.size() < word.length()) {
                char expected = word.charAt(stacked.size());
                if (letter == expected) {
                    // MATCH! Add to this word's stack
                    stacked.add(expected);

                    // Check if this word is now complete
                    if (stacked.size() == word.length()) {
                        gameScore.completeWord(wordIdx);
                    }

                    return wordIdx; // success - letter used by this word
                }
            }
        }

        // No word matched this letter - it's wrong!
        // Even if other words need this letter later, this drop is rejected
        loseLife();
        return -1;
    }

    /**
     * Discard a block via the garbage bin.
     * This is a FREE action - no penalty, no life loss.
     * Players can safely cycle through blocks to find the ones they need.
     */
    public void discardLetter(char letter) {
        discardedThisAttempt++;
        // No penalty for discarding via the bin - it's a free action!
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean isAllWordsComplete() {
        return gameScore.allWordsCompleted();
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    /**
     * Returns display strings for all 3 words.
     * E.g., ["_ _ A T", "_ O G", "_ E D"]
     */
    public List<String> getDisplayWords() {
        List<String> displays = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            displays.add(getDisplayWord(i));
        }
        return displays;
    }

    /**
     * Returns e.g. "_ _ A T" when "CAT" is the word and 'A','T' stacked.
     */
    public String getDisplayWord(int wordIndex) {
        if (wordIndex < 0 || wordIndex >= 3) return "";

        String word = targetWords.get(wordIndex);
        List<Character> stacked = stackedLettersPerWord.get(wordIndex);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (i < stacked.size()) {
                sb.append(stacked.get(i));
            } else {
                sb.append('_');
            }
            if (i < word.length() - 1) sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * Get the next expected letter for a specific word
     */
    public char getNextExpectedLetter(int wordIndex) {
        if (wordIndex < 0 || wordIndex >= 3) return 0;
        if (gameScore.isWordCompleted(wordIndex)) return 0;

        String word = targetWords.get(wordIndex);
        List<Character> stacked = stackedLettersPerWord.get(wordIndex);

        if (stacked.size() < word.length()) {
            return word.charAt(stacked.size());
        }
        return 0;
    }

    /**
     * Get the next expected letter from ANY incomplete word (for block coloring)
     */
    public char getAnyNextExpectedLetter() {
        for (int i = 0; i < 3; i++) {
            char c = getNextExpectedLetter(i);
            if (c != 0) return c;
        }
        return 0;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public List<String> getTargetWords() { return targetWords; }
    public int getLives() { return lives; }
    public int getLevel() { return level + 1; } // 1-based for display
    public GameScore getGameScore() { return gameScore; }
    public int getTotalScore() { return gameScore.getTotalScore(); }
    public int getComboCount() { return gameScore.getComboCount(); }
}
