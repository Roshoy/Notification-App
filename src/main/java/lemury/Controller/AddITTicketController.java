package lemury.Controller;

import lemury.Model.Departments.Department;
import lemury.Model.Ticket.ITTicket;
import lemury.Model.Ticket.Ticket;
import lemury.Model.Ticket.TicketStatus;
import lemury.Model.Users.Coordinator;
import lemury.Model.Users.User;
import lemury.Query.QueryExecutor;
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


    @FXML // zmienione
    private void handleAddTicket(ActionEvent event) throws SQLException, IOException {
        int compNo = Integer.parseInt(computerNo.getText());
        int departmentID = Department.findIdByName(ITTicket.getDepartment());

        if (departmentID < 0){
            System.out.println("Bledny ticket");
            return;
        }

        //String findCoordinator = String.format("SELECT * FROM USERS WHERE user_type = 'C'",
        //                  Coordinator.TABLE_NAME, Ticket.TABLE_NAME);
        //ResultSet resultSet = QueryExecutor.read(findCoordinator);

        int coordinatorID = Coordinator.findCoordinatorByDepartmentNo(departmentID);
        if(coordinatorID < 0){
            System.out.println("Brak dostepnych koordynatorów");
            return;
        }

        int ticketID = Ticket.create(coordinatorID, userID, title.getText(), description.getText());
        ITTicket.create(ticketID, compNo);


    }

    public void setLogin(String login) {
        this.login.setText(login);
    }

    public void setUserID(int id){
        userID = id;
    }
}
