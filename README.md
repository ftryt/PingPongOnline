# 🏓 Ping Pong Multiplayer Game (JavaFX)

A simple multiplayer Pong game built using JavaFX and Java sockets.

## 🔧 Features

- Local multiplayer over LAN
- Host & join game menu
- Paddle and ball sync with GameState class
- Display FPS counter and live score

---

## ▶️ Run Instructions

### 📦 Requirements

- Java 17+ (or compatible)
- JavaFX SDK (e.g., `javafx-sdk-24.0.1`)
- Maven *(optional but recommended)*

### 1. Install JavaFX

- Download the JavaFX SDK from [https://openjfx.io/](https://openjfx.io/)
- Unzip and place in the root project folder or in a convenient location

### 2. Compile project

```bash
    javac --module-path ".\javafx-sdk-24.0.1\lib" \
          --add-modules javafx.controls,javafx.fxml \
          ./src/main/java/*.java \
          -d target
```

### 3. Run

```bash
    java --module-path ".\javafx-sdk-24.0.1\lib" \
         --add-modules javafx.controls,javafx.fxml \
         -cp target \
         Main
```
