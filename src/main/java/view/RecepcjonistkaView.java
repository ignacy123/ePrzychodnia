package view;

import Model.Person;
import Model.Specialization;
import converters.PersonConverter;
import converters.SpecializationConverter;
import db.DatabaseService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class RecepcjonistkaView extends Application {
    @FXML
    private Label nameLabel;
    @FXML
    private Button newVisitButton;
    @FXML
    private Button logOutButton;

    Integer id;
    String name;
    DatabaseService db;
    Stage mainStage;

    RecepcjonistkaView(int id, String name, DatabaseService db) {
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
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recepcjonistka.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - recepcjonistka");
        mainStage = stage;
        newVisitButton.setOnAction(actionEvent -> {
            Application view = new RecepcjonistkaWizytaView(id, name, db);
            try {
                view.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}