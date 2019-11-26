package Controller;

import Model.Ticket.Ticket;
import Model.Ticket.TicketStatus;
import Model.Users.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


public class CoordinatorController {

    @FXML
    private TableView<Ticket> ticketsTable;
    @FXML
    private TableColumn<Ticket, String> titleColumn;
    @FXML
    private TableColumn<Ticket, String> descriptionColumn;
    @FXML
    private TableColumn<Ticket, TicketStatus> statusColumn;
    @FXML
    private TableColumn<Ticket, User> submiteeColumn;

    private ObservableList<Ticket> tickets;


    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().title()));
        descriptionColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().description()));
        statusColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().status()));
        submiteeColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().submitee()));
    }

    public void setTickets(ObservableList<Ticket> tickets) {
        this.tickets = tickets;
        ticketsTable.setItems(tickets);
    }
}
