import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PingPongGameClient extends PingPongGame {

    public void startGame(Stage stage, Socket socket) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        listenToSerer(in);

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) up = true;
            if (e.getCode() == KeyCode.DOWN) down = true;
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.UP) up = false;
            if (e.getCode() == KeyCode.DOWN) down = false;
        });

        new AnimationTimer() {
            private long lastUpdate = 0;
            private long lastFpsTime = 0;
            private int frameCount = 0;
            private int currentFps = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate > 0) {
                    double deltaSeconds = (now - lastUpdate) / 1_000_000_000.0;
                    update(out, deltaSeconds);
                }

                lastUpdate = now;

                frameCount++;
                if (now - lastFpsTime >= 1_000_000_000) { // раз на секунду
                    currentFps = frameCount;
                    frameCount = 0;
                    lastFpsTime = now;
                }

                draw(gc, currentFps);
            }
        }.start();

        stage.setTitle("Ping Pong (JavaFX) Client");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void update(ObjectOutputStream out, double delta) {
        // Update local paddle
        if (up && clientPaddleY > 0) clientPaddleY -= paddleSpeed * delta;
        if (down && clientPaddleY < HEIGHT - 100) clientPaddleY += paddleSpeed * delta;

        GameState gameState = new GameState(clientPaddleY);

        try {
            out.writeObject(gameState);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenToSerer(ObjectInputStream in){
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    GameState received = (GameState) in.readObject();

                    // Updating
                    serverPaddleY = received.paddleY;
                    ballX = received.ballX;
                    ballY = received.ballY;
                    ballDX = received.ballDX;
                    ballDY = received.ballDY;
                    clientScore = received.clientScore;
                    serverScore = received.serverScore;
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Сервер відключився або сталася помилка: " + e.getMessage());
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}

