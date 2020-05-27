package view;

import Model.Visit;
import Model.Worker;
import converters.VisitConverter;
import converters.WorkerConverter;
import db.DatabaseService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class AdministracjaInfoView extends Application {
    @FXML
    private ListView workersListView;
    @FXML
    private Button returnButton;
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

    @FXML
    void initialize(){
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
        workersListView.setOnMouseClicked(mouseEvent -> {
            if(workersListView.getSelectionModel().getSelectedItems().size()==0){
                return;
            }
            Worker worker = (Worker) workersListView.getSelectionModel().getSelectedItems().get(0);
            nameLabel.setText(worker.getName());
            lastNameLabel.setText(worker.getName());
            emailLabel.setText(worker.getEmail());
            peselLabel.setText(worker.getPesel());
            birthLabel.setText(String.valueOf(worker.getDateOfBirth()));
            numberLabel.setText(worker.getPhoneNumber());
            etatLabel.setText(String.valueOf(worker.getRole()));
            zatrudnionyLabel.setText(String.valueOf(worker.getHiredFrom()));
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
