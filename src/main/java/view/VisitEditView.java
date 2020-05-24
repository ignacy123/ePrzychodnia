package view;

import Model.Disease;
import Model.Specialization;
import Model.Visit;
import converters.DiseaseConverter;
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
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Map;
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

    String name;
    int doctorId;
    Stage mainStage;
    DatabaseService db;
    Visit visit;
    Map<String, String> diseases;
    boolean hasZwolnienie = false;
    boolean hasSkierowanie = false;
    ObservableList<Disease> selectedDiseases = FXCollections.observableArrayList();
    LocalDate zwolnienieDate = null;
    Specialization currentSpecialization = null;

    VisitEditView(int doctorId, String name, DatabaseService db, Visit visit) {
        this.doctorId = doctorId;
        this.name = name;
        this.visit = visit;
        this.db = db;
        diseases = db.getAllDiseases();
    }


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("visitEdit.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        hasSkierowanie = visit.hasSkierowanie();
        hasZwolnienie = visit.hasZwolnienie();
        zwolnienieLabel.setVisible(hasZwolnienie);
        zwolnienieDatePicker.setVisible(hasZwolnienie);
        skierowanieLabel.setVisible(hasSkierowanie);
        skierowanieText.setVisible(hasSkierowanie);
        skierowanieListView.setVisible(visit.hasSkierowanie());
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
        zwolnienieCheckBox.setSelected(hasZwolnienie);
        skierowanieCheckBox.setSelected(hasSkierowanie);
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
                disease.setPrettyName(name);
                selectedDiseases.add(disease);
                diseaseTextField.clear();
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
            visit.setHasRecepta(false);
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
    }
}
