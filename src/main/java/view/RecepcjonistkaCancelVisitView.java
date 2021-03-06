package view;

import Model.Visit;
import converters.OfficeConverter;
import converters.VisitCancelConverter;
import converters.VisitConverter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecepcjonistkaCancelVisitView extends Application {
    @FXML
    private ListView visitListView;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label patientLabel;
    @FXML
    private Label doctorLabel;
    @FXML
    private Label officeLabel;
    @FXML
    private Button backButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField surnameTextField;


    Integer id;
    String name;
    DatabaseService db;
    Stage mainStage;
    Visit visit = null;


    LocalDate date = null;
    ObservableList<Visit> visitsByDay = FXCollections.observableArrayList();
    List<Visit> orgVisitsByDay = new ArrayList<>();

    RecepcjonistkaCancelVisitView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
    }


    @FXML
    void initialize() {
        backButton.setOnAction(actionEvent -> {
            Application view = new RecepcjonistkaView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        datePicker.setValue(LocalDate.now());
        date = LocalDate.now();
        datePicker.valueProperty().addListener((observableValue, localDate, t1) -> {
            if (datePicker.getValue().isBefore(LocalDate.now())) {
                datePicker.setValue(LocalDate.now());
                date = LocalDate.now();
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd.");
                d.show();
                return;
            }
            date = datePicker.getValue();
            orgVisitsByDay = db.getDayVisits(date);
            visitsByDay.clear();
            visitsByDay.addAll(orgVisitsByDay.stream().filter(s1 -> {
                if (s1.getPatient().getLastName().toLowerCase().contains(String.valueOf(surnameTextField.getCharacters()).toLowerCase())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
            visit = null;
            patientLabel.setText("-");
            doctorLabel.setText("-");
            officeLabel.setText("-");
        });
        visitsByDay.addAll(db.getDayVisits(date));
        visitListView.setItems(visitsByDay);
        visitListView.setCellFactory(listView -> {
            TextFieldListCell<Visit> cell = new TextFieldListCell<>();
            cell.setConverter(new VisitCancelConverter());
            return cell;
        });
        visitListView.setOnMouseClicked(mouseEvent -> {
            if (visitListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            visit = (Visit) visitListView.getSelectionModel().getSelectedItems().get(0);
            doctorLabel.setText(visit.getDoctor().getName() + " " + visit.getDoctor().getLastName());
            patientLabel.setText(visit.getPatient().getName() + " " + visit.getPatient().getLastName());
            officeLabel.setText(new OfficeConverter().toString(visit.getOffice()));
        });
        cancelButton.setOnAction(actionEvent -> {
            if (visit == null) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: nie wybrano wizyty.");
                d.show();
                return;
            }
            db.cancelVisit(visit.getId());
            visit = null;
            patientLabel.setText("-");
            doctorLabel.setText("-");
            officeLabel.setText("-");
            Dialog d = new Dialog();
            d.setResizable(true);
            Window window = d.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(e -> window.hide());
            d.setContentText("Wizyta została odwołana.");
            d.show();
            date = datePicker.getValue();
            orgVisitsByDay = db.getDayVisits(date);
            visitsByDay.clear();
            visitsByDay.addAll(orgVisitsByDay.stream().filter(s1 -> {
                if (s1.getPatient().getLastName().toLowerCase().contains(String.valueOf(surnameTextField.getCharacters()).toLowerCase())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
        });
        surnameTextField.textProperty().addListener((observableValue, s, t1) -> {
            visitsByDay.clear();
            visitsByDay.addAll(orgVisitsByDay.stream().filter(s1 -> {
                if (s1.getPatient().getLastName().toLowerCase().contains(String.valueOf(surnameTextField.getCharacters()).toLowerCase())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recepcjonistkaCancelVisit.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - recepcjonistka - odwoływanie wizyty");
        mainStage = stage;
    }
}
