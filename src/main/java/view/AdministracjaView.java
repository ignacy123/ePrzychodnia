package view;

import db.DatabaseService;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Optional;

public class AdministracjaView extends Application {
    @FXML
    private Label nameLabel;
    @FXML
    private Button logOutButton;
    @FXML
    private Button infoButton;
    @FXML
    private Button hireButton;
    @FXML
    private Button statsButton;
    @FXML
    private Button medicineButton;

    Integer id;
    String name;
    Stage mainStage;
    DatabaseService db;

    AdministracjaView(int id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
    }

    @FXML
    void initialize() {
        nameLabel.setText(name);
        logOutButton.setOnAction(actionEvent -> {
            Application view = new MainViewController();
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        infoButton.setOnAction(actionEvent -> {
            Application view = new AdministracjaInfoView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        statsButton.setOnAction(actionEvent -> {
            Application view = new AdministracjaStatsView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        medicineButton.setOnAction(actionEvent -> {
            Application view = new AdministracjaMedicineView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        hireButton.setOnAction(actionEvent -> {
            TextInputDialog td = new TextInputDialog();
            td.getEditor().setText("Podaj PESEL");
            td.setResizable(true);
            Optional<String> s = td.showAndWait();
            if (s.isPresent()) {
                if (!isCorrectPESEL(s.get())) {
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("Podany PESEL jest nieprawidłowy.");
                    d.show();
                    return;
                }
                if (db.isNonFiredWorker(s.get())) {
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("Osoba o tym numerze PESEL już tu pracuje.");
                    d.show();
                    return;
                }
                if (db.isFiredWorker(s.get())) {
                    String prettyName = db.getPrettyNameByPesel(s.get());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Czy na pewno chcesz ponownie zatrudnić uprzednio zwolnionego pracownika " + prettyName + "?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        db.rehire(s.get());
                    }
                    return;
                }
                Application view = new AdministracjaHireView(id, name, db, s.get());
                try {
                    view.start(mainStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                return;
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("administracja.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - administracja");
        mainStage = stage;
    }


    boolean isCorrectPESEL(String pesel) {
        if (pesel.length() != 11) {
            return false;
        }
        int[] digits = new int[11];
        for (int i = 0; i < 11; i++) {
            if (!Character.isDigit(pesel.charAt(i))) {
                return false;
            }
            digits[i] = Integer.parseInt(pesel.substring(i, i + 1));
        }
        int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
        int check = 0;
        for (int i = 0; i < 10; i++) {
            check += weights[i] * digits[i];
        }
        int lastNumber = check % 10;
        int controlNumber = (10 - lastNumber)%10;
        if (controlNumber == digits[10]) {
            return true;
        }
        return false;
    }
}