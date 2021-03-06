package view;

import Model.*;
import converters.DiseaseConverter;
import converters.MedicineConverter;
import converters.ReferralConverter;
import db.DatabaseService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VisitEditView extends Application implements Initializable {
    @FXML
    private Label nameLabel;
    @FXML
    private Button saveAndExitButton;
    @FXML
    private Button exitButton;
    @FXML
    private RadioButton takenPlaceButton;
    @FXML
    private RadioButton notTakenPlaceButton;
    @FXML
    private TextArea notatkaTextArea;
    @FXML
    private TextField diseaseTextField;
    @FXML
    private ListView diseaseListView;
    @FXML
    private Label zwolnienieFromLabel;
    @FXML
    private Label zwolnienieToLabel;
    @FXML
    private CheckBox zwolnienieCheckBox;
    @FXML
    private DatePicker zwolnienieToDatePicker;
    @FXML
    private DatePicker zwolnienieFromDatePicker;
    @FXML
    private CheckBox skierowanieCheckBox;
    @FXML
    private ListView skierowanieListView;
    @FXML
    private ListView selectedSkierowanie;
    @FXML
    private Text typWizytyText;
    @FXML
    private CheckBox receptaCheckBox;
    @FXML
    private TextField medicineTextField;
    @FXML
    private ListView medicineListView;
    @FXML
    private Text credibilityText;
    @FXML
    private Text dataText;
    @FXML
    private Label patientNameLabel;
    @FXML
    private Label patientLastNameLabel;
    @FXML
    private Label dateOfBirthLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneLabel;

    String name;
    int doctorId;
    Stage mainStage;
    DatabaseService db;
    Visit visit;
    LocalDate zwolnienieFromDate = null;
    LocalDate zwolnienieToDate = null;
    Map<String, String> diseases;
    Map<String, Integer> medicines;
    boolean hasZwolnienie = false;
    boolean hasSkierowanie = false;
    boolean hasRecepta = false;
    ObservableList<Disease> selectedDiseases = FXCollections.observableArrayList();
    ObservableList<Medicine> selectedMedicines = FXCollections.observableArrayList();
    ObservableList<Referral> selectedReferrals = FXCollections.observableArrayList();

    VisitEditView(int doctorId, String name, DatabaseService db, Visit visit) {
        this.doctorId = doctorId;
        this.name = name;
        this.visit = visit;
        this.db = db;
        diseases = db.getAllDiseases();
        medicines = db.getAllMedicines();
    }


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("visitEdit.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - edytowanie wizyty");
        if(visit.getStart().toLocalDateTime().toLocalDate().isBefore(LocalDate.now())){
            saveAndExitButton.setDisable(true);
        }
        hasSkierowanie = visit.hasSkierowanie();
        hasZwolnienie = visit.hasZwolnienie();
        hasRecepta = visit.hasRecepta();
        zwolnienieToLabel.setVisible(hasZwolnienie);
        zwolnienieFromLabel.setVisible(hasZwolnienie);
        zwolnienieToDatePicker.setVisible(hasZwolnienie);
        zwolnienieToDatePicker.getEditor().setDisable(true);
        zwolnienieFromDatePicker.setVisible(hasZwolnienie);
        zwolnienieFromDatePicker.getEditor().setDisable(true);
        skierowanieListView.setVisible(hasSkierowanie);
        selectedSkierowanie.setVisible(hasSkierowanie);
        medicineListView.setVisible(hasRecepta);
        medicineTextField.setVisible(hasRecepta);
        notatkaTextArea.setText(visit.getNote());
        if (hasSkierowanie) {
            selectedReferrals.addAll(visit.getReferrals());
        }
        if (hasZwolnienie) {
            zwolnienieFromDatePicker.setValue(visit.getZwolnienieStart().toLocalDateTime().toLocalDate());
            zwolnienieToDatePicker.setValue(visit.getZwolnienieEnd().toLocalDateTime().toLocalDate());
        } else {
            zwolnienieFromDatePicker.setValue(LocalDate.now());
            zwolnienieToDatePicker.setValue(LocalDate.now().plus(1, ChronoUnit.DAYS));
        }
        if (hasRecepta) {
            selectedMedicines.addAll(visit.getMedicines());
        }
        zwolnienieCheckBox.setSelected(hasZwolnienie);
        skierowanieCheckBox.setSelected(hasSkierowanie);
        receptaCheckBox.setSelected(hasRecepta);
        zwolnienieCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                zwolnienieToDatePicker.setVisible(true);
                zwolnienieFromDatePicker.setVisible(true);
                zwolnienieToLabel.setVisible(true);
                zwolnienieFromLabel.setVisible(true);
                hasZwolnienie = true;
            } else {
                zwolnienieToDatePicker.setVisible(false);
                zwolnienieFromDatePicker.setVisible(false);
                zwolnienieToLabel.setVisible(false);
                zwolnienieFromLabel.setVisible(false);
                hasZwolnienie = false;
            }
        });
        skierowanieListView.setItems(FXCollections.observableArrayList(db.getAvailableSpecializations()));
        skierowanieListView.setOnMouseClicked(mouseEvent -> {
            if (skierowanieListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            Referral referral = new Referral();
            referral.setSpecialization((Specialization) skierowanieListView.getSelectionModel().getSelectedItems().get(0));
            TextInputDialog td = new TextInputDialog();
            td.getEditor().setText("Notatka...");
            td.setResizable(true);
            Optional<String> s = td.showAndWait();
            if (s.isPresent()) {
                referral.setNote(s.get());
            } else {
                return;
            }
            if (selectedReferrals.filtered(referral1 -> {
                if (referral1.getSpecialization().equals(referral.getSpecialization())) {
                    return true;
                }
                return false;
            }).size() > 0) {
                skierowanieListView.getSelectionModel().clearSelection();
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Skierowanie do tego specjalisty już jest wystawione.");
                d.show();
                return;
            }
            selectedReferrals.add(referral);
        });
        selectedSkierowanie.setItems(selectedReferrals);
        selectedSkierowanie.setCellFactory(listView -> {
            TextFieldListCell<Referral> cell = new TextFieldListCell<>();
            cell.setConverter(new ReferralConverter());
            return cell;
        });
        selectedSkierowanie.setOnMouseClicked(mouseEvent -> {
            if (!mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                return;
            }

            if (mouseEvent.getClickCount() != 2) {
                return;
            }
            if (selectedSkierowanie.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            Referral ref = (Referral) selectedSkierowanie.getSelectionModel().getSelectedItems().get(0);
            selectedReferrals.remove(ref);
        });
        skierowanieCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                skierowanieListView.setVisible(true);
                selectedSkierowanie.setVisible(true);
                hasSkierowanie = true;
            } else {
                skierowanieListView.setVisible(false);
                selectedSkierowanie.setVisible(false);
                hasSkierowanie = false;
            }
        });
        receptaCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                medicineTextField.setVisible(true);
                medicineListView.setVisible(true);
                hasRecepta = true;
            } else {
                medicineTextField.setVisible(false);
                medicineListView.setVisible(false);
                hasRecepta = false;
            }
        });

        if (visit.getDiseases() != null) {
            for (String s : visit.getDiseases()) {
                selectedDiseases.add(db.getDisease(s));
            }
        }
        diseaseListView.setItems(selectedDiseases);
        diseaseListView.setCellFactory(listView -> {
            TextFieldListCell<Disease> cell = new TextFieldListCell<>();
            cell.setConverter(new DiseaseConverter());
            return cell;
        });
        diseaseListView.setOnMouseClicked(mouseEvent -> {
            if (!mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                return;
            }

            if (mouseEvent.getClickCount() != 2) {
                return;
            }
            if (diseaseListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            Disease disease = (Disease) diseaseListView.getSelectionModel().getSelectedItems().get(0);
            selectedDiseases.remove(disease);
        });
        diseaseTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                String name = String.valueOf(diseaseTextField.getCharacters());
                Disease disease = new Disease();
                disease.setIcd10Code(diseases.get(name));
                if (disease.getIcd10Code() == null) {
                    return;
                }
                disease.setPrettyName(name);
                if(selectedDiseases.stream().map(disease1 -> disease1.getPrettyName()).collect(Collectors.toSet()).contains(disease.getPrettyName())){
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("Ta choroba już została wybrana.");
                    d.show();
                    return;
                }
                selectedDiseases.add(disease);
                diseaseTextField.clear();
            }
        });
        medicineListView.setItems(selectedMedicines);
        medicineListView.setCellFactory(listView -> {
            TextFieldListCell<Medicine> cell = new TextFieldListCell<>();
            cell.setConverter(new MedicineConverter());
            return cell;
        });
        medicineListView.setOnMouseClicked(mouseEvent -> {
            if (!mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                return;
            }

            if (mouseEvent.getClickCount() != 2) {
                return;
            }
            if (medicineListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            Medicine toDel = (Medicine) medicineListView.getSelectionModel().getSelectedItems().get(0);
            selectedMedicines.remove(toDel);
        });
        medicineTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                Medicine med = new Medicine();
                String name = String.valueOf(medicineTextField.getCharacters());
                med.setName(name);
                med.setId(medicines.get(name));
                if (med.getId() == null) {
                    return;
                }
                TextInputDialog td = new TextInputDialog();
                td.getEditor().setText("Zgodnie z ulotką");
                td.setResizable(true);
                Optional<String> s = td.showAndWait();
                if (s.isPresent()) {
                    med.setInstruction(s.get());
                } else {
                    return;
                }
                if (selectedMedicines.filtered(medicine -> {
                    if (medicine.getName().equals(med.getName())) {
                        return true;
                    }
                    return false;
                }).size() > 0) {
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("Taki lek znajduje się już w recepcie.");
                    d.show();
                    return;
                }
                selectedMedicines.add(med);
                medicineTextField.clear();
            }
        });
        final ToggleGroup takenPlaceGroup = new ToggleGroup();
        if (visit.hasTakenPlace()) {
            takenPlaceButton.setSelected(true);
        } else {
            notTakenPlaceButton.setSelected(true);
        }
        takenPlaceButton.setToggleGroup(takenPlaceGroup);
        notTakenPlaceButton.setToggleGroup(takenPlaceGroup);
        takenPlaceGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            if (t1 == takenPlaceButton) {
                visit.setTakenPlace(true);
            } else {
                visit.setTakenPlace(false);
            }
        });
        mainStage = stage;
        saveAndExitButton.setOnAction(actionEvent -> {
            if (hasSkierowanie && selectedReferrals.size() == 0) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Brak specjalisty");
                d.show();
                return;
            }
            visit.setNote(notatkaTextArea.getText());
            zwolnienieFromDate = zwolnienieFromDatePicker.getValue();
            zwolnienieToDate = zwolnienieToDatePicker.getValue();
            ArrayList<String> diseases = new ArrayList<>();
            for (Disease disease : selectedDiseases) {
                diseases.add(disease.getIcd10Code());
            }
            visit.setDiseases(diseases);
            if(diseases.size()>0 && !visit.hasTakenPlace()){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Nie można stwierdzić chorób na wizycie, która się nie odbyła.");
                d.show();
                return;
            }
            visit.setHasZwolnienie(hasZwolnienie);
            if (hasZwolnienie) {
                if (zwolnienieFromDate.isAfter(zwolnienieToDate)) {
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("Nieprawidłowe zwolnienie.");
                    d.show();
                    return;
                }
                if (zwolnienieFromDate.plus(269, ChronoUnit.DAYS).isBefore(zwolnienieToDate)) {
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("Zwolnienie nie może być dłuższe niż 270 dni.");
                    d.show();
                    return;
                }
                if (db.hasZwolnienie(zwolnienieFromDate, zwolnienieToDate, visit.getPatient().getId(), visit.getId())) {
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("Pacjent ma już zwolnienie w tym terminie.");
                    d.show();
                    return;
                }
                if (visit.getStart().toLocalDateTime().toLocalDate().plus(4, ChronoUnit.DAYS).isBefore(zwolnienieFromDate)) {
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("Zwolnienie zaczyna się za późno.");
                    d.show();
                    return;
                }
                if (zwolnienieFromDate.plus(3, ChronoUnit.DAYS).isBefore(visit.getStart().toLocalDateTime().toLocalDate()) && visit.getSpecialization().getId() != 65
                        && visit.getSpecialization().getId() != 66) {
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("Zwolnienie wystawione przez lekarza niebędącego psychiatrą nie może sięgać wstecz więcej niż 3 dni.");
                    d.show();
                    return;
                }
                visit.setZwolnienieStart(Timestamp.valueOf(zwolnienieFromDate.atStartOfDay()));
                visit.setZwolnienieEnd(Timestamp.valueOf(zwolnienieToDate.atStartOfDay()));
            }
            visit.setHasSkierowanie(hasSkierowanie);
            if (hasSkierowanie) {
                visit.setReferrals(selectedReferrals);
            }
            if (hasSkierowanie && !visit.isTakenPlace()) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Skierowanie nie może być wystawione na wizycie, która się nie odbyła.");
                d.show();
                return;
            }
            if (hasZwolnienie && !visit.isTakenPlace()) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Zwolnienie nie może być wystawione na wizycie, która się nie odbyła.");
                d.show();
                return;
            }
            if (hasRecepta && !visit.isTakenPlace()) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Recepta nie może być wystawiona na wizycie, która się nie odbyła.");
                d.show();
                return;
            }
            visit.setHasRecepta(hasRecepta);
            if (hasRecepta && selectedMedicines.size() == 0) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Recepta nie może być pusta.");
                d.show();
                return;
            }
            visit.setMedicines(selectedMedicines);
            db.updateVisit(visit);
            Application view = new LekarzView(doctorId, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        exitButton.setOnAction(actionEvent -> {
            Application view = new LekarzView(doctorId, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameLabel.setText(name);
        patientNameLabel.setText(visit.getPatient().getName());
        patientLastNameLabel.setText(visit.getPatient().getLastName());
        dateOfBirthLabel.setText(String.valueOf(visit.getPatient().getDateOfBirth()));
        emailLabel.setText(visit.getPatient().getEmail());
        phoneLabel.setText(visit.getPatient().getPhoneNumber());
        typWizytyText.setText(visit.getSpecialization().getPrettyName());
        dataText.setText(String.valueOf(visit.getStart()));
        TextFields.bindAutoCompletion(diseaseTextField, diseases.keySet());
        TextFields.bindAutoCompletion(medicineTextField, medicines.keySet());
    }
}
