package io.github.lab2coursework.lwjgl3.wordgame;

import java.util.List;

/**
 * Immutable pairing of a category name and its candidate words.
 */
public class WordCategory {
    private final String name;
    private final List<String> words;

    public WordCategory(String name, List<String> words) {
        this.name = name;
        this.words = words;
    }

    public String getName() { return name; }
    public List<String> getWords() { return words; }
}
