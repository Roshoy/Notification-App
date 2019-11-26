package Controller;

import Model.Ticket.Ticket;
import Model.Users.Coordinator;
import Model.Users.User;
import Query.QueryExecutor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AddITTicketController {
    @FXML
    private TextField title;

    @FXML
    private TextArea description;

    @FXML
    private Label login;

    @FXML
    private Button button;

    @FXML
    private TextField computerNo;

    private int userID;

    @FXML
    private void handleAddTicket(ActionEvent event) throws SQLException, IOException {
        int compNo = Integer.parseInt(computerNo.getText());
        String findCoordinator = String.format("SELECT * FROM USERS WHERE user_type = 'C'",
                          Coordinator.TABLE_NAME, Ticket.TABLE_NAME);
        ResultSet resultSet = QueryExecutor.read(findCoordinator);
        int coordinatorID = QueryExecutor.readIdFromResultSet(resultSet);
        if(coordinatorID < 0){
            System.out.println("Brak dostepnych koordynatorów");
            return;
        }

        String sqlInsert = String.format("INSERT INTO '%s'(coordinator_id, user_id, title, description, status, release_notes) VALUES('%d', '%d', '%s', '%s', '%s', '%s')", Ticket.TABLE_NAME,
                coordinatorID, userID, title.getText(), description.getText(), "WAITING" , null);
        int ticketID  = QueryExecutor.createAndObtainId(sqlInsert);
        String sqlInsertIT = String.format("INSERT INTO ITTICKETS(id, computer_no) VALUES ('%d', '%d')", ticketID, compNo );
        QueryExecutor.createAndObtainId(sqlInsertIT);

    }

    public void setLogin(String login) {
        this.login.setText(login);
    }

    public void setUserID(int id){
        userID = id;
    }
}
