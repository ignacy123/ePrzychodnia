package view;

import db.DatabaseService;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Optional;


public class RecepcjonistkaView extends Application {
    @FXML
    private Label nameLabel;
    @FXML
    private Button newVisitButton;
    @FXML
    private Button logOutButton;
    @FXML
    private Button addPatientButton;
    @FXML
    private Button editButton;
    @FXML
    private Button cancelVisitButton;
    @FXML
    private Button cancelExertionButton;
    @FXML
    private Button newExertionButton;
    @FXML
    private Button patientButton;

    Integer id;
    String name;
    DatabaseService db;
    Stage mainStage;

    RecepcjonistkaView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
    }

    @FXML
    void initialize() {
        nameLabel.setText(name);
        newVisitButton.setOnAction(actionEvent -> {
            Application view = new RecepcjonistkaWizytaView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        newExertionButton.setOnAction(actionEvent -> {
            Application view = new RecepcjonistkaZabiegView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        addPatientButton.setOnAction(actionEvent -> {
            Application view = new RecepcjonistkaAddView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        editButton.setOnAction(actionEvent -> {
            TextInputDialog td = new TextInputDialog();
            td.getEditor().setText("Podaj PESEL");
            td.setResizable(true);
            Optional<String> s = td.showAndWait();
            if (s.isPresent()) {
                if (db.isInDb(s.get())) {
                    Application view = new RecepcjonistkaEditView(id, name, db, s.get());
                    try {
                        view.start(mainStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("nie ma takiego gościa");
                    d.show();
                    return;
                }
            } else {
                System.out.println(":<");
            }
        });
        logOutButton.setOnAction(actionEvent -> {
            Application view = new MainViewController();
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        cancelVisitButton.setOnAction(actionEvent -> {
            Application view = new RecepcjonistkaCancelVisitView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        cancelExertionButton.setOnAction(actionEvent -> {
            Application view = new RecepcjonistkaCancelExertionView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        patientButton.setOnAction(actionEvent -> {
            TextInputDialog td = new TextInputDialog();
            td.getEditor().setText("Podaj PESEL");
            td.setResizable(true);
            Optional<String> s = td.showAndWait();
            if (s.isPresent()) {
                if (db.isInDb(s.get())) {
                    Application view = new RecepcjonistkaPacjentView(id, name, db, s.get());
                    try {
                        view.start(mainStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("nie ma takiego gościa");
                    d.show();
                    return;
                }
            } else {
                System.out.println(":<");
            }
        });
    }



    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recepcjonistka.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - recepcjonistka");
        mainStage = stage;
    }
}