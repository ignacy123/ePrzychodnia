package view;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AdministracjaView extends Application {
    @FXML
    private Label nameLabel;
    @FXML
    private Button logOutButton;

    Integer id;
    String name;
    Stage mainStage;

    AdministracjaView(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @FXML
    void initialize() {
        nameLabel.setText(name);
        logOutButton.setOnAction(actionEvent -> {
            Application view = new MainViewController();
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("administracja.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - administracja");
        mainStage = stage;
    }
}