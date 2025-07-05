import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public abstract class PingPongGame {

    protected double serverPaddleY = 200;
    protected double clientPaddleY = 200;
    protected double paddleSpeed = 500;
    protected boolean up, down;

    protected double ballX = 300, ballY = 200;
    protected double ballDX = -400, ballDY = 400;
    protected double ballDiametr = 20;

    protected static final int WIDTH = 640;
    protected static final int HEIGHT = 480;

    protected int serverScore = 0, clientScore = 0;

    protected void draw(GraphicsContext gc, int currentFps) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.WHITE);
        gc.fillRect(20, serverPaddleY, 10, 100); // Paddle server
        gc.fillRect(WIDTH - 20 - 10, clientPaddleY, 10, 100); // Paddle client
        gc.fillOval(ballX, ballY, ballDiametr, ballDiametr); // Ball

        gc.fillText("Score: " + serverScore + " : " + clientScore, WIDTH / 2 - 60, 30);

        // Fps display
        gc.setFill(Color.YELLOW);
        gc.setFont(javafx.scene.text.Font.font(15));
        gc.fillText("FPS: " + currentFps, WIDTH - 80, 20);
    }
}
