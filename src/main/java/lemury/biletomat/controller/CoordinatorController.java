package lemury.biletomat.controller;

import javafx.scene.control.*;
import lemury.biletomat.model.ticket.Ticket;
import lemury.biletomat.model.ticket.TicketStatus;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


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
    private TableColumn<Ticket, String> dateColumn;
    @FXML
    private Button logoutButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button viewTicketButton;
    @FXML
    private ChoiceBox<String> ticketStatusChoiceBox;
    @FXML
    private Button changeTicketStatusButton;

    private ObservableList<Ticket> tickets;


    @FXML
    private void initialize() {
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        titleColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().title()));
        descriptionColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().description()));
        statusColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().status()));
        userColumn.setCellValueFactory(dataValue ->
                new SimpleObjectProperty<>(dataValue.getValue().submitter().firstName() + " " +
                        dataValue.getValue().submitter().lastName()));
        userColumn.setText("Submitter");
        dateColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dateFormat.format(dataValue.getValue().date())));

        ObservableList<String> ticketStatusNames = TicketStatus.getNames();
        ticketStatusChoiceBox.setItems(ticketStatusNames);
        ticketStatusChoiceBox.setValue(ticketStatusNames.get(0));

        changeTicketStatusButton.setVisible(false);
        ticketStatusChoiceBox.setVisible(false);
    }

    public void setTicketsOwnedByCoordinator() {
        this.tickets = Ticket.getTicketsListOfCoordinator((Coordinator)user);
        ticketsTable.setItems(this.tickets);
    }

    @FXML
    public void handleUpdateAction() {
        this.changeTicketStatusButton.setVisible(false);
        this.ticketStatusChoiceBox.setVisible(false);
        super.handleUpdateAction();
    }
    @FXML
    public void handleRefreshAction(){
        setTickets(Ticket.getTicketsListOfCoordinator((Coordinator) user));
        initialize();

        this.changeTicketStatusButton.setVisible(true);
        this.ticketStatusChoiceBox.setVisible(true);
    }

    @FXML
    public void handleChangeTicketStatusAction() {
        if(ticketsTable.getSelectionModel().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "No ticket selected!").showAndWait();
        } else {
            Ticket referencedTicket = ticketsTable.getSelectionModel().getSelectedItem();
            TicketStatus newTicketStatus = TicketStatus.valueOf(ticketStatusChoiceBox.getValue().toUpperCase());

            if(!referencedTicket.status().equals(newTicketStatus)) {
                referencedTicket.setTicketStatus(newTicketStatus);
            }
        }
    }

    public void setUser(User user){
        this.user = Coordinator.findCoordinatorById(user.id()).get();
    }
}