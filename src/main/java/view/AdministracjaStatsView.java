package view;

import Model.Medicine;
import Model.Office;
import Model.Person;
import Model.Worker;
import converters.*;
import db.DatabaseService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AdministracjaStatsView extends Application {
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private Button returnButton;
    @FXML
    private ListView doctorsListView;
    @FXML
    private ListView nurseListView;
    @FXML
    private Label visitLabel;
    @FXML
    private Label exertionsLabel;
    @FXML
    private Label prescriptionLabel;
    @FXML
    private Label officeLabel;
    @FXML
    private Label medicineLabel;
    @FXML
    private Label skierowanieLabel;
    @FXML
    private Label zwolnienieLabel;
    @FXML
    private Label longestZwolnienieLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label doctorVisitLabel;
    @FXML
    private Label doctorPrescriptionLabel;
    @FXML
    private Label doctorOfficeLabel;
    @FXML
    private Label doctorMedicineLabel;
    @FXML
    private Label doctorSkierowanieLabel;
    @FXML
    private Label doctorZwolnienieLabel;
    @FXML
    private Label doctorLongestZwolnienieLabel;

    Stage mainStage;
    Integer id;
    String name;
    DatabaseService db;
    LocalDate date1 = null;
    LocalDate date2 = null;
    ObservableList<Person> doctors = FXCollections.observableArrayList();
    ObservableList<Person> nurses = FXCollections.observableArrayList();


    AdministracjaStatsView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
    }

    void countStats() {
        nameLabel.setText("-");
        lastNameLabel.setText("-");
        doctorVisitLabel.setText("-");
        doctorPrescriptionLabel.setText("-");
        doctorOfficeLabel.setText("-");
        doctorMedicineLabel.setText("-");
        doctorSkierowanieLabel.setText("-");
        doctorZwolnienieLabel.setText("-");
        doctorLongestZwolnienieLabel.setText("-");
        doctors.clear();
        doctors.addAll(db.getDoctorsVisitCount(date1, date2));
        nurses.clear();
        nurses.addAll(db.getNursesVisitCount(date1, date2));
        visitLabel.setText(String.valueOf(db.getVisitCount(date1, date2)));
        exertionsLabel.setText(String.valueOf(db.getExertionCount(date1, date2)));
        prescriptionLabel.setText(String.valueOf(db.getPrescriptionCount(date1, date2)));
        Office office = db.getMostUsedOffice(date1, date2);
        if (office == null) {
            officeLabel.setText("-");
        } else {
            officeLabel.setText(new OfficeConverter().toString(office));
        }
        Medicine medicine = db.getMostCommonMedicine(date1, date2);
        if (medicine == null) {
            medicineLabel.setText("-");
        } else {
            medicineLabel.setText(medicine.getName());
        }
        skierowanieLabel.setText(String.valueOf(db.getSkierowanieCount(date1, date2)));
        zwolnienieLabel.setText(String.valueOf(db.getZwolnienieCount(date1, date2)));
        longestZwolnienieLabel.setText(String.valueOf(db.getLongestZwolnienie(date1, date2)) + " dni");
    }

    @FXML
    void initialize() {
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
        fromDatePicker.valueProperty().addListener((observableValue, localDate, t1) -> {
            date1 = fromDatePicker.getValue();
            date2 = toDatePicker.getValue();
            if (date1 == null || date2 == null) {
                return;
            }
            if (date2.isAfter(date1)) {
                countStats();
            }
        });
        toDatePicker.valueProperty().addListener((observableValue, localDate, t1) -> {
            date1 = fromDatePicker.getValue();
            date2 = toDatePicker.getValue();
            if (date1 == null || date2 == null) {
                return;
            }
            if (date2.isAfter(date1)) {
                countStats();
            }
        });
        doctorsListView.setItems(doctors);
        nurseListView.setItems(nurses);
        doctorsListView.setCellFactory(listView -> {
            TextFieldListCell<Person> cell = new TextFieldListCell<>();
            cell.setConverter(new PersonCountConverter());
            return cell;
        });
        doctorsListView.setOnMouseClicked(mouseEvent -> {
            if (doctorsListView.getSelectionModel().getSelectedItems() == null) {
                return;
            }
            if (date1 == null || date2 == null) {
                return;
            }
            if (date1.isAfter(date2)) {
                return;
            }
            Person doctor = (Person) doctorsListView.getSelectionModel().getSelectedItems().get(0);
            nameLabel.setText(doctor.getName());
            lastNameLabel.setText(doctor.getLastName());
            doctorVisitLabel.setText(String.valueOf(doctor.getVisitCount()));
            doctorPrescriptionLabel.setText(String.valueOf(db.getPrescriptionCount(date1, date2, doctor.getId())));
            Office office = db.getMostUsedOffice(date1, date2, doctor.getId());
            if(office!=null){
                doctorOfficeLabel.setText(new OfficeConverter().toString(office));
            }else{
                doctorOfficeLabel.setText("-");
            }
            Medicine medicine = db.getMostCommonMedicine(date1, date2, doctor.getId());
            if(medicine!=null){
                doctorMedicineLabel.setText(medicine.getName());
            }else{
                doctorMedicineLabel.setText("-");
            }
            doctorSkierowanieLabel.setText(String.valueOf(db.getSkierowanieCount(date1, date2, doctor.getId())));
            doctorZwolnienieLabel.setText(String.valueOf(db.getZwolnienieCount(date1, date2, doctor.getId())));
            doctorLongestZwolnienieLabel.setText(db.getLongestZwolnienie(date1, date2, doctor.getId())+" dni");
            System.out.println(doctor.getName() + " " + doctor.getLastName());
        });
        nurseListView.setCellFactory(listView -> {
            TextFieldListCell<Person> cell = new TextFieldListCell<>();
            cell.setConverter(new PersonCountConverter());
            return cell;
        });

        nurseListView.setOnMouseClicked(mouseEvent -> {
            if(nurseListView.getSelectionModel().getSelectedItems().size()==0){
                return;
            }
            Person nurse = (Person) nurseListView.getSelectionModel().getSelectedItems().get(0);
            nameLabel.setText(nurse.getName());
            lastNameLabel.setText(nurse.getLastName());
            doctorVisitLabel.setText(String.valueOf(nurse.getVisitCount()));
            doctorPrescriptionLabel.setText("-");
            doctorOfficeLabel.setText("-");
            doctorMedicineLabel.setText("-");
            doctorSkierowanieLabel.setText("-");
            doctorZwolnienieLabel.setText("-");
            doctorLongestZwolnienieLabel.setText("-");
        });

    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("administracjaStats.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - statystyki");
        mainStage = stage;
    }
}
