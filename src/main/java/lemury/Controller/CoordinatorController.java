package lemury.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

import java.io.IOException;


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
    private TableColumn<Ticket, String> userColumn;
    @FXML
    private Button logoutButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button viewTicketButton;


    private ObservableList<Ticket> tickets;


    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().title()));
        descriptionColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().description()));
        statusColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().status()));
        userColumn.setCellValueFactory(dataValue ->
                new SimpleObjectProperty<>(dataValue.getValue().submitter().firstName() + " " +
                        dataValue.getValue().submitter().lastName()));
        userColumn.setText("Submitter");
    }

    public void setTicketsOwnedByCoordinator() {
        this.tickets = Ticket.getTicketsListOfCoordinator((Coordinator)user);
        ticketsTable.setItems(this.tickets);
    }
    @FXML
    public void handleRefreshAction(){
        setTickets(Ticket.getTicketsListOfCoordinator((Coordinator) user));
        initialize();
    }

    public void setUser(User user){
        this.user = Coordinator.findCoordinatorById(user.id()).get();
    }
}