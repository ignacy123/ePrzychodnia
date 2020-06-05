package view;

import Model.*;
import converters.*;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RecepcjonistkaPacjentView extends Application {
    @FXML
    private ListView pastExertionListView;
    @FXML
    private ListView futureExertionListView;
    @FXML
    private ListView pastVisitListView;
    @FXML
    private ListView futureVisitListView;
    @FXML
    private Button cancelButton;
    @FXML
    private Label patientLabel;
    @FXML
    private ListView receptaListView;
    @FXML
    private Label receptaLabel;
    @FXML
    private Label skierowanieLabel;
    @FXML
    private ListView skierowanieListView;
    @FXML
    private Label zwolnienieLabel;
    @FXML
    private Label zwolnienieFromLabel;
    @FXML
    private Label zwolnienieToLabel;
    @FXML
    private Text zabiegText;
    @FXML
    private Label doctorLabel;
    @FXML
    private Label officeLabel;


    Integer id;
    String name;
    DatabaseService db;
    Stage mainStage;
    String pesel;
    Person patient = null;
    ObservableList<Exertion> pastExertions = FXCollections.observableArrayList();
    ObservableList<Exertion> futureExertions = FXCollections.observableArrayList();
    ObservableList<Visit> pastVisits = FXCollections.observableArrayList();
    ObservableList<Visit> futureVisits = FXCollections.observableArrayList();
    ObservableList<Medicine> currMedicines = FXCollections.observableArrayList();
    ObservableList<Referral> currReferrals = FXCollections.observableArrayList();
    Visit visit = null;
    Exertion exertion = null;


    public RecepcjonistkaPacjentView(Integer id, String name, DatabaseService db, String pesel) {
        this.id = id;
        this.name = name;
        this.db = db;
        this.pesel = pesel;
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
        receptaListView.setVisible(false);
        skierowanieListView.setVisible(false);
        patient = db.getPerson(pesel);
        patientLabel.setText(patient.getName() + " " + patient.getLastName());
        pastExertions.addAll(db.getPastExertions(patient.getId()));
        futureExertions.addAll(db.getFutureExertions(patient.getId()));
        futureVisits.addAll(db.getFutureVisitsPatient(patient.getId()));
        pastVisits.addAll(db.getPastVisits(patient.getId()));
        pastExertionListView.setItems(pastExertions);
        futureExertionListView.setItems(futureExertions);
        pastVisitListView.setItems(pastVisits);
        futureVisitListView.setItems(futureVisits);
        receptaListView.setItems(currMedicines);
        skierowanieListView.setItems(currReferrals);
        pastExertionListView.setCellFactory(listView -> {
            TextFieldListCell<Exertion> cell = new TextFieldListCell<>();
            cell.setConverter(new ExertionConverter());
            return cell;
        });
        futureExertionListView.setCellFactory(listView -> {
            TextFieldListCell<Exertion> cell = new TextFieldListCell<>();
            cell.setConverter(new ExertionConverter());
            return cell;
        });
        pastVisitListView.setCellFactory(listView -> {
            TextFieldListCell<Visit> cell = new TextFieldListCell<>();
            cell.setConverter(new VisitConverter());
            return cell;
        });
        futureVisitListView.setCellFactory(listView -> {
            TextFieldListCell<Visit> cell = new TextFieldListCell<>();
            cell.setConverter(new VisitConverter());
            return cell;
        });
        receptaListView.setCellFactory(listView -> {
            TextFieldListCell<Medicine> cell = new TextFieldListCell<>();
            cell.setConverter(new MedicineConverter());
            return cell;
        });
        skierowanieListView.setCellFactory(listView -> {
            TextFieldListCell<Referral> cell = new TextFieldListCell<>();
            cell.setConverter(new ReferralConverter());
            return cell;
        });
        pastVisitListView.setOnMouseClicked(mouseEvent -> {
            if (pastVisitListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            visit = (Visit) pastVisitListView.getSelectionModel().getSelectedItems().get(0);
            doctorLabel.setText(visit.getDoctor().getName()+" "+visit.getDoctor().getLastName());
            officeLabel.setText(new OfficeConverter().toString(visit.getOffice()));
            zabiegText.setText("-");
            if (visit.hasRecepta()) {
                receptaListView.setVisible(true);
                receptaLabel.setText(String.valueOf(db.getReceptaId(visit.getId())));
                currMedicines.clear();
                currMedicines.addAll(visit.getMedicines());
            } else {
                receptaListView.setVisible(false);
                receptaLabel.setText("-");
            }
            if (visit.hasSkierowanie()) {
                skierowanieListView.setVisible(true);
                skierowanieLabel.setText(db.getSkierowanieIds(visit.getId()));
                currReferrals.clear();
                currReferrals.addAll(visit.getReferrals());
            } else {
                skierowanieLabel.setText("-");
                skierowanieListView.setVisible(false);
            }
            if (visit.hasZwolnienie()) {
                zwolnienieLabel.setText(String.valueOf(db.getZwolnienieId(visit.getId())));
                zwolnienieFromLabel.setText(String.valueOf(visit.getZwolnienieStart().toLocalDateTime().toLocalDate()));
                zwolnienieToLabel.setText(String.valueOf(visit.getZwolnienieEnd().toLocalDateTime().toLocalDate()));
            } else {
                zwolnienieLabel.setText("-");
                zwolnienieFromLabel.setText("-");
                zwolnienieToLabel.setText("-");
            }
        });
        pastExertionListView.setOnMouseClicked(mouseEvent -> {
            if (pastExertionListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            exertion = (Exertion) pastExertionListView.getSelectionModel().getSelectedItems().get(0);
            doctorLabel.setText(exertion.getNurse().getName()+" "+exertion.getNurse().getLastName());
            officeLabel.setText(new OfficeConverter().toString(exertion.getOffice()));
            zabiegText.setText(exertion.getNote());
            receptaListView.setVisible(false);
            receptaLabel.setText("-");
            skierowanieLabel.setText("-");
            skierowanieListView.setVisible(false);
            zwolnienieLabel.setText("-");
            zwolnienieFromLabel.setText("-");
            zwolnienieToLabel.setText("-");
        });
        futureVisitListView.setOnMouseClicked(mouseEvent -> {
            if (futureVisitListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            visit = (Visit) futureVisitListView.getSelectionModel().getSelectedItems().get(0);
            doctorLabel.setText(visit.getDoctor().getName()+" "+visit.getDoctor().getLastName());
            officeLabel.setText(new OfficeConverter().toString(visit.getOffice()));
            receptaListView.setVisible(false);
            receptaLabel.setText("-");
            skierowanieLabel.setText("-");
            skierowanieListView.setVisible(false);
            zwolnienieLabel.setText("-");
            zwolnienieFromLabel.setText("-");
            zwolnienieToLabel.setText("-");
            zabiegText.setText("-");
        });
        futureExertionListView.setOnMouseClicked(mouseEvent -> {
            if (futureExertionListView.getSelectionModel().getSelectedItems().size() == 0) {
                return;
            }
            exertion = (Exertion) futureExertionListView.getSelectionModel().getSelectedItems().get(0);
            doctorLabel.setText(exertion.getNurse().getName()+" "+exertion.getNurse().getLastName());
            officeLabel.setText(new OfficeConverter().toString(exertion.getOffice()));
            zabiegText.setText(exertion.getNote());
            receptaListView.setVisible(false);
            receptaLabel.setText("-");
            skierowanieLabel.setText("-");
            skierowanieListView.setVisible(false);
            zwolnienieLabel.setText("-");
            zwolnienieFromLabel.setText("-");
            zwolnienieToLabel.setText("-");
        });

    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recepcjonistkaPacjent.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - recepcjonistka - info o pacjencie");
        mainStage = stage;
    }
}
