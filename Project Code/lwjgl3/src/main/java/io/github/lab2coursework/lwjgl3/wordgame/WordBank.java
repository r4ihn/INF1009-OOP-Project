package io.github.lab2coursework.lwjgl3.wordgame;

import java.util.Arrays;
import java.util.List;

/**
 * In-memory dictionary grouped by themed categories.
 */
public class WordBank {
    private final List<WordCategory> categories;

    public WordBank() {
        categories = Arrays.asList(
            new WordCategory("Animals", Arrays.asList(
                // 3-letter words
                "CAT", "DOG", "FOX", "OWL", "RAT", "BAT", "ANT", "BEE", "ELK", "GNU",
                // 4-letter words
                "BEAR", "FROG", "LION", "WOLF", "DUCK", "FISH", "BIRD", "CRAB", "GOAT", "HARE",
                "MOLE", "NEWT", "PUMA", "SEAL", "TOAD"
            )),
            new WordCategory("Fruits", Arrays.asList(
                // 3-letter words
                "FIG", "LIME", "PLUM", "KIWI", "PEAR", "DATE", "APRICOT",
                // 4-letter words
                "GRAPE", "MANGO", "LEMON", "PEACH", "APPLE", "MELON", "BERRY", "PRUNE", "OLIVE", "PAPAYA"
            )),
            new WordCategory("Colors", Arrays.asList(
                // 3-letter words
                "RED", "BLUE", "GOLD", "PINK", "TEAL", "TAN", "GRAY", "CYAN",
                // 4-letter words
                "NAVY", "JADE", "ROSE", "AQUA", "LIME", "MINT", "OLIVE", "CORAL", "INDIGO", "BEIGE"
            )),
            new WordCategory("Objects", Arrays.asList(
                // 3-letter words
                "BAG", "BOX", "CAR", "CUP", "KEY", "PEN", "TOY", "HAT", "JAR", "RUG",
                // 4-letter words
                "BOOK", "DOOR", "LAMP", "RING", "SHOE", "WALL", "ROPE", "FORK", "BOWL", "COIN"
            )),
            new WordCategory("Actions", Arrays.asList(
                // 3-letter words
                "RUN", "SIT", "EAT", "FLY", "HOP", "DIG", "CRY", "JOG",
                // 4-letter words
                "JUMP", "WALK", "SWIM", "PLAY", "SING", "TALK", "READ", "DRAW", "COOK", "SPIN"
            ))
        );
    }

    public List<WordCategory> getCategories() { return categories; }

    public WordCategory getCategory(int index) {
        // Wrap index so callers can request categories without bounds checks.
        return categories.get(index % categories.size());
    }

    public String getCategoryNameForWord(String word) {
        // Linear scan is fine here because the dictionary is small and static.
        for (WordCategory cat : categories) {
            for (String w : cat.getWords()) {
                if (w.equalsIgnoreCase(word)) return cat.getName();
            }
        }
        return "Unknown";
    }
}
