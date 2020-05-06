package view;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class RecepcjonistkaView extends Application {
    @FXML
    private Label nameLabel;

    Integer id;
    String name;

    RecepcjonistkaView(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @FXML
    void initialize() {
        nameLabel.setText(name);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recepcjonistka.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }
}