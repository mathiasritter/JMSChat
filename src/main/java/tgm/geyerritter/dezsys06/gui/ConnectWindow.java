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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import tgm.geyerritter.dezsys06.io.ChatConsoleReader;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * TODO class comment
 */
public class ConnectWindow extends Application implements Initializable, GUIPrinter {

    @FXML
    private GridPane root;

    @FXML
    private TextField ip, chatroom, username;

    @FXML
    private Button connect;

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
            this.root = (GridPane) loader.load();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(root);

        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setTitle("Connect to Message Broker");
        primaryStage.setScene(scene);
        primaryStage.show();

        this.primaryStage = primaryStage;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        connect.setOnAction((event) -> {
            this.connect();
        });

        chatroom.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.connect();
            }
        });

    }

    @Override
    public void print(String text) {

        out.appendText(text + "\n");

    }

    private void connect() {
        String line = "vsdbchat " + ip.getText() + " " + username.getText() + " " +  chatroom.getText();
        String label = "";
        String[] args = line.split(" ");
        if (args.length > 0) {
            label = args[0];
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        this.chatConsoleReader.proccessCommand(label, args);

        if (this.chatConsoleReader.connectionEstablished()) {
            try {
                ChatWindow chatWindow = new ChatWindow();
                chatWindow.start(new Stage());
                this.primaryStage.close();
                Logger.getRootLogger().removeAppender(this.appender);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
