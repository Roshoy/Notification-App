package lemury.biletomat.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AppUtils extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Tickets Application");
        //this.primaryStage.show();
        initRootLayout();
    }

    private void initRootLayout() {
        try {
            // load layout from FXML file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/LoggingPane.fxml"));
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

    public static void appUtilsMain(String[] args) {
        launch(args);
    }
}
