# P4_Team5_ProjectPart1Final 
## OOP Abstract Game Engine project

A cross-platform game development project built with [libGDX](https://libgdx.com/), generated using [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

## Overview

This project serves as a foundation for building 2D/3D games and interactive applications using the libGDX framework. It includes a basic application structure with launchers and an `ApplicationAdapter` extension that demonstrates the libGDX logo rendering.

## Project Information

- **Project Name**: oopProjectWork
- **Version**: 1.0.0
- **libGDX Version**: 1.14.0
- **Java Version**: 8+
- **Build Tool**: Gradle

## Project Structure

### Modules

- **`core`**: Main module containing the application logic shared across all platforms. This is where your game code should reside.
- **`lwjgl3`**: Primary desktop platform using LWJGL3 (Lightweight Java Game Library). This module handles desktop-specific launching and configuration.

### Key Files

- `build.gradle`: Main build configuration for all modules
- `settings.gradle`: Defines which subprojects are included in the build
- `gradle.properties`: Project-wide properties including versions and JVM settings
- `gradlew` / `gradlew.bat`: Gradle wrapper scripts for Unix/Windows

## Prerequisites

- **Java Development Kit (JDK) 8 or higher**
- **No Gradle installation required** - The project includes Gradle Wrapper

## Getting Started

### Running the Application

On Unix/Mac:
```bash
./gradlew lwjgl3:run
```

On Windows:
```bash
gradlew.bat lwjgl3:run
```

### Building the Project

Build all modules:
```bash
./gradlew build
```

Create a runnable JAR:
```bash
./gradlew lwjgl3:jar
```

The JAR file will be located at `lwjgl3/build/libs/`.

## Gradle Tasks

### Essential Tasks

| Task | Description |
|------|-------------|
| `lwjgl3:run` | Starts the application |
| `lwjgl3:jar` | Builds application's runnable JAR |
| `build` | Builds sources and archives of every project |
| `clean` | Removes build folders |
| `test` | Runs unit tests (if any) |

### IDE Integration

| Task | Description |
|------|-------------|
| `idea` | Generates IntelliJ IDEA project data |
| `eclipse` | Generates Eclipse project data |
| `cleanIdea` | Removes IntelliJ project data |
| `cleanEclipse` | Removes Eclipse project data |

### Useful Flags

- `--continue`: Continue running tasks even if errors occur
- `--daemon`: Use Gradle daemon for faster builds
- `--offline`: Use cached dependencies (useful when offline)
- `--refresh-dependencies`: Force validation of all dependencies

### Module-Specific Tasks

You can run tasks for specific modules using the format `moduleName:taskName`:
```bash
./gradlew core:clean    # Only cleans the core module
./gradlew core:build    # Only builds the core module
```

## Development Workflow

### 1. Setting Up Your IDE

**IntelliJ IDEA:**
```bash
./gradlew idea
```
Then open the project in IntelliJ IDEA.

**Eclipse:**
```bash
./gradlew eclipse
```
Then import the project as an existing Gradle project.

### 2. Adding Dependencies

Add dependencies to the appropriate module's `build.gradle` file. Common dependencies go in the `core` module, while platform-specific dependencies go in their respective modules.

### 3. Asset Management

The project includes automatic asset list generation:
- Place your assets in the `assets/` folder
- An `assets.txt` file will be automatically generated during build
- This file lists all assets in your project

## Project Configuration

### Memory Settings

Configured in `gradle.properties`:
- **Initial Memory**: 512MB
- **Maximum Memory**: 1GB
- **Encoding**: UTF-8

### JVM Options

You can modify JVM options in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xms512M -Xmx1G -Dfile.encoding=UTF-8
```

## Supported Platforms

Currently configured for:
- **Desktop (LWJGL3)**: Primary platform for development and testing

The project structure supports adding additional platforms such as:
- Android
- iOS
- HTML5 (GWT/TeaVM)
- Headless server

## Contributing

When contributing to this project:
1. Keep platform-independent code in the `core` module
2. Place platform-specific code in the respective platform modules
3. Follow Java 8 compatibility guidelines
4. Run `./gradlew test` before committing

## Build Directory Structure

After building, the following structure will be created:
```
project-root/
├── build/              # Parent project build artifacts
├── core/build/         # Core module compiled classes and JARs
├── lwjgl3/build/       # Desktop platform build artifacts
│   └── libs/          # Runnable JAR location
└── assets/            # Game assets
    └── assets.txt     # Auto-generated asset list
```

## Troubleshooting

### Dependency Issues

If you encounter dependency download issues:
```bash
./gradlew --refresh-dependencies build
```

### Build Cache Issues

Clear all build artifacts:
```bash
./gradlew clean
```

### IDE Issues

Regenerate IDE project files:
```bash
./gradlew cleanIdea idea     # For IntelliJ IDEA
./gradlew cleanEclipse eclipse  # For Eclipse
```

## Resources

- [libGDX Official Documentation](https://libgdx.com/wiki/)
- [libGDX Community](https://libgdx.com/community/)
- [Gradle Documentation](https://docs.gradle.org/)
- [LWJGL Website](https://www.lwjgl.org/)

## License

This project template is provided by the libGDX project and follows their licensing terms.

## Additional Notes

- The project uses Gradle's foojay-resolver plugin for automatic JDK downloads
- Incremental compilation is enabled for faster builds
- Build output logging is set to "quiet" mode by default (can be changed in `gradle.properties`)

---

**Getting Started Tip**: Run `./gradlew lwjgl3:run` to see the demo application with the libGDX logo!
