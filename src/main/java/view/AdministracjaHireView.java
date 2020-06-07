package view;

import Model.Person;
import db.DatabaseService;
import enums.Roles;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.management.relation.Role;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdministracjaHireView extends Application {
    @FXML
    Label peselLabel;
    @FXML
    Button cancelButton;
    @FXML
    Button hireButton;
    @FXML
    TextField nameTextField;
    @FXML
    TextField lastNameTextField;
    @FXML
    DatePicker birthDatePicker;
    @FXML
    TextField phoneTextField;
    @FXML
    TextField emailTextField;
    @FXML
    ChoiceBox etatChoiceBox;

    Integer id;
    String name;
    Stage mainStage;
    DatabaseService db;
    String pesel;
    boolean inDb = false;
    Person person;


    AdministracjaHireView(int id, String name, DatabaseService db, String pesel) {
        this.id = id;
        this.name = name;
        this.db = db;
        this.pesel = pesel;
    }


    @FXML
    void initialize() {
        etatChoiceBox.setItems(FXCollections.observableArrayList(Roles.PIELEGNIARKA_ARZ, Roles.LEKARZ, Roles.ADMINISTRACJA, Roles.RECEPCJONISTKA_TA, Roles.DYREKCJA, Roles.LABORANT_KA, Roles.OBSŁUGA_TECHNICZNA));
        peselLabel.setText(pesel);
        if (db.isInDb(pesel)) {
            inDb = true;
            person = db.getPerson(pesel);
            nameTextField.setDisable(true);
            nameTextField.setText(person.getName());
            lastNameTextField.setDisable(true);
            lastNameTextField.setText(person.getLastName());
            emailTextField.setDisable(true);
            emailTextField.setText(person.getEmail());
            phoneTextField.setDisable(true);
            phoneTextField.setText(person.getPhoneNumber());
            birthDatePicker.setDisable(true);
            birthDatePicker.setValue(person.getDateOfBirth().toLocalDate());
        } else {
            inDb = false;
        }
        cancelButton.setOnAction(actionEvent -> {
            Application view = new AdministracjaView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        hireButton.setOnAction(actionEvent -> {
            if (inDb) {
                Roles role = (Roles) etatChoiceBox.getSelectionModel().getSelectedItem();
                if (role == null) {
                    Dialog d = new Dialog();
                    d.setResizable(true);
                    Window window = d.getDialogPane().getScene().getWindow();
                    window.setOnCloseRequest(e -> window.hide());
                    d.setContentText("Błąd: nie wybrano etatu.");
                    d.show();
                    return;
                }
                db.addRole(person.getId(), role);
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Sukces! Zatrudniono pracownika.");
                d.show();
                Application view = new AdministracjaView(this.id, this.name, db);
                try {
                    view.start(mainStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            String name = String.valueOf(nameTextField.getCharacters());
            if (name.equals("")) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: nie podano imienia.");
                d.show();
                return;
            }
            String lastName = String.valueOf(lastNameTextField.getCharacters());
            if (lastName.equals("")) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: nie podano nazwiska.");
                d.show();
                return;
            }
            LocalDate birthDate = birthDatePicker.getValue();
            if (birthDate == null) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: nie podano daty urodzenia.");
                d.show();
                return;
            }
            if(birthDate.isAfter(LocalDate.now().minus(2, ChronoUnit.DAYS))){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: nieprawidłowa data urodzenia.");
                d.show();
                return;
            }
            String email = String.valueOf(emailTextField.getCharacters());
            String phoneNumber = String.valueOf(phoneTextField.getCharacters());
            Roles role = (Roles) etatChoiceBox.getSelectionModel().getSelectedItem();
            if (role == null) {
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: nie wybrano etatu.");
                d.show();
                return;
            }
            String ptr = "^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
            Pattern pattern = Pattern.compile(ptr);
            Matcher matcher = pattern.matcher(email);
            if(!matcher.matches()){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: nie podano poprawnego adresu email.");
                d.show();
                return;
            }
            String ptr2 = "^([+]?[\\s0-9]+)?(\\d{3}|[(]?[0-9]+[)])?([-]?[\\s]?[0-9])+$";
            Pattern pattern2 = Pattern.compile(ptr2);
            Matcher matcher2 = pattern2.matcher(phoneNumber);
            if(!matcher2.matches()){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: nie podano poprawnego numeru telefonu.");
                d.show();
                return;
            }
            Person toAdd = new Person();
            toAdd.setName(name);
            toAdd.setLastName(lastName);
            toAdd.setPesel(pesel);
            toAdd.setDateOfBirth(Date.valueOf(birthDate));
            toAdd.setEmail(email);
            toAdd.setPhoneNumber(phoneNumber);
            Integer id = db.addPerson(toAdd);
            db.addRole(id, role);
            Dialog d = new Dialog();
            d.setResizable(true);
            Window window = d.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(e -> window.hide());
            d.setContentText("Sukces! Zatrudniono pracownika.");
            d.show();
            Application view = new AdministracjaView(this.id, this.name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("administracjaHire.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - administracja");
        mainStage = stage;

    }
}
