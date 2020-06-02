package view;

import Model.Office;
import Model.Person;
import Model.Referral;
import converters.PersonConverter;
import converters.ReferralConverter;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class RecepcjonistkaZabiegView extends Application {

    @FXML
    private ListView patientsListView;
    @FXML
    private TextField surnameTextField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField fromTextField;
    @FXML
    private TextField toTextField;
    @FXML
    private ListView nurseListView;
    @FXML
    private Button findNurseButton;
    @FXML
    private Label patientLabel;
    @FXML
    private Button backButton;
    @FXML
    private Button findOfficeButton;
    @FXML
    private ListView officeListView;
    @FXML
    private Label officeLabel;

    Integer id;
    String name;
    DatabaseService db;
    Stage mainStage;
    Map<String, Integer> patients;
    LocalDateTime date1;
    LocalDateTime date2;
    Person currentPatient = null;
    Person currentNurse = null;
    Office currentOffice = null;
    ObservableList<String> patientsToShow = FXCollections.observableArrayList();


    RecepcjonistkaZabiegView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
    }

    @FXML
    void initialize() {
        backButton.setOnAction(actionEvent -> {
            Application view = new RecepcjonistkaView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        patients = db.getPatients();
        patientsToShow.addAll(patients.keySet());
        datePicker.setValue(LocalDate.now());
        patientsListView.setItems(patientsToShow);
        surnameTextField.textProperty().addListener((observableValue, s, t1) -> {
            patientsToShow.clear();
            patientsToShow.addAll(patients.keySet().stream().filter(s1 -> {
                if (s1.toLowerCase().contains(" " + String.valueOf(surnameTextField.getCharacters()).toLowerCase())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
        });

        patientsListView.setOnMouseClicked(mouseEvent -> {
            if (patientsListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            currentPatient = db.getPerson(patients.get(patientsListView.getSelectionModel().getSelectedItems().get(0)));
            patientLabel.setText(currentPatient.getName() + " " + currentPatient.getLastName());
        });

        findNurseButton.setOnAction(actionEvent -> {
            if (currentPatient == null) {
                Dialog d = new Dialog();
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("dodaj pacjenta to się posortuje hehe");
                d.show();
                return;
            }
            Date hour = null;
            Date hour2 = null;
            try {
                hour = new SimpleDateFormat("HH:mm").parse(String.valueOf(fromTextField.getCharacters()));
                hour2 = new SimpleDateFormat("HH:mm").parse(String.valueOf(toTextField.getCharacters()));
            } catch (ParseException e) {
                System.out.println("to nie data debilu");
            }
            date1 = datePicker.getValue().atStartOfDay();
            date2 = datePicker.getValue().atStartOfDay();
            date1 = date1.plus(hour.getTime(), ChronoUnit.MILLIS);
            date1 = date1.plus(1, ChronoUnit.HOURS);
            date2 = date2.plus(hour2.getTime(), ChronoUnit.MILLIS);
            date2 = date2.plus(1, ChronoUnit.HOURS);

            if (date1.isBefore(LocalDateTime.now())) {
                Dialog d = new Dialog();
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("czasu nie cofniesz");
                d.show();
                return;
            }
            if (!date1.isBefore(date2)) {
                Dialog d = new Dialog();
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("wizyta nie może się skończyć zanim się zacznie deklu");
                d.show();
                return;
            }
            nurseListView.setItems(FXCollections.observableArrayList(db.getAvailableNursesAtTimeSortedByPatient(date1, date2, currentPatient.getId())));
            nurseListView.setCellFactory(listView -> {
                TextFieldListCell<Person> cell = new TextFieldListCell<>();
                cell.setConverter(new PersonConverter());
                return cell;
            });
        });

        findOfficeButton.setOnAction(actionEvent -> {
            Date hour = null;
            Date hour2 = null;
            try {
                hour = new SimpleDateFormat("HH:mm").parse(String.valueOf(fromTextField.getCharacters()));
                hour2 = new SimpleDateFormat("HH:mm").parse(String.valueOf(toTextField.getCharacters()));
            } catch (ParseException e) {
                System.out.println("to nie data debilu");
            }
            date1 = datePicker.getValue().atStartOfDay();
            date2 = datePicker.getValue().atStartOfDay();
            date1 = date1.plus(hour.getTime(), ChronoUnit.MILLIS);
            date1 = date1.plus(1, ChronoUnit.HOURS);
            date2 = date2.plus(hour2.getTime(), ChronoUnit.MILLIS);
            date2 = date2.plus(1, ChronoUnit.HOURS);
            if (date1.isBefore(LocalDateTime.now())) {
                Dialog d = new Dialog();
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("czasu nie cofniesz");
                d.show();
                return;
            }
            if (!date1.isBefore(date2)) {
                Dialog d = new Dialog();
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("wizyta nie może się skończyć zanim się zacznie deklu");
                d.show();
                return;
            }

            if(currentNurse!=null){
                officeListView.setItems(FXCollections.observableArrayList(db.getAvailableOfficesAtTimeSortedByNurse(date1, date2, currentNurse.getId())));
            }

        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recepcjonistkaZabieg.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - dodawanie wizyty");
        mainStage = stage;

    }
}
