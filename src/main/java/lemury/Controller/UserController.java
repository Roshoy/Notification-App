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
import lemury.Model.Users.Administrator;
import lemury.Model.Users.Coordinator;
import lemury.Model.Users.User;

import java.io.IOException;
import java.sql.SQLException;

public class UserController {
    @FXML
    protected Label login = new Label();
    @FXML
    private Button addITTicketButton;
    @FXML
    private Button menageUsersButton;
    @FXML
    private Button viewTicketButton;
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
    protected User user;

    @FXML
    protected Button updateButton;

    private ObservableList<Ticket> tickets;

    public void setLogin(String login) {
        this.login.setText(login);
    }

    @FXML
    private void initialize() {
        this.titleColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().title()));
        descriptionColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().description()));
        statusColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().status()));
        submiteeColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().submitee().getFullName()));
    }

    @FXML
    private void handleAddITTicket(ActionEvent event) throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AddITTicketPane.fxml"));
        Parent addITTicket = loader.load();

        AddITTicketController controller = loader.getController();
        controller.setUserID(user.id());
        controller.setLogin(login.getText());

        Scene scene = new Scene(addITTicket);
        Stage appStage = new Stage();
        appStage.setScene(scene);
        appStage.show();
    }

    public void setUser(User user) {
        this.user = user;
        this.login.setText(user.getLogin());
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
    @FXML
    public void handleUpdateAction() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/UserPane.fxml"));
        Stage stage = (Stage) updateButton.getScene().getWindow();
        stage.close();

        setTickets(User.getTicketsList(userID));
        initialize();
        stage.show();
    }

    @FXML
    public void handleManageUsers(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ManageUsersPane.fxml"));
        Parent manageUsers = loader.load();

        ManageUsersController menageUsers = loader.getController();
        menageUsers.setAdministrator(user);

        Scene scene = new Scene(manageUsers);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }

    @FXML
    public void handleViewTicket(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CoordinatorPane.fxml"));
        Parent coordinate = loader.load();

        CoordinatorController controller = loader.getController();
        controller.setUser(user);

        Scene scene = new Scene(coordinate);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }
}
