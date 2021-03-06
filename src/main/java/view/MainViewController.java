package view;

import db.DatabaseService;
import db.DatabaseServiceImpl;
import enums.Roles;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Map;

public class MainViewController extends Application {
    @FXML
    ChoiceBox roleChoiceBox;
    @FXML
    ListView nameListView;

    DatabaseService db;

    Map<String, Integer> names;

    Stage mainStage;


    @FXML
    void initialize() {
        roleChoiceBox.setItems(FXCollections.observableArrayList(Roles.LEKARZ, Roles.ADMINISTRACJA, Roles.PIELEGNIARKA_ARZ, Roles.RECEPCJONISTKA_TA));
        roleChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Roles role = (Roles) roleChoiceBox.getItems().get((Integer) t1);
                fillListView(role);
            }
        });

        nameListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (nameListView.getSelectionModel().getSelectedItems().size() == 0) {
                    return;
                }
                logInto((String) nameListView.getSelectionModel().getSelectedItems().get(0));
            }
        });
    }

    static String user;
    static String pswd;

    public static void main(String[] args) {
        if(args.length<2){
            System.out.println("not enough args");
            System.exit(0);
        }
        user = args[0];
        pswd = args[1];
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        loader.setController(this);
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ePrzychodnia - logowanie");
        db = new DatabaseServiceImpl();
        db.start(user, pswd);
        mainStage = stage;
    }

    private void fillListView(Roles role) {
        names = db.getNames(role);
        nameListView.setItems(FXCollections.observableArrayList(names.keySet()));
    }

    private void logInto(String name) {
        Roles role = (Roles) roleChoiceBox.getSelectionModel().getSelectedItem();
        Application view = null;
        switch (role) {
            case LEKARZ:
                view = new LekarzView(names.get(name), name, db);
                break;
            case ADMINISTRACJA:
                view = new AdministracjaView(names.get(name), name, db);
                break;
            case PIELEGNIARKA_ARZ:
                view = new PielegniarkaView(names.get(name), name, db);
                break;
            case RECEPCJONISTKA_TA:
                view = new RecepcjonistkaView(names.get(name), name, db);
                break;
        }
        try {
            view.start(mainStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
