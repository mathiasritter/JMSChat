package tgm.geyerritter.dezsys06.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import tgm.geyerritter.dezsys06.Broker;
import tgm.geyerritter.dezsys06.io.ChatConsoleReader;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConnectWindow extends Application implements Initializable, GUIPrinter {

    @FXML
    private BorderPane root;

    @FXML
    private TextField ip, chatroom, username;

    @FXML
    private Button connect;

    @FXML
    private Button startBrokerConnect;

    @FXML
    private TextArea out;

    private ChatConsoleReader chatConsoleReader;
    private Appender appender;
    private Stage primaryStage;


    public ConnectWindow() {

        // Konsolennachrichten abfangen
        this.appender = new GUIAppender(this);
        Logger.getRootLogger().addAppender(appender);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/connectwindow.fxml"));
        loader.setController(this);

        this.chatConsoleReader = ChatConsoleReader.getInstance();

        try {
            this.root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(root);
        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");

        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setTitle("Connect to Message Broker");
        primaryStage.setScene(scene);
        primaryStage.show();

        this.ip.requestFocus();

        this.primaryStage = primaryStage;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        connect.setOnAction((event) -> {
            this.connect.setDisable(true);
            this.startBrokerConnect.setDisable(true);
            new Thread(this::connect).start();
        });

        startBrokerConnect.setOnAction((event) -> {
            this.connect.setDisable(true);
            this.startBrokerConnect.setDisable(true);
            new Thread(() -> {
                if (Broker.start()) {
                    if (!this.connect()) {
                        Broker.stop();
                    };
                } else {
                    this.reset();
                }
            }).start();
        });

        chatroom.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {

                new Thread(this::connect).start();
            }
        });

    }

    @Override
    public void print(String text) {
        out.appendText(text + "\n");
    }

    private boolean connect() {

        this.connect.setDisable(true);
        this.startBrokerConnect.setDisable(true);

        String line = ip.getText() + " " + username.getText() + " " +  chatroom.getText();
        String label = "vsdbchat";
        String[] args = line.split(" ");

        this.chatConsoleReader.proccessCommand(label, args);

        if (this.chatConsoleReader.connectionEstablished()) {

            try {
                ChatWindow chatWindow = new ChatWindow();

                Platform.runLater(() -> {
                    chatWindow.start(new Stage());
                    this.primaryStage.close();
                });
                Logger.getRootLogger().removeAppender(this.appender);
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        } else {
            this.reset();
            return false;
        }
    }

    private void reset() {
        Platform.runLater(() -> {
            this.connect.setDisable(false);
            this.startBrokerConnect.setDisable(false);
            this.ip.requestFocus();
        });
    }
}
