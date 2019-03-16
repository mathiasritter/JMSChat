package tgm.geyerritter.dezsys06.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import tgm.geyerritter.dezsys06.Broker;
import tgm.geyerritter.dezsys06.io.ChatConsoleReader;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * TODO class comment
 */
public class ChatWindow extends Application implements Initializable, GUIPrinter {

    @FXML
    private BorderPane root;

    @FXML
    private TextArea showMessages;

    @FXML
    private TextField writeMessage, writePrivateMessage, receiver;

    @FXML
    private Button sendMessage, query, sendPrivateMessage;

    @FXML
    private TabPane tabPane;

    private ChatConsoleReader chatConsoleReader;

    public ChatWindow() {

        Logger.getRootLogger().addAppender(new GUIAppender(this));

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/chatwindow.fxml"));
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

        Scene scene = new Scene(this.root);
        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");

        primaryStage.setOnCloseRequest(e -> {
            String empty[] = null;
            this.chatConsoleReader.proccessCommand("exit", empty);
        });
        primaryStage.setTitle("Chat: " + ChatConsoleReader.getInstance().getController().getChatroom() + "@" + ChatConsoleReader.getInstance().getController().getIP());
        primaryStage.setScene(scene);
        primaryStage.show();

        this.requestFocusWithoutSelection(this.writeMessage);

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

        this.tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            switch (newTab.getId()) {
                case "publicChat": Platform.runLater(() -> this.requestFocusWithoutSelection(this.writeMessage));
                case "privateChat": Platform.runLater(() -> this.requestFocusWithoutSelection(this.writePrivateMessage));
            }
        });

    }

    @Override
    public void print(String text) {
        Platform.runLater(() -> showMessages.appendText(text + "\n"));
    }

    private void sendMessage() {

        this.sendMessage.setDisable(true);
        this.writeMessage.setDisable(true);

        String line = writeMessage.getText();

        String label = "";
        String[] args = line.split(" ");

        new Thread(() -> this.chatConsoleReader.proccessCommand(label, args)).start();

        this.writeMessage.setText("Sending message...");

        // anti spam
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                this.writeMessage.clear();
                this.sendMessage.setDisable(false);
                this.writeMessage.setDisable(false);
                this.requestFocusWithoutSelection(this.writeMessage);
            });
        }).start();


    }

    private void sendPrivateMessage() {

        this.sendPrivateMessage.setDisable(true);
        this.writePrivateMessage.setDisable(true);
        this.receiver.setDisable(true);

        String line = receiver.getText() + " " + writePrivateMessage.getText();

        String label = "mail";
        String[] args = line.split(" ");

        new Thread(() -> this.chatConsoleReader.proccessCommand(label, args));

        this.writePrivateMessage.setText("Sending message...");

        // anti spam
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                this.writePrivateMessage.clear();
                this.receiver.clear();
                this.sendPrivateMessage.setDisable(false);
                this.writePrivateMessage.setDisable(false);
                this.receiver.setDisable(false);
                this.requestFocusWithoutSelection(this.writePrivateMessage);
            });
        }).start();

    }

    private void queryPrivateMessages() {

        this.query.setDisable(true);

        String label = "mailbox";
        String[] args = {};
        new Thread(() -> this.chatConsoleReader.proccessCommand(label, args)).start();

        // anti spam
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                this.query.setDisable(false);
                this.requestFocusWithoutSelection(this.writePrivateMessage);
            });
        }).start();
    }

    private void requestFocusWithoutSelection(TextField textField) {
        textField.requestFocus();
        int textLength = textField.getText().length();
        textField.selectRange(textLength, textLength);
    }
}
