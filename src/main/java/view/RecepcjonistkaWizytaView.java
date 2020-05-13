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
import javafx.stage.Window;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class RecepcjonistkaWizytaView extends Application {
    @FXML
    private Label nameLabel;
    @FXML
    private ListView specializationListView;
    @FXML
    private Text specializationText;
    @FXML
    private Button findDoctorsButton;
    @FXML
    private TextField startTextField;
    @FXML
    private TextField endTextField;
    @FXML
    private DatePicker visitDatePicker;
    @FXML
    private ListView doctorsListView;
    @FXML
    private TextField surnameTextField;
    @FXML
    private ListView patientsListView;
    @FXML
    private Text surnameText;
    @FXML
    private Text doctorText;
    @FXML
    private Button setVisitButton;
    Specialization currentSpecialization = null;

    Integer id;
    String name;
    DatabaseService db;
    Person currentPatient = null;
    Person currentDoctor = null;
    ObservableList patientsToShow;
    Map<String, Integer> patients;
    LocalDateTime date1;
    LocalDateTime date2;
    RecepcjonistkaWizytaView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
    }

    @FXML
    void initialize() {
        nameLabel.setText(name);
        patients = db.getPatients();
        patientsToShow = FXCollections.observableArrayList();
        patientsToShow.addAll(patients.keySet());
        visitDatePicker.setValue(LocalDate.now().plus(1, ChronoUnit.DAYS));
        specializationListView.setItems(FXCollections.observableArrayList(db.getAvailableSpecializations()));
        patientsListView.setItems(patientsToShow);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recepcjonistkawizyta.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        specializationListView.setOnMouseClicked(mouseEvent -> {
            if (specializationListView.getSelectionModel().getSelectedItems() == null) {
                return;
            }
            currentSpecialization = (Specialization) specializationListView.getSelectionModel().getSelectedItems().get(0);
            specializationText.setText(currentSpecialization.getPrettyName());
        });
        specializationListView.setCellFactory(listView -> {
            TextFieldListCell<Specialization> cell = new TextFieldListCell<>();
            cell.setConverter(new SpecializationConverter());
            return cell;
        });
        doctorsListView.setCellFactory(listView -> {
            TextFieldListCell<Person> cell = new TextFieldListCell<>();
            cell.setConverter(new PersonConverter());
            return cell;
        });

        patientsListView.setOnMouseClicked(mouseEvent -> {
            if (patientsListView.getSelectionModel().getSelectedItems() == null) {
                return;
            }
            currentPatient = db.getPerson(patients.get(patientsListView.getSelectionModel().getSelectedItems().get(0)));
            surnameText.setText(new PersonConverter().toString(currentPatient));
        });
        doctorsListView.setOnMouseClicked(mouseEvent -> {
            if (doctorsListView.getSelectionModel().getSelectedItems() == null) {
                return;
            }
            currentDoctor = (Person) doctorsListView.getSelectionModel().getSelectedItems().get(0);
            doctorText.setText(new PersonConverter().toString(currentDoctor));
            
        });
        findDoctorsButton.setOnAction(actionEvent -> {
            try {
                Date hour = new SimpleDateFormat("HH:mm").parse(String.valueOf(startTextField.getCharacters()));
                Date hour2 = new SimpleDateFormat("HH:mm").parse(String.valueOf(endTextField.getCharacters()));
                date1 = visitDatePicker.getValue().atStartOfDay();
                date2 = visitDatePicker.getValue().atStartOfDay();
                date1 = date1.plus(hour.getTime(), ChronoUnit.MILLIS);
                date1 = date1.plus(1, ChronoUnit.HOURS);
                date2 = date2.plus(hour2.getTime(), ChronoUnit.MILLIS);
                date2 = date2.plus(1, ChronoUnit.HOURS);
                if(date1.isBefore(LocalDateTime.now())){
                    Dialog d = new Dialog();
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("czasu nie cofniesz");
                    d.show();
                    return;
                }
                if(!date1.isBefore(date2)){
                    Dialog d = new Dialog();
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("wizyta nie może się skończyć zanim się zacznie deklu");
                    d.show();
                    return;
                }
                if (currentSpecialization != null) {
                    doctorsListView.setItems(FXCollections.observableArrayList(db.getAvailableSpecialistsAtTime(currentSpecialization.getId(), date1, date2)));
                } else {
                    System.out.println("a specjalizacja?");
                }
            } catch (ParseException e) {
                System.out.println("to nie data debilu");
            }
        });
        setVisitButton.setOnAction(actionEvent -> {
            System.out.println("Pacjent: "+new PersonConverter().toString(currentPatient)+" specjalizacja: "+currentSpecialization.getPrettyName()+" lekarz: "
                    +new PersonConverter().toString(currentDoctor)+" od: "+date1+" do: "+date2);
            System.out.println("Here will be performed visit checks, and then the visit will be inserted into db.");
        });

        surnameTextField.textProperty().addListener((observableValue, s, t1) -> {
            patientsToShow.clear();
            patientsToShow.addAll(patients.keySet().stream().filter(s1 -> {
                if (s1.contains(" " + surnameTextField.getCharacters())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
        });
    }
}
