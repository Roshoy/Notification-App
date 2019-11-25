package Model.main;

import java.io.IOException;
import java.sql.SQLException;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sun.plugin.javascript.navig.AnchorArray;


public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("My first JavaFX app");
        //this.primaryStage.show();
        initRootLayout();
    }



    public static void main(String[] args) {
        launch(args);
    }


    private void initRootLayout() {
        try {
            // load layout from FXML file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/LoggingPane.fxml"));
            AnchorPane rootLayout = (AnchorPane) loader.load();

            // add layout to a scene and show them all
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();



        } catch (IOException e) {
            // don't do this in common apps
            e.printStackTrace();
        }
    }


    public void setPrimaryStage(Scene scene){
        this.primaryStage.setScene(scene);
        primaryStage.show();
    }

}

