import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class WaitingScene {

    private Thread serverThread;
    private ServerSocket serverSocket;

    public void show(Stage stage, int port, Scene mainMenuScene) {
        Label ipLabel = new Label();
        Label portLabel = new Label("Port: " + port);
        Label statusLabel = new Label("Waiting for player to connect...");
        Button cancelButton = new Button("Cancel");

        ipLabel.setText("Local IP address: " + getLocalIp());

        VBox layout = new VBox(15, ipLabel, portLabel, statusLabel, cancelButton);
        layout.setAlignment(Pos.CENTER);
        // layout.setStyle("-fx-background-color: #2c3e50;");
        Scene scene = new Scene(layout, 400, 400);

        stage.setScene(scene);

        // Server in a separate thread
        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                Socket client = serverSocket.accept();
                Platform.runLater(() -> {
                    try {
                        new PingPongGameServer().startGame(stage, client);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            } catch (IOException e) {
                if (!(e instanceof SocketException)) {
                    Platform.runLater(() -> statusLabel.setText("Error: Server failed to start server."));
                    e.printStackTrace();
                }
            }
        });

        serverThread.setDaemon(true);
        serverThread.start();

        cancelButton.setOnAction(e -> {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            stage.setScene(mainMenuScene);
        });
    }

    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                // Пропускаємо неактивні або віртуальні інтерфейси
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress(); // Наприклад, 192.168.0.101
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return "Unavailable";
    }

}
