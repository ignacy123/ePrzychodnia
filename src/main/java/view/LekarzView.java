package view;

import Model.Person;
import Model.Visit;
import converters.OfficeConverter;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

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
    @FXML
    private Button logOutButton;
    @FXML
    private Text surnameText;
    @FXML
    private Text nameText;
    @FXML
    private Text dateText;
    @FXML
    private Text officeText;
    @FXML
    private Button nextVisitEditButton;

    Integer id;
    String name;
    DatabaseService db;
    Map<String, Integer> patients;
    ObservableList patientsToShow;
    Stage mainStage;
    Visit currentVisit = null;

    LekarzView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
        patientsToShow = FXCollections.observableArrayList();
    }

    @FXML
    void initialize() {
        patientsListView.setOnMouseClicked(mouseEvent -> {
            if (patientsListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
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
                if (s1.toLowerCase().contains(" " + String.valueOf(surnameTextField.getCharacters()).toLowerCase())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
        });
        nameLabel.setText(name);
        patients = db.getPatients(id);
        fillPatients(patients.keySet());
        patientsListView.setItems(patientsToShow);
        datePicker.setValue(LocalDate.now());
        datePicker.getEditor().setDisable(true);
        futureVisits.setItems(FXCollections.observableArrayList(db.getFutureVisits(id)).sorted((visit, t1) -> {
            if (t1.getStart().getTime() < visit.getStart().getTime()) {
                return 1;
            }
            return 0;
        }));
        Visit nextVisit = db.getNextVisit(id);
        if(nextVisit.getPatient()!=null){
            surnameText.setText(nextVisit.getPatient().getLastName());
            nameText.setText(nextVisit.getPatient().getName());
            dateText.setText(String.valueOf(nextVisit.getStart()));
            officeText.setText(new OfficeConverter().toString(nextVisit.getOffice()));
        }else{
            nextVisitEditButton.setDisable(true);
        }
        logOutButton.setOnAction(actionEvent -> {
            Application view = new MainViewController();
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        nextVisitEditButton.setOnAction(actionEvent -> {
            System.out.println("I want to edit!");
            Application view = new VisitEditView(id, name, db, nextVisit);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("lekarz.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - lekarz");
        mainStage = stage;
    }

    private void setVisitClicker(ListView futureVisits) {
        futureVisits.setOnMouseClicked(mouseEvent -> {
            Dialog d = new Dialog();
            d.setResizable(true);
            Window window = d.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(e -> window.hide());
            if (futureVisits.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            currentVisit = (Visit) futureVisits.getSelectionModel().getSelectedItems().get(0);
            d.setContentText(String.valueOf(currentVisit));
            ButtonType editButtontype = new ButtonType("Edytuj", ButtonBar.ButtonData.APPLY);
            d.getDialogPane().getButtonTypes().addAll(editButtontype);
            d.setResultConverter(new Callback<ButtonType, Boolean>() {
                @Override
                public Boolean call(ButtonType buttonType) {
                    if (buttonType == editButtontype) {
                        return true;
                    }
                    return false;
                }
            });
            Optional<Boolean> result = d.showAndWait();
            if (result.isPresent()) {
                System.out.println("I want to edit!");
                Application view = new VisitEditView(id, name, db, currentVisit);
                try {
                    view.start(mainStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("I don't want to edit!");
            }
        });
    }

    public void fillPatients(Set<String> list) {
        patientsToShow.clear();
        patientsToShow.addAll(list);
    }
}
