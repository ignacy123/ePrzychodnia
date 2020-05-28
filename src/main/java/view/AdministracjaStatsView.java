package view;

import db.DatabaseService;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AdministracjaStatsView extends Application {
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private Button returnButton;

    Stage mainStage;
    Integer id;
    String name;
    DatabaseService db;

    AdministracjaStatsView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
    }
    @FXML
    void initialize(){
        returnButton.setOnAction(actionEvent -> {
            Application view = new AdministracjaView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        fromDatePicker.getEditor().setDisable(true);
        toDatePicker.getEditor().setDisable(true);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("administracjaStats.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - info o pracownikach");
        mainStage = stage;
    }
}
