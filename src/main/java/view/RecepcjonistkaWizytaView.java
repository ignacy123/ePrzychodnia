package view;

import Model.Office;
import Model.Person;
import Model.Specialization;
import Model.Visit;
import converters.OfficeConverter;
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

import java.sql.Timestamp;
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
    private Button findOfficeButton;
    @FXML
    private TextField startTextField;
    @FXML
    private TextField endTextField;
    @FXML
    private DatePicker visitDatePicker;
    @FXML
    private ListView doctorsListView;
    @FXML
    private ListView officeListView;
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
    @FXML
    private Button cancelButton;
    @FXML
    private Text officeText;
    Specialization currentSpecialization = null;

    Integer id;
    String name;
    DatabaseService db;
    Person currentPatient = null;
    Person currentDoctor = null;
    Office currentOffice = null;
    ObservableList patientsToShow;
    Map<String, Integer> patients;
    LocalDateTime date1;
    LocalDateTime date2;
    Stage mainStage;

    RecepcjonistkaWizytaView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
    }

    @FXML
    void initialize() {
        nameLabel.setText(name);
        specializationListView.setOnMouseClicked(mouseEvent -> {
            if (specializationListView.getSelectionModel().getSelectedItems() == null) {
                return;
            }
            currentSpecialization = (Specialization) specializationListView.getSelectionModel().getSelectedItems().get(0);
            specializationText.setText(currentSpecialization.getPrettyName());
            currentDoctor = null;
            doctorText.setText("");
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
        officeListView.setCellFactory(listView -> {
            TextFieldListCell<Office> cell = new TextFieldListCell<>();
            cell.setConverter(new OfficeConverter());
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
        officeListView.setOnMouseClicked(mouseEvent -> {
            if (officeListView.getSelectionModel().getSelectedItems() == null) {
                return;
            }
            currentOffice = (Office) officeListView.getSelectionModel().getSelectedItems().get(0);
            officeText.setText(new OfficeConverter().toString(currentOffice));
        });
        findDoctorsButton.setOnAction(actionEvent -> {
            try {
                if (currentPatient == null) {
                    Dialog d = new Dialog();
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("dodaj pacjenta to się posortuje hehe");
                    d.show();
                    return;
                }
                Date hour = new SimpleDateFormat("HH:mm").parse(String.valueOf(startTextField.getCharacters()));
                Date hour2 = new SimpleDateFormat("HH:mm").parse(String.valueOf(endTextField.getCharacters()));
                date1 = visitDatePicker.getValue().atStartOfDay();
                date2 = visitDatePicker.getValue().atStartOfDay();
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
                if (currentSpecialization != null) {
                    //officeListView.setItems(FXCollections.observableArrayList(db.getAvailableOfficesAtTime(date1, date2)));
                    doctorsListView.setItems(FXCollections.observableArrayList(db.getAvailableSpecialistsAtTimeSortedByPatient(currentPatient.getId(), currentSpecialization.getId(), date1, date2)));
                } else {
                    System.out.println("a specjalizacja?");
                }
            } catch (ParseException e) {
                System.out.println("to nie data debilu");
            }
        });
        findOfficeButton.setOnAction(actionEvent -> {
            try {
                Date hour = new SimpleDateFormat("HH:mm").parse(String.valueOf(startTextField.getCharacters()));
                Date hour2 = new SimpleDateFormat("HH:mm").parse(String.valueOf(endTextField.getCharacters()));
                date1 = visitDatePicker.getValue().atStartOfDay();
                date2 = visitDatePicker.getValue().atStartOfDay();
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
                if (currentDoctor != null) {
                    officeListView.setItems(FXCollections.observableArrayList(db.getAvailableOfficesAtTimeSortedByDoctor(currentDoctor.getId(), date1, date2)));
                } else {
                    officeListView.setItems(FXCollections.observableArrayList(db.getAvailableOfficesAtTime(date1, date2)));
                }
            } catch (ParseException e) {
                System.out.println("to nie data debilu");
            }

        });
        setVisitButton.setOnAction(actionEvent -> {
            if (currentPatient == null) {
                Dialog d = new Dialog();
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("jakiś pacjent by się przydał");
                d.show();
                return;
            }
            if(!db.isPatientFree(date1, date2, currentPatient.getId())){
                Dialog d = new Dialog();
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("pacjent jest zajety");
                d.show();
                return;
            }
            if (currentDoctor == null) {
                Dialog d = new Dialog();
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("wizyta bez lekarza jest mało przydatna");
                d.show();
                return;
            }
            if (currentOffice == null) {
                Dialog d = new Dialog();
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("pod mostem już nie ma miejsca");
                d.show();
                return;
            }
            System.out.println("Pacjent: " + new PersonConverter().toString(currentPatient) + " specjalizacja: " + currentSpecialization.getPrettyName() + " lekarz: "
                    + new PersonConverter().toString(currentDoctor) + " od: " + date1 + " do: " + date2 + " gabinet: " + new OfficeConverter().toString(currentOffice));
            System.out.println("Here will be performed visit checks, and then the visit will be inserted into db.");
            Visit visit = new Visit();
            visit.setPatient(currentPatient);
            visit.setDoctor(currentDoctor);
            visit.setSpecialization(currentSpecialization);
            visit.setOffice(currentOffice);
            visit.setStart(Timestamp.valueOf(date1));
            visit.setEnd(Timestamp.valueOf(date2));
            db.newVisit(visit);
            Dialog d = new Dialog();
            d.setResizable(true);
            Window window = d.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(e -> window.hide());
            d.setContentText("brawo, udało ci się umówić: "+visit.getPatient().getName()+" "+visit.getPatient().getLastName());
            d.show();
            Application view = new RecepcjonistkaView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        cancelButton.setOnAction(actionEvent -> {
            Application view = new RecepcjonistkaView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        surnameTextField.textProperty().addListener((observableValue, s, t1) -> {
            patientsToShow.clear();
            patientsToShow.addAll(patients.keySet().stream().filter(s1 -> {
                if (s1.toLowerCase().contains(" " + String.valueOf(surnameTextField.getCharacters()).toLowerCase())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
        });
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
        stage.setTitle("ePrzychodnia - dodawanie wizyty");
        mainStage = stage;
    }
}
