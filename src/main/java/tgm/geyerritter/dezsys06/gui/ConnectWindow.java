package tgm.geyerritter.dezsys06.gui;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
public class ConnectWindow extends Application implements Initializable, GUIPrinter {

    @FXML
    private GridPane root;

    @FXML
    private TextField ip, chatroom, username;

    @FXML
    private Button connect;

    private ChatConsoleReader chatConsoleReader;


    public ConnectWindow() {

        // Konsolennachrichten abfangen
        Logger.getRootLogger().addAppender(new GUIAppender(this));

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

        primaryStage.setTitle("Connect to Message Broker");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        connect.setOnAction((event) -> {

            String line = "vsdbchat " + ip.getText() + " " + username.getText() + " " +  chatroom.getText();
            String label = "";
            String[] args = line.split(" ");
            if (args.length > 0) {
                label = args[0];
                args = Arrays.copyOfRange(args, 1, args.length);
            }

            this.chatConsoleReader.proccessCommand(label, args);


        });

    }

    @Override
    public void print(String text) {

        //TODO print into gui text area

    }
}
