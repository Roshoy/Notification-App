package lemury.Controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lemury.Model.Ticket.Ticket;
import lemury.Model.Ticket.TicketStatus;
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
    private Button viewOwnedTicketsButton;

    @FXML
    private TableView<Ticket> ticketsTable;
    @FXML
    protected TableColumn<Ticket, String> titleColumn;
    @FXML
    protected TableColumn<Ticket, String> descriptionColumn;
    @FXML
    protected TableColumn<Ticket, TicketStatus> statusColumn;
    @FXML
    private TableColumn<Ticket, String> userColumn;
    @FXML
    private RadioButton waitingFilter;
    @FXML
    private RadioButton inProgressFilter;
    @FXML
    private RadioButton doneFilter;
    @FXML
    private Button filterButton;


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
        userColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().owner().getFullName()));
        userColumn.setText("Owner");
    }

    @FXML
    private void handleAddITTicket(ActionEvent event) throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AddITTicketPane.fxml"));
        Parent addITTicket = loader.load();

        AddITTicketController controller = loader.getController();
        controller.setUser(user);
        controller.setLogin(login.getText());

        Scene scene = new Scene(addITTicket);
        Stage appStage = new Stage();
        appStage.setScene(scene);
        appStage.show();
    }

    public void setUser(User user) {
        this.user = user;
        this.login.setText(user.getLogin());
        setButtonsVisibility();
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
        setTickets(Ticket.getTicketsList(user));

        initialize();
    }

    @FXML
    public void handleFilterAction(){
        boolean waiting;
        boolean inProgress;
        boolean done;

        if(waitingFilter.isSelected()){
            waiting = true;
        }
        else{
            waiting = false;
        }

        if(inProgressFilter.isSelected()){
            inProgress = true;
        }
        else{
            inProgress = false;
        }

        if(doneFilter.isSelected()){
            done = true;
        }
        else{
            done = false;
        }

        setTickets(Ticket.filterTicketList(user, waiting, inProgress, done));
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

    }

    @FXML
    public void handleViewOwnedTickets(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CoordinatorPane.fxml"));
        Parent coordinate = loader.load();

        CoordinatorController controller = loader.getController();
        controller.setUser(user);

        Scene scene = new Scene(coordinate);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }

    private void setButtonsVisibility(){
        menageUsersButton.managedProperty().bind(menageUsersButton.visibleProperty());
        viewOwnedTicketsButton.managedProperty().bind(viewOwnedTicketsButton.visibleProperty());

        if(user.getUserType().equalsIgnoreCase("C")) {
            menageUsersButton.setVisible(false);
        }
        if(user.getUserType().equalsIgnoreCase("U")){
            viewOwnedTicketsButton.setVisible(false);
            menageUsersButton.setVisible(false);
        }

    }
}
