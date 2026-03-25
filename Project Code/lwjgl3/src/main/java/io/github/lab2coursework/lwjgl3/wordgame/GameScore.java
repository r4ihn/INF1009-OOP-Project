package io.github.lab2coursework.lwjgl3.wordgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tracks score, combo multiplier, and target word progress.
 */
public class GameScore {

    public static final int TARGET_WORD_COUNT = 3;

    private static final int COMBO_THRESHOLD = 3;
    private static final int BASE_WORD_POINTS = 100;
    private static final int COMBO_BONUS = 150;

    private int totalScore;
    private int comboCount;
    private final List<String> targetWords;
    private final List<Boolean> wordCompleted;
    private final List<Integer> wordPoints;

    public GameScore() {
        this.totalScore = 0;
        this.comboCount = 0;
        this.targetWords = new ArrayList<>();
        this.wordCompleted = new ArrayList<>();
        this.wordPoints = new ArrayList<>();
    }

    public void setTargetWords(List<String> words) {
        if (words.size() != TARGET_WORD_COUNT) {
            throw new IllegalArgumentException("Must provide exactly " + TARGET_WORD_COUNT + " target words");
        }

        targetWords.clear();
        targetWords.addAll(words);
        wordCompleted.clear();
        wordPoints.clear();

        for (int i = 0; i < TARGET_WORD_COUNT; i++) {
            wordCompleted.add(false);
            wordPoints.add(BASE_WORD_POINTS);
        }
    }

    public void completeWord(int wordIndex) {
        if (!isValidWordIndex(wordIndex) || wordCompleted.get(wordIndex)) {
            return;
        }

        wordCompleted.set(wordIndex, true);
        int points = BASE_WORD_POINTS;

        comboCount++;
        if (comboCount % COMBO_THRESHOLD == 0) {
            points += COMBO_BONUS;
        }

        totalScore += points;
        wordPoints.set(wordIndex, points);
    }

    public void resetWordProgress(int wordIndex) {
        if (!isValidWordIndex(wordIndex)) {
            return;
        }
        wordCompleted.set(wordIndex, false);
        wordPoints.set(wordIndex, 0);
    }


    public void addScore(int points) {
        totalScore += points;
    }

    public void resetForNextLevel() {
        comboCount = 0;
        for (int i = 0; i < TARGET_WORD_COUNT; i++) {
            wordCompleted.set(i, false);
            wordPoints.set(i, BASE_WORD_POINTS);
        }
    }

    public boolean allWordsCompleted() {
        for (boolean completed : wordCompleted) {
            if (!completed) {
                return false;
            }
        }
        return true;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getComboCount() {
        return comboCount;
    }

    public List<String> getTargetWords() {
        return Collections.unmodifiableList(targetWords);
    }

    public int getWordPoints(int index) {
        if (!isValidWordIndex(index)) {
            return 0;
        }
        return wordPoints.get(index);
    }

    public String getTargetWord(int index) {
        if (!isValidWordIndex(index)) {
            return "";
        }
        return targetWords.get(index);
    }

    public boolean isWordCompleted(int index) {
        return isValidWordIndex(index) && wordCompleted.get(index);
    }

    private boolean isValidWordIndex(int index) {
        return index >= 0 && index < TARGET_WORD_COUNT;
    }
}
