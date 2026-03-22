package io.github.lab2coursework.lwjgl3.wordgame;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks score, combo multiplier, and multiple target words for simultaneous spelling.
 */
public class GameScore {

    private int totalScore;
    private int comboCount;           // number of consecutive successful words
    private static final int COMBO_THRESHOLD = 3;  // every 3 words = combo bonus
    private static final int BASE_WORD_POINTS = 100;
    private static final int COMBO_BONUS = 150;

    // Track the 3 target words and their completion status
    private List<String> targetWords;
    private List<Boolean> wordCompleted;
    private List<Integer> wordPoints;

    public GameScore() {
        this.totalScore = 0;
        this.comboCount = 0;
        this.targetWords = new ArrayList<>();
        this.wordCompleted = new ArrayList<>();
        this.wordPoints = new ArrayList<>();
    }

    /**
     * Initialize with 3 target words
     */
    public void setTargetWords(List<String> words) {
        if (words.size() != 3) {
            throw new IllegalArgumentException("Must provide exactly 3 target words");
        }
        this.targetWords = new ArrayList<>(words);
        this.wordCompleted = new ArrayList<>();
        this.wordPoints = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            wordCompleted.add(false);
            wordPoints.add(BASE_WORD_POINTS);
        }
    }

    /**
     * Mark a word as completed and add points
     */
    public void completeWord(int wordIndex) {
        if (wordIndex < 0 || wordIndex >= 3) return;
        if (wordCompleted.get(wordIndex)) return; // already completed

        wordCompleted.set(wordIndex, true);
        int points = BASE_WORD_POINTS;

        // Apply combo bonus
        comboCount++;
        if (comboCount % COMBO_THRESHOLD == 0) {
            points += COMBO_BONUS;
        }

        totalScore += points;
        wordPoints.set(wordIndex, points);
    }

    /**
     * Reset for next level
     */
    public void resetForNextLevel() {
        comboCount = 0;
        for (int i = 0; i < 3; i++) {
            wordCompleted.set(i, false);
            wordPoints.set(i, BASE_WORD_POINTS);
        }
    }

    /**
     * Check if all 3 words are completed
     */
    public boolean allWordsCompleted() {
        return wordCompleted.get(0) && wordCompleted.get(1) && wordCompleted.get(2);
    }

    /**
     * Get the index of the first incomplete word, or -1 if all done
     */
    public int getFirstIncompleteWordIndex() {
        for (int i = 0; i < 3; i++) {
            if (!wordCompleted.get(i)) {
                return i;
            }
        }
        return -1;
    }

    // Getters
    public int getTotalScore() { return totalScore; }
    public int getComboCount() { return comboCount; }
    public List<String> getTargetWords() { return targetWords; }
    public List<Boolean> getWordCompleted() { return wordCompleted; }
    public List<Integer> getWordPoints() { return wordPoints; }
    public String getTargetWord(int index) {
        if (index < 0 || index >= 3) return "";
        return targetWords.get(index);
    }
    public boolean isWordCompleted(int index) {
        if (index < 0 || index >= 3) return false;
        return wordCompleted.get(index);
    }
}
