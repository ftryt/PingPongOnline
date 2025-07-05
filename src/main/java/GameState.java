import java.io.Serial;
import java.io.Serializable;

public class GameState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public double ballX, ballY;
    public double ballDX, ballDY;

    public double paddleY;

    public int clientScore, serverScore;

    GameState(double ballX, double ballY, double ballDX, double ballDY, double paddleY, int clientScore, int serverScore){
        this.ballX = ballX;
        this.ballY = ballY;
        this.ballDX = ballDX;
        this.ballDY = ballDY;
        this.paddleY = paddleY;
        this.clientScore = clientScore;
        this.serverScore = serverScore;
    }

    GameState(double paddleY){
        this.paddleY = paddleY;
    }
}
