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
            td.getEditor().setText("visitEditView.java linia koło 150");
            Optional<String> s = td.showAndWait();
            if (s.isPresent()) {
                System.out.println(s);
                referral.setNote(s.get());
            } else {
                System.out.println(":<");
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
                System.out.println(name);
                Disease disease = new Disease();
                disease.setIcd10Code(diseases.get(name));
                if (disease.getIcd10Code() == null) {
                    return;
                }
                disease.setPrettyName(name);
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
                System.out.println(name);
                med.setName(name);
                med.setId(medicines.get(name));
                if (med.getId() == null) {
                    return;
                }
                TextInputDialog td = new TextInputDialog();
                td.getEditor().setText("zgodnie z ulotką");
                td.setResizable(true);
                Optional<String> s = td.showAndWait();
                if (s.isPresent()) {
                    System.out.println(s);
                    med.setInstruction(s.get());
                } else {
                    System.out.println(":<");
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
                    d.setContentText("już jest taki lek");
                    d.show();
                    return;
                }
                selectedMedicines.add(med);
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
                System.out.println("odbyła się");
            } else {
                visit.setTakenPlace(false);
                System.out.println("nie odbyła się");
            }
        });
        mainStage = stage;
        saveAndExitButton.setOnAction(actionEvent -> {
            if (hasSkierowanie && selectedReferrals.size() == 0) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("nie ma specjalisty");
                d.show();
                return;
            }
            visit.setNote(notatkaTextArea.getText());
            zwolnienieFromDate = zwolnienieFromDatePicker.getValue();
            zwolnienieToDate = zwolnienieToDatePicker.getValue();
            System.out.println("Here the data will be saved to db.");
            System.out.println("Odbyła się: " + visit.hasTakenPlace());
            System.out.println("Notatka: " + visit.getNote());
            System.out.println("Choroby: " + selectedDiseases);
            System.out.println("Zwolnienie: " + hasZwolnienie);
            ArrayList<String> diseases = new ArrayList<>();
            for (Disease disease : selectedDiseases) {
                diseases.add(disease.getIcd10Code());
            }
            visit.setDiseases(diseases);
            visit.setHasZwolnienie(hasZwolnienie);
            if (hasZwolnienie) {
                System.out.println("Od: " + zwolnienieFromDate);
                System.out.println("Do: " + zwolnienieToDate);
                if (zwolnienieFromDate.isAfter(zwolnienieToDate)) {
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("zabawne (zwolnienie)");
                    d.show();
                    return;
                }
                visit.setZwolnienieStart(Timestamp.valueOf(zwolnienieFromDate.atStartOfDay()));
                visit.setZwolnienieEnd(Timestamp.valueOf(zwolnienieToDate.atStartOfDay()));
            }
            System.out.println("Skierowanie: " + hasSkierowanie);
            visit.setHasSkierowanie(hasSkierowanie);
            if (hasSkierowanie) {
                visit.setReferrals(selectedReferrals);
                System.out.println("Do: " + selectedReferrals);
            }
            if (hasSkierowanie && !visit.isTakenPlace()) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("wizyta ma sie odbyc jak chcesz skierowanie");
                d.show();
                return;
            }
            if (hasZwolnienie && !visit.isTakenPlace()) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("wizyta ma sie odbyc jak chcesz zwolnienie");
                d.show();
                return;
            }
            if (hasRecepta && !visit.isTakenPlace()) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("wizyta ma sie odbyc jak chcesz zwolnienie");
                d.show();
                return;
            }
            visit.setHasRecepta(hasRecepta);
            if (hasRecepta && selectedMedicines.size() == 0) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("wybierz leki");
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
            System.out.println("The data will not be saved to db.");
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
        typWizytyText.setText(visit.getSpecialization().getPrettyName());
        TextFields.bindAutoCompletion(diseaseTextField, diseases.keySet());
        TextFields.bindAutoCompletion(medicineTextField, medicines.keySet());
    }
}
