package view;

import Model.Person;
import Model.Visit;
import converters.VisitConverter;
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
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class LekarzView extends Application {
    @FXML
    private Label nameLabel;
    @FXML
    private ListView patientsListView;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ListView dateVisits;
    @FXML
    private ListView futureVisits;
    @FXML
    private TextField surnameTextField;

    Integer id;
    String name;
    DatabaseService db;
    Map<String, Integer> patients;
    ObservableList patientsToShow;

    LekarzView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
        patientsToShow = FXCollections.observableArrayList();
    }

    @FXML
    void initialize() {
        nameLabel.setText(name);
        patients = db.getPatients(id);
        fillPatients(patients.keySet());
        patientsListView.setItems(patientsToShow);
        datePicker.setValue(LocalDate.now());
        futureVisits.setItems(FXCollections.observableArrayList(db.getFutureVisits(id)));
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("lekarz.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        patientsListView.setOnMouseClicked(mouseEvent -> {
            Person patient = db.getPerson(patients.get(patientsListView.getSelectionModel().getSelectedItems().get(0)));
            Dialog d = new Dialog();
            d.setResizable(true);
            Window window = d.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(e -> window.hide());
            d.setContentText(String.valueOf(patient));
            d.show();
        });
        dateVisits.setCellFactory(listView -> {
            TextFieldListCell<Visit> cell = new TextFieldListCell<>();
            cell.setConverter(new VisitConverter());
            return cell;

        });
        futureVisits.setCellFactory(listView -> {
            TextFieldListCell<Visit> cell = new TextFieldListCell<>();
            cell.setConverter(new VisitConverter());
            return cell;

        });
        datePicker.setOnAction(actionEvent -> {
            LocalDate date = datePicker.getValue();
            System.out.println(date);
            List<Visit> visits = db.getDayVisitsFromDoctor(id, date);
            dateVisits.setItems(FXCollections.observableArrayList(visits));
        });
        setVisitClicker(dateVisits);
        setVisitClicker(futureVisits);
        surnameTextField.textProperty().addListener((observableValue, s, t1) -> {
            patientsToShow.clear();
            patientsToShow.addAll(patients.keySet().stream().filter(s1 -> {
                if(s1.contains(" "+surnameTextField.getCharacters())){
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
        });
    }

    private void setVisitClicker(ListView futureVisits) {
        futureVisits.setOnMouseClicked(mouseEvent -> {
            Dialog d = new Dialog();
            d.setResizable(true);
            Window window = d.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(e -> window.hide());
            d.setContentText(String.valueOf(futureVisits.getSelectionModel().getSelectedItems().get(0)));
            d.show();
        });
    }

    public void fillPatients(Set<String> list){
        patientsToShow.clear();
        patientsToShow.addAll(list);
    }
}
