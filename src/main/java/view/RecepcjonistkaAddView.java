package view;

import Model.Person;
import db.DatabaseService;
import enums.Roles;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecepcjonistkaAddView extends Application {
    @FXML
    TextField peselTextField;
    @FXML
    Button cancelButton;
    @FXML
    Button addButton;
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


    Integer id;
    String name;
    DatabaseService db;
    Stage mainStage;

    public RecepcjonistkaAddView(Integer id, String name, DatabaseService db) {
        this.id = id;
        this.name = name;
        this.db = db;
    }

    @FXML
    void initialize() {
        cancelButton.setOnAction(actionEvent -> {
            Application view = new RecepcjonistkaView(id, name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        addButton.setOnAction(actionEvent -> {
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
            if(name.length()>=40){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: za długie imię. Maks to 40 znaków");
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
            if(lastName.length()>=40){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: za długie nazwisko. Maks to 40 znaków");
                d.show();
                return;
            }
            String pesel = String.valueOf(peselTextField.getCharacters());
            if(!isCorrectPESEL(pesel)){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: niepoprawny numer PESEL.");
                d.show();
                return;
            }
            if(db.isInDb(pesel)){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: pacjent o tym numerze PESEL jest już zapisany.");
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
            String ptr = "^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
            Pattern pattern = Pattern.compile(ptr);
            Matcher matcher = pattern.matcher(email);
            if(!matcher.matches() && !email.equals("")){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: nieprawidłowy adres email.");
                d.show();
                return;
            }
            if(email.length()>=100){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: za długi email. Maks to 100 znaków");
                d.show();
                return;
            }
            String ptr2 = "^([+]?[\\s0-9]+)?(\\d{3}|[(]?[0-9]+[)])?([-]?[\\s]?[0-9])+$";
            Pattern pattern2 = Pattern.compile(ptr2);
            Matcher matcher2 = pattern2.matcher(phoneNumber);
            if(!matcher2.matches() && !phoneNumber.equals("")){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: nieprawidłowy numer telefonu.");
                d.show();
                return;
            }
            if(phoneNumber.length()>=25){
                Dialog d = new Dialog();
                d.setResizable(true);
                Window window = d.getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(e -> window.hide());
                d.setContentText("Błąd: za długi numer telefonu. Maks to 25 znaków");
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
            Dialog d = new Dialog();
            d.setResizable(true);
            Window window = d.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(e -> window.hide());
            d.setContentText("Sukces. Dodano Pacjenta.");
            d.show();
            Application view = new RecepcjonistkaView(this.id, this.name, db);
            try {
                view.start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recepcjonistkaAdd.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - dodawanie pacjenta");
        mainStage = stage;
    }


    boolean isCorrectPESEL(String pesel){
        if(pesel.length()!=11){
            return false;
        }
        int[] digits = new int[11];
        for(int i=0;i<11;i++){
            if(!Character.isDigit(pesel.charAt(i))){
                System.out.println(pesel.charAt(i));
                return false;
            }
            digits[i] = Integer.parseInt(pesel.substring(i, i + 1));
        }
        int[] weights = {1,3,7,9,1,3,7,9,1,3};
        int check = 0;
        for(int i=0;i<10;i++){
            check += weights[i]*digits[i];
        }
        int lastNumber = check % 10;
        int controlNumber = 10 - lastNumber;
        if (controlNumber == digits[10]) {
            return true;
        }
        return false;
    }
}
