package io.github.lab2coursework.lwjgl3.wordgame;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all mutable state for one playthrough of the word game.
 * Passed by reference into the screen and factory — no globals needed.
 */
public class WordGameState {

    public static final int MAX_LIVES = 3;

    private final WordBank wordBank;
    private int level;           // 0-based index into words across categories
    private int lives;
    private String targetWord;
    private String categoryName;

    // Letters the player has successfully stacked so far this attempt
    private final List<Character> stackedLetters = new ArrayList<>();

    // How many blocks the player has discarded this attempt
    private int discardedThisAttempt;

    public WordGameState(WordBank wordBank) {
        this.wordBank = wordBank;
        this.level = 0;
        this.lives = MAX_LIVES;
        loadWordForLevel();
    }

    // Level management

    private void loadWordForLevel() {
        // Walk through all categories in order
        int totalPerCategory = 10;
        int catIndex  = (level / totalPerCategory) % wordBank.getCategories().size();
        int wordIndex = level % totalPerCategory;

        WordCategory cat = wordBank.getCategory(catIndex);
        List<String> words = cat.getWords();
        targetWord    = words.get(wordIndex % words.size()).toUpperCase();
        categoryName  = cat.getName();
        stackedLetters.clear();
        discardedThisAttempt = 0;
    }

    public void advanceLevel() {
        level++;
        lives = MAX_LIVES;   // restore lives for new level
        loadWordForLevel();
    }

    // Attempt management

    /** Called when the player discards a NEEDED letter or stacks a wrong one. */
    public void loseLife() {
        lives--;
        resetAttempt();
    }

    /** Clears the current stacked progress for a fresh attempt. */
    public void resetAttempt() {
        stackedLetters.clear();
        discardedThisAttempt = 0;
    }

    // Letter stacking

    /**
     * Try to place a letter onto the stack.
     * Returns true if the letter is the next correct letter.
     * Returns false (and costs a life) if it is wrong.
     */
    public boolean placeNextLetter(char letter) {
        char expected = targetWord.charAt(stackedLetters.size());
        if (Character.toUpperCase(letter) == expected) {
            stackedLetters.add(expected);
            return true;
        } else {
            loseLife();
            return false;
        }
    }

    /**
     * Discard a block. If its letter was actually needed next, lose a life.
     */
    public void discardLetter(char letter) {
        discardedThisAttempt++;
        char expected = targetWord.charAt(stackedLetters.size());
        if (Character.toUpperCase(letter) == expected) {
            loseLife(); // penalise throwing away a needed letter
        }
    }

    // Queries

    public boolean isWordComplete() {
        return stackedLetters.size() == targetWord.length();
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    /** Returns e.g. "_ _ A T" when "CAT" is the word and 'A','T' stacked. */
    public String getDisplayWord() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < targetWord.length(); i++) {
            if (i < stackedLetters.size()) {
                sb.append(stackedLetters.get(i));
            } else {
                sb.append('_');
            }
            if (i < targetWord.length() - 1) sb.append(' ');
        }
        return sb.toString();
    }

    // Getters

    public String getTargetWord()   { return targetWord; }
    public String getCategoryName() { return categoryName; }
    public int    getLives()        { return lives; }
    public int    getLevel()        { return level + 1; } // 1-based for display
    public List<Character> getStackedLetters() { return stackedLetters; }
    public int    getNextLetterIndex() { return stackedLetters.size(); }
    public char   getNextExpectedLetter() {
        if (isWordComplete()) return 0;
        return targetWord.charAt(stackedLetters.size());
    }
}
