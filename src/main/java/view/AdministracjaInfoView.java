package view;

import Model.Disease;
import Model.Specialization;
import Model.Visit;
import Model.Worker;
import converters.VisitConverter;
import converters.WorkerConverter;
import db.DatabaseService;
import enums.Roles;
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
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;


public class AdministracjaInfoView extends Application implements Initializable {
    @FXML
    private ListView workersListView;
    @FXML
    private Button returnButton;
    @FXML
    private Button fireButton;
    @FXML
    private Label nameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label peselLabel;
    @FXML
    private Label birthLabel;
    @FXML
    private Label numberLabel;
    @FXML
    private Label etatLabel;
    @FXML
    private Label zatrudnionyLabel;
    @FXML
    private Label visitCountLabel;
    @FXML
    private Label prescriptionLabel;
    @FXML
    private Label visitCountNameLabel;
    @FXML
    private Label prescriptionNameLabel;
    @FXML
    private Label activeLabel;
    @FXML
    private TextField specializationTextField;
    @FXML
    private ListView specializationListView;

    AdministracjaInfoView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
    }

    Integer id;
    String name;
    Stage mainStage;
    DatabaseService db;
    ObservableList<Worker> workers = FXCollections.observableArrayList();
    Map<String, Integer> specializations;
    Worker currentWorker = null;
    ObservableList<Specialization> specs = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        specializations = db.getAllSpecializations();
        TextFields.bindAutoCompletion(specializationTextField, specializations.keySet());
        visitCountLabel.setVisible(false);
        prescriptionLabel.setVisible(false);
        visitCountNameLabel.setVisible(false);
        prescriptionNameLabel.setVisible(false);
        specializationTextField.setVisible(false);
        specializationListView.setVisible(false);
        workersListView.setItems(workers);
        workers.addAll(db.getAllWorkers());
        workersListView.setCellFactory(listView -> {
            TextFieldListCell<Worker> cell = new TextFieldListCell<>();
            cell.setConverter(new WorkerConverter());
            return cell;
        });
        returnButton.setOnAction(actionEvent -> {
            Application view = new AdministracjaView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        specializationListView.setItems(specs);
        workersListView.setOnMouseClicked(mouseEvent -> {
            if (workersListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            Worker worker = (Worker) workersListView.getSelectionModel().getSelectedItems().get(0);
            currentWorker = worker;
            specs.clear();
            nameLabel.setText(worker.getName());
            lastNameLabel.setText(worker.getName());
            emailLabel.setText(worker.getEmail());
            peselLabel.setText(worker.getPesel());
            birthLabel.setText(String.valueOf(worker.getDateOfBirth()));
            numberLabel.setText(worker.getPhoneNumber());
            etatLabel.setText(String.valueOf(worker.getRole()));
            zatrudnionyLabel.setText(String.valueOf(worker.getHiredFrom()));
            activeLabel.setText(String.valueOf(worker.isActive()));
            if (worker.getRole() == Roles.LEKARZ) {
                visitCountLabel.setVisible(true);
                prescriptionLabel.setVisible(true);
                visitCountNameLabel.setVisible(true);
                prescriptionNameLabel.setVisible(true);
                specializationTextField.setVisible(true);
                specializationListView.setVisible(true);
                specs.addAll(db.getDoctorsSpecialization(worker.getId()));
                visitCountLabel.setText(String.valueOf(db.getTotalVisitCount(worker.getId())));
                prescriptionLabel.setText(String.valueOf(db.getTotalPrescriptionCount(worker.getId())));
            } else if (worker.getRole() == Roles.PIELEGNIARKA_ARZ) {
                visitCountLabel.setVisible(true);
                visitCountNameLabel.setVisible(true);
                visitCountLabel.setText(String.valueOf(db.getTotalExertionCount(worker.getId())));
                prescriptionLabel.setVisible(false);
                prescriptionNameLabel.setVisible(false);
                specializationTextField.setVisible(false);
                specializationListView.setVisible(false);
            } else {
                visitCountLabel.setVisible(false);
                prescriptionLabel.setVisible(false);
                visitCountNameLabel.setVisible(false);
                prescriptionNameLabel.setVisible(false);
                specializationTextField.setVisible(false);
                specializationListView.setVisible(false);
            }
        });
        specializationTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                String name = String.valueOf(specializationTextField.getCharacters());
                System.out.println(name);
                Specialization specialization = new Specialization();
                specialization.setPrettyName(name);
                specialization.setId(specializations.get(name));
                if(specialization.getId()==null){
                    return;
                }
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Czy na pewno chcesz dodać lekarzowi "+currentWorker.getName()+" "+currentWorker.getLastName()+" specjalizację "+specialization.getPrettyName()+"?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    System.out.println(specialization.getPrettyName());
                    if(specs.contains(specialization)){
                        Dialog d = new Dialog();
                        d.setResizable(true);
                        Window window = d.getDialogPane().getScene().getWindow();
                        window.setOnCloseRequest(e -> window.hide());
                        d.setContentText("ten lekarz ma już tą specjalizację");
                        d.show();
                        return;
                    }
                    db.addSpecialization(currentWorker.getId(), specialization.getId());
                }
            }
        });

        fireButton.setOnAction(actionEvent -> {
            if(!currentWorker.isActive()){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("nie można kogoś wylać dwa razy");
                d.show();
                return;
            }
            db.fireWorker(currentWorker.getId());
            currentWorker.setActive(false);
        });

    }
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("administracjaInfo.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - info o pracownikach");
        mainStage = stage;
    }
}
