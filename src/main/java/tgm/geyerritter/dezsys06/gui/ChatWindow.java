package tgm.geyerritter.dezsys06.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    private Pane root;

    @FXML
    private TextArea showMessages;

    @FXML
    private TextField writeMessage;

    @FXML
    private Button sendMessage;

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

        primaryStage.setTitle("Chat-Window");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sendMessage.setOnAction((event) -> {
            this.sendMessage();
        });

        writeMessage.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.sendMessage();
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
            label = args[0];
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        this.chatConsoleReader.proccessCommand(label, args);

        this.writeMessage.clear();

    }
}
