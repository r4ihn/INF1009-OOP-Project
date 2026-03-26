# 🏗️ Word Crane — OOP Educational Word Game

A physics-based word-spelling game built with **LibGDX (LWJGL3)** as part of an Object-Oriented Programming coursework project. Players control a crane to stack letter blocks and spell target words, combining real-time gameplay with vocabulary and spelling practice.

---

## 📚 Educational Purpose

Word Crane is designed to make learning engaging and effective by transforming passive vocabulary study into active play:

- **Spelling & Vocabulary** — Players must identify and sequence correct letters to complete target words drawn from themed categories (Animals, Fruits, Colors, Objects, Actions).
- **Memory & Attention** — Tracking multiple words simultaneously across three towers demands sustained focus and working memory.
- **Problem-Solving** — Deciding which block to drop, discard, or save under time pressure develops real-time decision-making skills.
- **Gamification** — Combo multipliers, lives, and level progression provide clear feedback loops that motivate continued play.
- **Physics-Based Interaction** — The pendulum rope mechanic makes every drop feel consequential, turning each correct letter placement into a rewarding physical interaction.

By blending game mechanics with language learning goals, Word Crane transforms rote spelling practice into an immersive and memorable experience.

---

## 🎮 Gameplay Overview

Three target words are displayed at the top of the screen. Letter blocks hang from a crane on a swinging rope, and the player must drop each block onto the correct tower to spell each word letter-by-letter from the ground up.

- **Drop too far off-centre** and the tower sways and collapses, resetting that word's progress.
- **Catch the right letter** and the block lands cleanly, building toward a completed word.
- **Discard unwanted blocks** by clicking the bin on the right side of the screen.
- **Complete all three words** to advance to the next level with a fresh set of words.
- **Lose all three lives** and the game ends, showing your highest level reached.

---

## 🕹️ Controls

| Input | Action |
|---|---|
| `A` / `←` | Move crane left |
| `D` / `→` | Move crane right |
| `SPACE` | Release hanging block |
| `Left Click` on bin | Discard hanging block |
| `P` / `ESC` | Pause game |
| `W` (title screen) | Start word game |
| `ESC` (title screen) | Exit application |
| `R` (end screen) | Play again |
| `ESC` (end screen) | Return to title |

---

## 🏗️ Project Architecture

The project follows a clean **OOP design** with well-separated responsibilities across the following packages:

### `core`
- **`GameMaster`** — Main LibGDX `ApplicationAdapter`. Wires all engine-level managers at startup and forwards frame events to the active screen via `ScreenManager`.
- **`StartupHelper`** — Ensures the JVM is launched with `-XstartOnFirstThread` on macOS and works around Windows username encoding issues.

### `screens`
Implements the **Template Method** pattern via `AbstractScreen`, which defines a fixed `render → update → draw` flow for all screens.

| Class | Role |
|---|---|
| `TitleScreen` | Main menu with controls and navigation |
| `WordGameScreen` | Thin wrapper; delegates to controller and renderer |
| `WordGameController` | All game logic, state transitions, and input handling |
| `WordGameRenderer` | All rendering — shapes, textures, HUD, and labels |
| `PauseScreen` | Pause overlay with resume/restart/quit options |
| `WordGameEndScreen` | Game-over screen displaying highest level reached |

Separating `WordGameController` from `WordGameRenderer` keeps game logic independent of rendering concerns.

### `entities`
All game objects extend the abstract `Entity` base class, which provides shared position, color, speed, and a pluggable **Strategy** slot for movement.

| Entity | Description |
|---|---|
| `CraneArm` | Player-controlled horizontal crane arm with a hook |
| `LetterBlock` | A block carrying a single letter; tracks landed/discarded state and which word tower it belongs to |
| `GarbageCan` | Static bin used to discard unwanted blocks |

### `movement`
Implements the **Strategy** pattern. Each `Movement` subclass encapsulates one movement behavior and is injected into an entity at runtime.

| Strategy | Behavior |
|---|---|
| `CraneMovement` | Keyboard-driven left/right movement with boundary clamping |
| `RopeSwingMovement` | Driven pendulum physics (gravity + pivot acceleration + drag) for the hanging block |
| `FallMovement` | Gravity-accelerated free fall after the block is released |
| `PlayerMovement` | Direction-flag-based movement for general player entities |
| `AIMovement` | Downward looping movement for AI-controlled entities |

### `collision`
Implements the **Rule** pattern — each `CollisionRule` is a self-contained behavior that the `CollisionManager` evaluates each frame.

| Rule | Behavior |
|---|---|
| `BlockLandingRule` | Handles landing, stacking, sway animation, and tower collapse/reset |
| `GarbageCollectionRule` | Detects overlap with the bin and discards the block |
| `KeepInBoundsRule` | Clamps entities within world boundaries |

Supporting classes keep `BlockLandingRule` focused on decisions rather than data:

- **`BlockLandingValidator`** — Pure AABB geometry checks (overlap, landing-on-top detection).
- **`BlockPlacementService`** — Applies final position and state changes after a landing decision.
- **`BlockStackTracker`** — Stores per-word tower height and last-landed X position.
- **`TowerSettlingController`** — Manages the sin-wave sway animation that plays before a stabilize or collapse outcome.

### `wordgame`
Contains all game-rules and data logic, fully decoupled from rendering.

| Class | Role |
|---|---|
| `WordGameState` | Central mutable state: lives, level, per-word letter progress |
| `GameScore` | Score, combo multiplier, and word-completion tracking |
| `LetterBlockFactory` | **Factory Method** pattern; spawns letter blocks biased toward remaining target letters (70% useful, 30% noise) |
| `WordBank` | In-memory dictionary of themed word categories |
| `WordCategory` | Immutable name + word list pairing |

### `managers`
Engine-level service classes shared across screens:

| Manager | Role |
|---|---|
| `ScreenManager` | Stack-based screen lifecycle (push/pop/set) |
| `EntityManager` | Owns the active entity list; forwards update and draw calls |
| `MovementManager` | Iterates entities and calls their movement strategy each frame |
| `CollisionManager` | Applies registered collision rules to entity pairs |
| `IOManager` | Global key-binding map forwarded to the active input handler |
| `DebugManager` | Runtime debug toggles (render overlay, FPS display) |

### `input`
A small input-binding system decoupled from LibGDX's raw key codes.

- **`Input`** (abstract) — Binding map base class.
- **`KeyboardInput`** / **`MouseInput`** — Concrete handlers that translate raw codes into `Action` callbacks.
- **`Key`** (enum) — Logical key constants with code mapping.
- **`Action`** (interface) — Single-method command interface (`execute()`).

---

## 🔑 Design Patterns Used

| Pattern | Where Applied |
|---|---|
| **Strategy** | Movement system (`Movement` and all subclasses) |
| **Template Method** | Screen lifecycle (`AbstractScreen`) |
| **Factory Method** | Letter block spawning (`LetterBlockFactory`) |
| **Rule / Chain of Responsibility** | Collision resolution (`CollisionRule` + `CollisionManager`) |
| **MVC-style separation** | `WordGameController` vs `WordGameRenderer` vs `WordGameState` |

---

## 🚀 Getting Started

### Prerequisites

- Java 8 or higher
- Gradle (wrapper included)

### Running the Game

```bash
# Clone the repository
git clone <repository-url>
cd <project-directory>

# Run the desktop application
./gradlew lwjgl3:run
```

On **macOS**, the JVM is automatically relaunched with `-XstartOnFirstThread` by `StartupHelper` if needed.

### Build

```bash
./gradlew lwjgl3:jar
```

---

## 📁 Project Structure

```
lwjgl3/src/main/java/io/github/lab2coursework/lwjgl3/
├── collision/          # Collision rules and supporting services
├── core/               # Application entry point and startup utilities
├── entities/           # Game object classes
├── graphics/           # Texture rendering helper
├── input/              # Input binding system
├── launch/             # LWJGL3 launcher and window configuration
├── managers/           # Engine-level service managers
├── movement/           # Movement strategy implementations
├── screens/            # Screen classes and game loop
└── wordgame/           # Game rules, state, scoring, and word data
```

---

## 🎯 Scoring

- Each completed word awards **100 base points**.
- Every **3rd consecutive word completion** triggers a **combo bonus of +150 points**.
- The combo counter resets between levels.
- Losing a life does **not** reset the score — only word tower progress is reset.

---

## 📝 License

This project was developed as coursework for an Object-Oriented Programming module. All game code is original student work.
