package tgm.geyerritter.dezsys06.gui;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * TODO class comment
 */
public class ConnectWindow extends Application implements Initializable, GUIPrinter {

    @FXML
    private GridPane root;

    public ConnectWindow() {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/connectwindow.fxml"));
        loader.setController(this);

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

    }

    @Override
    public void print(String text) {

    }
}
