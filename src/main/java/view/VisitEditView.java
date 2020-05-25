package view;

import Model.Disease;
import Model.Medicine;
import Model.Specialization;
import Model.Visit;
import converters.DiseaseConverter;
import converters.MedicineConverter;
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
    private Label zwolnienieLabel;
    @FXML
    private CheckBox zwolnienieCheckBox;
    @FXML
    private DatePicker zwolnienieDatePicker;
    @FXML
    private CheckBox skierowanieCheckBox;
    @FXML
    private ListView skierowanieListView;
    @FXML
    private Label skierowanieLabel;
    @FXML
    private Text skierowanieText;
    @FXML
    private Text typWizytyText;
    @FXML
    private CheckBox receptaCheckBox;
    @FXML
    private TextField medicineTextField;
    @FXML
    private ListView medicineListView;

    String name;
    int doctorId;
    Stage mainStage;
    DatabaseService db;
    Visit visit;
    Map<String, String> diseases;
    Map<String, Integer> medicines;
    boolean hasZwolnienie = false;
    boolean hasSkierowanie = false;
    boolean hasRecepta = false;
    ObservableList<Disease> selectedDiseases = FXCollections.observableArrayList();
    LocalDate zwolnienieDate = null;
    Specialization currentSpecialization = null;
    ObservableList<Medicine> selectedMedicines = FXCollections.observableArrayList();

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
        zwolnienieLabel.setVisible(hasZwolnienie);
        zwolnienieDatePicker.setVisible(hasZwolnienie);
        zwolnienieDatePicker.getEditor().setDisable(true);
        skierowanieLabel.setVisible(hasSkierowanie);
        skierowanieText.setVisible(hasSkierowanie);
        skierowanieListView.setVisible(hasSkierowanie);
        medicineListView.setVisible(hasRecepta);
        medicineTextField.setVisible(hasRecepta);
        notatkaTextArea.setText(visit.getNote());
        if (hasSkierowanie) {
            currentSpecialization = db.getSpecialization(visit.getSpecializationId());
            skierowanieText.setText(currentSpecialization.getPrettyName());
        }
        if (visit.hasZwolnienie()) {
            zwolnienieDatePicker.setValue(visit.getZwolnienieEnd().toLocalDateTime().toLocalDate());
        } else {
            zwolnienieDatePicker.setValue(LocalDate.now().plus(1, ChronoUnit.DAYS));
        }
        if(visit.hasRecepta()){
            selectedMedicines.addAll(visit.getMedicines());
        }
        zwolnienieCheckBox.setSelected(hasZwolnienie);
        skierowanieCheckBox.setSelected(hasSkierowanie);
        receptaCheckBox.setSelected(hasRecepta);
        zwolnienieCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                zwolnienieDatePicker.setVisible(true);
                zwolnienieLabel.setVisible(true);
                hasZwolnienie = true;
            } else {
                zwolnienieDatePicker.setVisible(false);
                zwolnienieLabel.setVisible(false);
                hasZwolnienie = false;
            }
        });
        skierowanieListView.setItems(FXCollections.observableArrayList(db.getAvailableSpecializations()));
        skierowanieListView.setOnMouseClicked(mouseEvent -> {
            if (skierowanieListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            currentSpecialization = (Specialization) skierowanieListView.getSelectionModel().getSelectedItems().get(0);
            skierowanieText.setText(String.valueOf(currentSpecialization));
        });
        skierowanieCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                skierowanieListView.setVisible(true);
                skierowanieText.setVisible(true);
                skierowanieLabel.setVisible(true);
                hasSkierowanie = true;
            } else {
                skierowanieListView.setVisible(false);
                skierowanieText.setVisible(false);
                skierowanieLabel.setVisible(false);
                hasSkierowanie = false;
            }
        });
        receptaCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(t1){
                medicineTextField.setVisible(true);
                medicineListView.setVisible(true);
                hasRecepta = true;
            }else{
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
                if(disease.getIcd10Code()==null){
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
            if(medicineListView.getSelectionModel().getSelectedItems().size()==0){
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
                if(med.getId()==null){
                    return;
                }
                TextInputDialog td = new TextInputDialog();
                td.getEditor().setText("zgodnie z ulotką");
                Optional<String> s = td.showAndWait();
                if(s.isPresent()){
                    System.out.println(s);
                    med.setInstruction(s.get());
                }else{
                    System.out.println(":<");
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
            if (hasSkierowanie && currentSpecialization == null) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("nie ma specjalisty");
                d.show();
                return;
            }
            visit.setNote(notatkaTextArea.getText());
            zwolnienieDate = zwolnienieDatePicker.getValue();
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
                System.out.println("Do: " + zwolnienieDate);
                visit.setZwolnienieStart(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
                visit.setZwolnienieEnd(Timestamp.valueOf(zwolnienieDate.atStartOfDay()));
            }
            System.out.println("Skierowanie: " + hasSkierowanie);
            visit.setHasSkierowanie(hasSkierowanie);
            if (hasSkierowanie) {
                visit.setSpecializationId(currentSpecialization.getId());
                visit.setSkierowanieNote("TODO");
                System.out.println("Do: " + currentSpecialization);
            }
            visit.setHasRecepta(hasRecepta);
            if(hasRecepta && selectedMedicines.size()==0){
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
