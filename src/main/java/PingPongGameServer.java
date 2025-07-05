import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PingPongGameServer extends PingPongGame {
    public void startGame(Stage stage, Socket client) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(client.getInputStream());

        listenToClient(in);

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

        stage.setTitle("Ping Pong (JavaFX) Server");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void update(ObjectOutputStream out, double delta) {
        // Update local paddle
        if (up && serverPaddleY > 0) serverPaddleY -= paddleSpeed * delta;
        if (down && serverPaddleY < HEIGHT - 100) serverPaddleY += paddleSpeed * delta;

        // Ball movement
        ballX += ballDX * delta;
        ballY += ballDY * delta;

        // Bounce from walls and prevent ball from escaping and
        if (ballY <= 0) {
            ballY = 0;
            ballDY *= -1;
        }
        if (ballY >= HEIGHT - ballDiametr) {
            ballY = HEIGHT - ballDiametr;
            ballDY *= -1;
        }

        // Bouncing from a racket (server)
        if (ballX <= 30 && ballY + ballDiametr >= serverPaddleY && ballY <= serverPaddleY + 100) {
            ballDX *= -1;
            ballX = 30;
        }

        // Bouncing from a racket (client)
        if (ballX >= WIDTH - ballDiametr - 20 && ballY + ballDiametr >= clientPaddleY && ballY <= clientPaddleY + 100) {
            ballDX *= -1;
            ballX = WIDTH - ballDiametr - 20;
        }

        if (ballX < 0) clientScore++;
        if (ballX > WIDTH) serverScore++;

        // Рестарт, якщо м'яч вилетів
        if (ballX < 0 || ballX > WIDTH) {
            ballX = WIDTH / 2;
            ballY = HEIGHT / 2;
        }

        // Send to client updated game state
        GameState gameState = new GameState(ballX, ballY, ballDX, ballDY, serverPaddleY, clientScore, serverScore);

        try {
            out.writeObject(gameState);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenToClient(ObjectInputStream in){
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    GameState received = (GameState) in.readObject();

                    // Updating
                    clientPaddleY = received.paddleY;
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Клієнт відключився або сталася помилка: " + e.getMessage());
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
