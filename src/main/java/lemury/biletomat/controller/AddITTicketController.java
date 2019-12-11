package lemury.biletomat.controller;

import lemury.biletomat.model.departments.Department;
import lemury.biletomat.model.ticket.ITTicket;
import lemury.biletomat.model.ticket.Ticket;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
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

    private User user;

    @FXML // zmienione
    private void handleAddTicket(ActionEvent event) throws SQLException, IOException {
        int compNo = Integer.parseInt(computerNo.getText());
        int departmentID = Department.findIdByName(ITTicket.getDepartment());

        if (departmentID < 0){
            System.out.println("Bledny ticket");
            return;
        }

        //String findCoordinator = String.format("SELECT * FROM USERS WHERE user_type = 'C'",
        //                  Coordinator.TABLE_NAME, ticket.TABLE_NAME);
        //ResultSet resultSet = QueryExecutor.read(findCoordinator);

        int coordinatorID = Coordinator.findCoordinatorByDepartmentNo(departmentID);
        if(coordinatorID < 0){
            System.out.println("Brak dostepnych koordynatorów");
            return;
        }

        int ticketID = Ticket.create(coordinatorID, user.id(), title.getText(), description.getText());
        ITTicket.create(ticketID, compNo);


    }

    public void setLogin(String login) {
        this.login.setText(login);
    }

    public void setUser(User user){
        this.user = user;
    }
}