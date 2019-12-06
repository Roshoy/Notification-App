package lemury.Controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lemury.Model.Ticket.Ticket;
import lemury.Model.Ticket.TicketStatus;

import java.io.IOException;
import java.sql.SQLException;

public class UserController {
    @FXML
    private Label login = new Label();
    @FXML
    private Button addITTicketButton;
    @FXML
    private TableView<Ticket> ticketsTable;
    @FXML
    private TableColumn<Ticket, String> titleColumn;
    @FXML
    private TableColumn<Ticket, String> descriptionColumn;
    @FXML
    private TableColumn<Ticket, TicketStatus> statusColumn;

    @FXML
    private Button logoutButton;
    private int userID;

    private ObservableList<Ticket> tickets;

    public void setLogin(String login) {
        this.login.setText(login);
    }

    @FXML
    private void initialize() {
        this.titleColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().title()));
        descriptionColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().description()));
        statusColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().status()));
    }

    @FXML
    private void handleAddITTicket(ActionEvent event) throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AddITTicketPane.fxml"));
        Parent addITTicket = loader.load();

        AddITTicketController controller = loader.getController();
        controller.setUserID(userID);
        controller.setLogin(login.getText());

        Scene scene = new Scene(addITTicket);
        Stage appStage = new Stage();
        appStage.setScene(scene);
        appStage.show();
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setTickets(ObservableList<Ticket> tickets) {
        this.tickets = tickets;
        ticketsTable.setItems(tickets);
    }

    public void setLoginText(String login) {
        this.login.setText(login);
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
