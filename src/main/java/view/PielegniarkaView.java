package view;

import Model.Exertion;
import converters.ExertionConverter;
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
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.LocalDate;

public class PielegniarkaView extends Application {
    @FXML
    private Label nameLabel;
    @FXML
    private Button logOutButton;
    @FXML
    private ListView exertionListView;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private RadioButton takenPlaceButton;
    @FXML
    private RadioButton notTakenPlaceButton;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label patientNameLabel;
    @FXML
    private Label patientLastNameLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Button saveButton;

    Integer id;
    String name;
    DatabaseService db;
    Stage mainStage;
    LocalDate date;
    ObservableList<Exertion> exertionsToShow = FXCollections.observableArrayList();
    Exertion currentExertion = null;

    PielegniarkaView(int id, String name, DatabaseService db) {
        this.db = db;
        this.id = id;
        this.name = name;
    }

    @FXML
    void initialize() {
        datePicker.setValue(LocalDate.now());
        date = LocalDate.now();
        nameLabel.setText(name);
        logOutButton.setOnAction(actionEvent -> {
            Application view = new MainViewController();
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        final ToggleGroup takenPlaceGroup = new ToggleGroup();
        takenPlaceButton.setToggleGroup(takenPlaceGroup);
        notTakenPlaceButton.setToggleGroup(takenPlaceGroup);
        exertionsToShow.addAll(db.getDayExertions(id, date));
        exertionListView.setItems(exertionsToShow);
        exertionListView.setCellFactory(listView -> {
            TextFieldListCell<Exertion> cell = new TextFieldListCell<>();
            cell.setConverter(new ExertionConverter());
            return cell;
        });
        datePicker.setOnAction(actionEvent -> {
            date = datePicker.getValue();
            exertionsToShow.clear();
            exertionsToShow.addAll(db.getDayExertions(id, date));
        });
        exertionListView.setOnMouseClicked(mouseEvent -> {
            if (exertionListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            currentExertion = (Exertion) exertionListView.getSelectionModel().getSelectedItems().get(0);
            if (currentExertion.isTakenPlace()) {
                takenPlaceButton.setSelected(true);
                notTakenPlaceButton.setSelected(false);
            } else {
                takenPlaceButton.setSelected(false);
                notTakenPlaceButton.setSelected(true);
            }
            noteTextArea.setText(currentExertion.getNote());
            patientNameLabel.setText(currentExertion.getPatient().getName());
            patientLastNameLabel.setText(currentExertion.getPatient().getLastName());
            dateLabel.setText(currentExertion.getStart().toString());
        });
        saveButton.setOnAction(actionEvent -> {
            if (currentExertion == null) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("wybierz zabieg");
                d.show();
                return;
            }
            currentExertion.setTakenPlace(takenPlaceButton.isSelected());
            currentExertion.setNote(noteTextArea.getText());
            db.updateExertion(currentExertion);
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("pielegniarka.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        mainStage = stage;
    }
}