package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AddTicketController {
    @FXML
    private Label login = new Label();
    @FXML
    private Button addITTicketButton;

    private int userID;

    public void setLogin(String login) {
        this.login.setText(login);
    }


    @FXML
    private void handleAddITTicket(ActionEvent event) throws SQLException, IOException {
        //Parent addTicket = FXMLLoader.load(getClass().getResource("/View/AddITTicketPane.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AddITTicketPane.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(new Scene((Pane) loader.load()));
        AddITTicketController addITTicketController = loader.<AddITTicketController>getController();
        addITTicketController.setUserID(userID);
        addITTicketController.setLogin(login.getText());
        stage.show();

    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setLoginText(String login) {
        this.login.setText(login);
    }
}
