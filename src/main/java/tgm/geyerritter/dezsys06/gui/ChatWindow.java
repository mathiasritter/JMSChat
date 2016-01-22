package tgm.geyerritter.dezsys06.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import tgm.geyerritter.dezsys06.io.ChatConsoleReader;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * TODO class comment
 */
public class ChatWindow extends Application implements Initializable, GUIPrinter {

    private GridPane root;

    @FXML
    private TextArea showMessages;

    @FXML
    private TextField writeMessage, writePrivateMessage, receiver;

    @FXML
    private Button sendMessage, query, sendPrivateMessage;

    private ChatConsoleReader chatConsoleReader;

    public ChatWindow() {

        Logger.getRootLogger().addAppender(new GUIAppender(this));

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/chatwindow.fxml"));
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

        Scene scene = new Scene(this.root);

        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            String empty[] = null;
            this.chatConsoleReader.proccessCommand("exit", empty);
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setTitle("Chat: " + ChatConsoleReader.getInstance().getController().getChatroom() + "@" + ChatConsoleReader.getInstance().getController().getIP());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.sendMessage.setOnAction((event) -> {
            this.sendMessage();
        });

        this.writeMessage.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.sendMessage();
            }
        });

        this.sendPrivateMessage.setOnAction((event) -> {
            this.sendPrivateMessage();
        });

        this.query.setOnAction((event) -> {
            this.queryPrivateMessages();
        });

        this.receiver.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.sendPrivateMessage();
            }
        });



    }

    @Override
    public void print(String text) {

        showMessages.appendText(text + "\n");

    }

    private void sendMessage() {

        String line = writeMessage.getText();

        String label = "";
        String[] args = line.split(" ");
        if (args.length > 0) {
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        this.chatConsoleReader.proccessCommand(label, args);

        this.writeMessage.clear();

    }

    private void sendPrivateMessage() {
        String line = receiver.getText() + " " + writePrivateMessage.getText();

        String label = "mail";
        String[] args = line.split(" ");

        this.chatConsoleReader.proccessCommand(label, args);

        this.writePrivateMessage.clear();
        this.receiver.clear();
    }

    private void queryPrivateMessages() {
        String label = "mailbox";
        String[] args = {};
        this.chatConsoleReader.proccessCommand(label, args);
    }
}
