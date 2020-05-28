package view;

import db.DatabaseService;
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
    @FXML
    private Button infoButton;
    @FXML
    private Button hireButton;
    @FXML
    private Button statsButton;

    Integer id;
    String name;
    Stage mainStage;
    DatabaseService db;

    AdministracjaView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
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
        infoButton.setOnAction(actionEvent -> {
            Application view = new AdministracjaInfoView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        statsButton.setOnAction(actionEvent -> {
            Application view = new AdministracjaStatsView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        hireButton.setOnAction(actionEvent -> {
            //TODO
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