package lemury.biletomat.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lemury.biletomat.model.ticket.Ticket;
import lemury.biletomat.model.ticket.TicketStatus;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


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
    private TableColumn<Ticket, User> userColumn;
    @FXML
    private TableColumn<Ticket, Date> dateColumn;
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
    @FXML
    protected Button backButton;

    private ObservableList<Ticket> tickets;


    @FXML
    protected void initialize() {
        super.initialize();
        userColumn.setText("Submitter");
        userColumn.setCellValueFactory(dataValue -> dataValue.getValue().getSubmitterProperty());

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

    @FXML
    protected void handleBackAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserPane.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        stage.setScene(new Scene(loader.load()));
        UserController userController = loader.<UserController>getController();
        userController.setUser(user);
        userController.setTickets(Ticket.getTicketsList(user));
        stage.show();
    }

    public void setUser(User user){
        this.user = Coordinator.findCoordinatorById(user.id()).get();
    }
}