package io.github.lab2coursework.lwjgl3.wordgame;

import java.util.Arrays;
import java.util.List;

public class WordBank {
    private final List<WordCategory> categories;

    public WordBank() {
        categories = Arrays.asList(
            new WordCategory("Animals", Arrays.asList(
                "CAT", "DOG", "FOX", "OWL", "RAT",
                "BEAR", "FROG", "LION", "WOLF", "DUCK"
            )),
            new WordCategory("Fruits", Arrays.asList(
                "FIG", "LIME", "PLUM", "KIWI", "PEAR",
                "GRAPE", "MANGO", "LEMON", "PEACH", "APPLE"
            )),
            new WordCategory("Colors", Arrays.asList(
                "RED", "BLUE", "GOLD", "PINK", "TEAL",
                "CYAN", "LIME", "NAVY", "JADE", "ROSE"
            ))
        );
    }

    public List<WordCategory> getCategories() { return categories; }

    public WordCategory getCategory(int index) {
        return categories.get(index % categories.size());
    }

    public String getCategoryNameForWord(String word) {
        for (WordCategory cat : categories) {
            for (String w : cat.getWords()) {
                if (w.equalsIgnoreCase(word)) return cat.getName();
            }
        }
        return "Unknown";
    }
}
