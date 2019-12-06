package lemury.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lemury.Model.Ticket.Ticket;
import lemury.Model.Ticket.TicketStatus;
import lemury.Model.Users.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sun.rmi.runtime.Log;

import java.io.IOException;


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
    private TableColumn<Ticket, String> submiteeColumn;
    @FXML
    private Button logoutButton;


    private ObservableList<Ticket> tickets;


    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().title()));
        descriptionColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().description()));
        statusColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().status()));
        submiteeColumn.setCellValueFactory(dataValue ->
                new SimpleObjectProperty<>(dataValue.getValue().submitee().firstName() + " " +
                        dataValue.getValue().submitee().lastName()));
    }

    public void setTickets(ObservableList<Ticket> tickets) {
        this.tickets = tickets;
        ticketsTable.setItems(tickets);
    }


    @FXML
    public void handleLogoutAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/LoggingPane.fxml"));
        Parent logging = loader.load();
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();

        LoggingController loggingController = loader.getController();
        Scene scene = new Scene(logging);
        Stage appStage = new Stage();
        appStage.setScene(scene);
        appStage.show();
    }
}
