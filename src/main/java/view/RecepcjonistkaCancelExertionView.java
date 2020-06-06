package view;

import Model.Exertion;
import Model.Visit;
import converters.ExertionCancelConverter;
import converters.OfficeConverter;
import converters.VisitCancelConverter;
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

public class RecepcjonistkaCancelExertionView extends Application {
    @FXML
    private ListView exertionListView;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label patientLabel;
    @FXML
    private Label nurseLabel;
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


    LocalDate date = null;
    ObservableList<Exertion> exertionsByDay = FXCollections.observableArrayList();
    List<Exertion> orgExertionsByDay = new ArrayList<>();
    Exertion exertion = null;

    RecepcjonistkaCancelExertionView(int id, String name, DatabaseService db) {
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
            orgExertionsByDay = db.getDayExertions(date);
            exertionsByDay.clear();
            exertionsByDay.addAll(orgExertionsByDay.stream().filter(exertion -> {
                if (exertion.getPatient().getLastName().toLowerCase().contains(String.valueOf(surnameTextField.getCharacters()).toLowerCase())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
        });
        exertionsByDay.addAll(db.getDayExertions(date));
        exertionListView.setItems(exertionsByDay);
        exertionListView.setCellFactory(listView -> {
            TextFieldListCell<Exertion> cell = new TextFieldListCell<>();
            cell.setConverter(new ExertionCancelConverter());
            return cell;
        });
        exertionListView.setOnMouseClicked(mouseEvent -> {
            if (exertionListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            exertion = (Exertion) exertionListView.getSelectionModel().getSelectedItems().get(0);
            nurseLabel.setText(exertion.getNurse().getName() + " " + exertion.getNurse().getLastName());
            patientLabel.setText(exertion.getPatient().getName() + " " + exertion.getPatient().getLastName());
            officeLabel.setText(new OfficeConverter().toString(exertion.getOffice()));
        });

        cancelButton.setOnAction(actionEvent -> {
            if (exertion == null) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: nie wybrano zabiegu.");
                d.show();
                return;
            }
            db.cancelExertion(exertion.getId());
            Dialog d = new Dialog();
            d.setResizable(true);
            Window window = d.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(e -> window.hide());
            d.setContentText("Zabieg został odwołany.");
            d.show();
            date = datePicker.getValue();
            orgExertionsByDay = db.getDayExertions(date);
            exertionsByDay.clear();
            exertionsByDay.addAll(orgExertionsByDay.stream().filter(exertion -> {
                if (exertion.getPatient().getLastName().toLowerCase().contains(String.valueOf(surnameTextField.getCharacters()).toLowerCase())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
        });

        surnameTextField.textProperty().addListener((observableValue, s, t1) -> {
            exertionsByDay.clear();
            exertionsByDay.addAll(orgExertionsByDay.stream().filter(s1 -> {
                if (s1.getPatient().getLastName().toLowerCase().contains(String.valueOf(surnameTextField.getCharacters()).toLowerCase())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toSet()));
        });

    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recepcjonistkaCancelExertion.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - recepcjonistka - odwoływanie zabiegu");
        mainStage = stage;

    }
}
