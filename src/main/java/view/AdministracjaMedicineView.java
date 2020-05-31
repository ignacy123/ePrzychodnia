package view;

import Model.Medicine;
import db.DatabaseService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Map;
import java.util.stream.Collectors;

public class AdministracjaMedicineView extends Application {
    @FXML
    private TextField nameTextField;
    @FXML
    private ListView medicineListView;
    @FXML
    private TextField medicineTextField;
    @FXML
    private Button editButton;
    @FXML
    private Button returnButton;
    @FXML
    private TextField newTextField;
    @FXML
    private Button addButton;
    @FXML
    private CheckBox newCheckBox;

    Integer id;
    String name;
    Stage mainStage;
    DatabaseService db;
    Map<String, Integer> medicines;
    ObservableList toShow = FXCollections.observableArrayList();
    Medicine currMedicine = null;

    AdministracjaMedicineView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
    }

    @FXML
    void initialize() {
        newTextField.setVisible(false);
        addButton.setVisible(false);
        newCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                newTextField.setVisible(true);
                addButton.setVisible(true);
            } else {
                newTextField.setVisible(false);
                addButton.setVisible(false);
            }
        });
        addButton.setOnAction(actionEvent -> {
            String name = String.valueOf(newTextField.getCharacters());
            if (name == null) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("podaj nazwe dla mefedronu");
                d.show();
                return;
            } else {
                if(db.hasMedicine(name)){
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("już jest taki lek");
                    d.show();
                    return;
                }
                db.newMedicine(name);
                medicines = db.getAllMedicines();
                toShow.clear();
                toShow.addAll(medicines.keySet().stream().filter(s1 -> {
                    if (s1.toLowerCase().contains(String.valueOf(medicineTextField.getCharacters()).toLowerCase())) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toSet()));
            }
        });
        returnButton.setOnAction(actionEvent -> {
            Application view = new AdministracjaView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        medicines = db.getAllMedicines();
        toShow.addAll(medicines.keySet());
        medicineListView.setItems(toShow);
        medicineTextField.textProperty().addListener((observableValue, s, t1) -> {
            toShow.clear();
            toShow.addAll(medicines.keySet().stream().filter(s1 -> {
                if (s1.toLowerCase().contains(String.valueOf(medicineTextField.getCharacters()).toLowerCase())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
        });
        medicineListView.setOnMouseClicked(mouseEvent -> {
            if (medicineListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            String name = (String) medicineListView.getSelectionModel().getSelectedItems().get(0);
            currMedicine = new Medicine();
            currMedicine.setName(name);
            currMedicine.setId(medicines.get(name));
            nameTextField.setText(name);
        });
        editButton.setOnAction(actionEvent -> {
            if (currMedicine == null) {
                return;
            }
            String name = String.valueOf(nameTextField.getCharacters());
            currMedicine.setName(name);
            db.updateMedicine(currMedicine);
            medicines = db.getAllMedicines();
            toShow.clear();
            toShow.addAll(medicines.keySet().stream().filter(s1 -> {
                if (s1.toLowerCase().contains(String.valueOf(medicineTextField.getCharacters()).toLowerCase())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("administracjaMedicine.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - edytowanie leków");
        mainStage = stage;

    }
}
