package lemury.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lemury.Model.Ticket.Ticket;
import lemury.Model.Ticket.TicketStatus;
import lemury.Model.Users.Coordinator;
import lemury.Model.Users.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.sql.SQLException;


public class CoordinatorController extends UserController {

    @FXML
    private TableView<Ticket> ticketsTable;
    @FXML
    private TableColumn<Ticket, String> titleColumn;
    @FXML
    private TableColumn<Ticket, String> descriptionColumn;
    @FXML
    private TableColumn<Ticket, TicketStatus> statusColumn;
    @FXML
    private TableColumn<Ticket, String> submitterColumn;
    @FXML
    private Button logoutButton;
    @FXML
    private Button refreshButton;


    private ObservableList<Ticket> tickets;


    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().title()));
        descriptionColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().description()));
        statusColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().status()));
        submitterColumn.setCellValueFactory(dataValue ->
                new SimpleObjectProperty<>(dataValue.getValue().submitter().firstName() + " " +
                        dataValue.getValue().submitter().lastName()));
    }

    public void setTickets(ObservableList<Ticket> tickets) {
        this.tickets = Ticket.getTicketsListOfCoordinator((Coordinator)user);
        ticketsTable.setItems(this.tickets);
    }
    @FXML
    public void handleRefreshAction(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CoordinatorPane.fxml"));
        Stage stage = (Stage) refreshButton.getScene().getWindow();
        stage.close();

        setTickets(Coordinator.getTicketsList(userID));
        initialize();
        stage.show();

    }

    public void setUser(User user){
        this.user = Coordinator.findCoordinatorById(user.id()).get();
    }
}