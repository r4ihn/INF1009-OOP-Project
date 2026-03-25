package io.github.lab2coursework.lwjgl3.wordgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds all mutable state for one playthrough of the word game.
 */
public class WordGameState {

    public static final int MAX_LIVES = 3;

    private final WordBank wordBank;
    private final GameScore gameScore;
    private int level;
    private int lives;

    private final List<String> targetWords = new ArrayList<>();
    private final List<List<Character>> stackedLettersPerWord = new ArrayList<>();

    public WordGameState(WordBank wordBank) {
        this.wordBank = wordBank;
        this.gameScore = new GameScore();
        this.level = 0;
        this.lives = MAX_LIVES;
        loadWordsForLevel();
    }

    private void loadWordsForLevel() {
        targetWords.clear();
        stackedLettersPerWord.clear();

        int categoryIndex = (int) (Math.random() * wordBank.getCategories().size());
        WordCategory category = wordBank.getCategory(categoryIndex);
        List<String> words = new ArrayList<>(category.getWords());
        java.util.Collections.shuffle(words);

        for (int i = 0; i < GameScore.TARGET_WORD_COUNT && i < words.size(); i++) {
            targetWords.add(words.get(i).toUpperCase());
            stackedLettersPerWord.add(new ArrayList<>());
        }

        gameScore.setTargetWords(targetWords);
    }

    public void advanceLevel() {
        level++;
        lives = MAX_LIVES;
        gameScore.resetForNextLevel();
        loadWordsForLevel();
    }

    public void loseLife() {
        lives--;
        resetAttempt();
    }

    public void loseLifeOnly() {
        lives--;
    }

    public void resetAttempt() {
        for (List<Character> stack : stackedLettersPerWord) {
            stack.clear();
        }
    }

    public int peekMatchingWordIndex(char letter) {
        letter = Character.toUpperCase(letter);

        for (int wordIdx = 0; wordIdx < GameScore.TARGET_WORD_COUNT; wordIdx++) {
            if (gameScore.isWordCompleted(wordIdx)) {
                continue;
            }

            String word = targetWords.get(wordIdx);
            List<Character> stacked = stackedLettersPerWord.get(wordIdx);

            if (stacked.size() < word.length() && letter == word.charAt(stacked.size())) {
                return wordIdx;
            }
        }

        return -1;
    }

    public void resetWordProgress(int wordIndex) {
        if (!isValidWordIndex(wordIndex)) {
            return;
        }

        List<Character> stack = stackedLettersPerWord.get(wordIndex);
        if (stack.isEmpty() && !gameScore.isWordCompleted(wordIndex)) {
            return;
        }

        stack.clear();
        gameScore.resetWordProgress(wordIndex);
    }

    public int placeNextLetter(char letter) {
        letter = Character.toUpperCase(letter);

        int wordIdx = peekMatchingWordIndex(letter);
        if (wordIdx >= 0) {
            List<Character> stacked = stackedLettersPerWord.get(wordIdx);
            stacked.add(letter);

            if (stacked.size() == targetWords.get(wordIdx).length()) {
                gameScore.completeWord(wordIdx);
            }

            return wordIdx;
        }

        loseLife();
        return -1;
    }

    public void discardLetter(char letter) {
        // Discarding is intentionally free in this game mode.
    }

    public boolean isAllWordsComplete() {
        return gameScore.allWordsCompleted();
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    public List<String> getDisplayWords() {
        List<String> displays = new ArrayList<>();
        for (int i = 0; i < GameScore.TARGET_WORD_COUNT; i++) {
            displays.add(getDisplayWord(i));
        }
        return displays;
    }

    public String getDisplayWord(int wordIndex) {
        if (!isValidWordIndex(wordIndex)) {
            return "";
        }

        String word = targetWords.get(wordIndex);
        List<Character> stacked = stackedLettersPerWord.get(wordIndex);
        StringBuilder display = new StringBuilder();

        for (int i = 0; i < word.length(); i++) {
            display.append(i < stacked.size() ? stacked.get(i) : '_');
            if (i < word.length() - 1) {
                display.append(' ');
            }
        }
        return display.toString();
    }

    public char getNextExpectedLetter(int wordIndex) {
        if (!isValidWordIndex(wordIndex) || gameScore.isWordCompleted(wordIndex)) {
            return 0;
        }

        String word = targetWords.get(wordIndex);
        List<Character> stacked = stackedLettersPerWord.get(wordIndex);
        return stacked.size() < word.length() ? word.charAt(stacked.size()) : 0;
    }

    public String getTargetWord(int wordIndex) {
        if (!isValidWordIndex(wordIndex)) {
            return "";
        }
        return targetWords.get(wordIndex);
    }

    public List<String> getTargetWords() {
        return Collections.unmodifiableList(targetWords);
    }

    public int getLives() {
        return lives;
    }

    public int getLevel() {
        return level + 1;
    }

    public GameScore getGameScore() {
        return gameScore;
    }

    public int getTotalScore() {
        return gameScore.getTotalScore();
    }

    public int getComboCount() {
        return gameScore.getComboCount();
    }

    private boolean isValidWordIndex(int wordIndex) {
        return wordIndex >= 0 && wordIndex < GameScore.TARGET_WORD_COUNT;
    }
}
