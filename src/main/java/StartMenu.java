import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class StartMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        // ===== UI elements =====
        Label titleLabel = new Label("🎮 Ping Pong Multiplayer");
        titleLabel.setFont(Font.font(24));
        titleLabel.setTextFill(Color.WHITE);

        Label portLabel = new Label("Preferred port:");
        portLabel.setTextFill(Color.WHITE);

        Label ipHostLabel = new Label("Enter host ip:");
        ipHostLabel.setTextFill(Color.WHITE);

        TextField portField = new TextField("12345");
        portField.setPromptText("Port");
        portField.setMaxWidth(100);

        TextField connectIpField = new TextField();
        connectIpField.setPromptText("Enter host IP");
        connectIpField.setMaxWidth(200);

        Button hostButton = new Button("Host a game...");
        Button joinButton = new Button("Connect to a game...");

        hostButton.setPrefWidth(200);
        joinButton.setPrefWidth(200);

        joinButton.setOnAction(e -> {
            String ip = connectIpField.getText();
            int port = Integer.parseInt(portField.getText());
            System.out.println("Joining " + ip + ":" + port);

            Socket socket = null;
            try {
                socket = new Socket(ip, port);
                new PingPongGameClient().startGame(primaryStage, socket);
            } catch (IOException err) {
                System.out.println("Can`t connect to a server! " + err);
            }
        });

        VBox layout = new VBox(15,
                titleLabel,
                portLabel, portField,
                ipHostLabel, connectIpField,
                hostButton, joinButton
        );
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2c3e50;");

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ping Pong Menu");
        primaryStage.setResizable(false);
        primaryStage.show();

        hostButton.setOnAction(e -> {
            int port = Integer.parseInt(portField.getText());

            WaitingScene waiting = new WaitingScene();
            waiting.show(primaryStage, port, scene);
        });
    }
}

