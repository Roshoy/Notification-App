package Model.main;

import java.io.IOException;
import java.sql.SQLException;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;








public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("My first JavaFX app");
        this.primaryStage.show();
        //initRootLayout();
    }

    public static void main(String[] args) {
        launch(args);
    }



}

