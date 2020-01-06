package lemury.biletomat.controller;

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
import lemury.biletomat.model.ticket.Ticket;
import lemury.biletomat.model.ticket.TicketStatus;
import lemury.biletomat.model.ticket.TicketStructure;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserController {
    @FXML
    protected Label login = new Label();
    /*
    @FXML
    private Button addITTicketButton;
    */
    @FXML
    private Button menageUsersButton;
    @FXML
    private Button viewTicketButton;
    @FXML
    private Button viewOwnedTicketsButton;
    @FXML
    private Button addNewTicketTypeButton;


    @FXML
    private TableView<Ticket> ticketsTable;
    @FXML
    protected TableColumn<Ticket, String> titleColumn;
    @FXML
    protected TableColumn<Ticket, String> descriptionColumn;
    @FXML
    protected TableColumn<Ticket, TicketStatus> statusColumn;
    @FXML
    private TableColumn<Ticket, Coordinator> userColumn;
    @FXML
    private TableColumn<Ticket, Date> dateColumn;
    @FXML
    private RadioButton waitingFilter;
    @FXML
    private RadioButton inProgressFilter;
    @FXML
    private RadioButton doneFilter;
    @FXML
    private Button filterButton;
    @FXML
    private ChoiceBox<String> ticketTypeField;
    @FXML
    private Button addNewTicketButton;
    @FXML
    private Button deleteTicketButton;

    @FXML
    private Button logoutButton;
    protected User user;

    @FXML
    protected Button updateButton;

    private ObservableList<Ticket> tickets;


    @FXML
    protected void initialize() {
        ticketTypeField.setItems(TicketStructure.getNames());
        ticketTypeField.setValue(TicketStructure.getNames().get(0));

        titleColumn.setCellValueFactory(dataValue -> dataValue.getValue().getTitleProperty());
        descriptionColumn.setCellValueFactory(dataValue -> dataValue.getValue().getDescriptionProperty());
        statusColumn.setCellValueFactory(dataValue -> dataValue.getValue().getStatusProperty());
        userColumn.setCellValueFactory(dataValue -> dataValue.getValue().getOwnerProperty());
        userColumn.setText("Owner");

        // za: https://stackoverflow.com/questions/47484280/format-of-date-in-the-javafx-tableview
        dateColumn.setCellFactory(dataValue -> {
            TableCell<Ticket, Date> dateCell = new TableCell<Ticket, Date>() {
                private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    } else {
                        setText(dateFormat.format(item));
                    }
                }
            };

            return dateCell;
        });
        dateColumn.setCellValueFactory(dataValue -> dataValue.getValue().getDateProperty());
    }

    /*
    @FXML
    private void handleAddITTicket(ActionEvent event) throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddITTicketPane.fxml"));
        Parent addITTicket = loader.load();

        AddITTicketController controller = loader.getController();
        controller.setUser(user);
        controller.setLogin(login.getText());

        Scene scene = new Scene(addITTicket);
        Stage appStage = new Stage();
        appStage.setScene(scene);
        appStage.show();
    }
    */

    @FXML
    private void handleAddNewTicketTypeAction(ActionEvent event) throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddNewTicketTypePane.fxml"));
        Parent addTicketType = loader.load();

        AddNewTicketTypeController controller = loader.getController();
        //controller.setUser(user);
        //ontroller.setLogin(login.getText());

        Scene scene = new Scene(addTicketType);
        Stage appStage = new Stage();
        appStage.setScene(scene);
        appStage.show();
    }

    @FXML
    protected void handleAddNewTicketAction(ActionEvent event) throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddTicketPane.fxml"));
        Parent addTicketType = loader.load();

        AddTicketController controller = loader.getController();
        controller.setTicketName(this.ticketTypeField.getValue());

        System.out.println(this.ticketTypeField.getValue());
        controller.setUser(user);
        controller.setLogin(login.getText());

        controller.actualiseView();
        Scene scene = new Scene(addTicketType);
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

    @FXML
    public void handleLogoutAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoggingPane.fxml"));
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
        setTickets(Ticket.getTicketsList(this.user));

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ManageUsersPane.fxml"));
        Parent manageUsers = loader.load();

        ManageUsersController menageUsers = loader.getController();
        menageUsers.setAdministrator(user);

        Scene scene = new Scene(manageUsers);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }

    @FXML
    public void handleViewTicket(ActionEvent event) throws IOException, SQLException {
        if(ticketsTable.getSelectionModel().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "No ticket selected!").showAndWait();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TicketMessagesPane.fxml"));
            Parent ticketMessages = loader.load();

            Ticket referencedTicket = ticketsTable.getSelectionModel().getSelectedItem();
            TicketMessagesController controller = loader.getController();
            controller.setUser(user);
            controller.setTicket(referencedTicket);
            controller.actualiseView();
            Scene scene = new Scene(ticketMessages);
            Stage appStage = new Stage();
            appStage.setScene(scene);
            appStage.show();
        }
    }

    @FXML
    public void handleViewOwnedTickets(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CoordinatorPane.fxml"));
        Parent coordinate = loader.load();

        CoordinatorController controller = loader.getController();
        controller.setUser(user);

        Scene scene = new Scene(coordinate);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }

    @FXML
    public void handleDeleteTicket(ActionEvent event) throws IOException {
        ObservableList<Ticket> toRemove = ticketsTable.getSelectionModel().getSelectedItems();

        for(Ticket ticket : toRemove) {
            Ticket.deleteTicketById(ticket.id());
        }

        this.tickets.removeAll(toRemove);
    }

    private void setButtonsVisibility(){
        menageUsersButton.managedProperty().bind(menageUsersButton.visibleProperty());
        viewOwnedTicketsButton.managedProperty().bind(viewOwnedTicketsButton.visibleProperty());

        if(user.getUserType().equalsIgnoreCase("C")) {
            menageUsersButton.setVisible(false);
            addNewTicketTypeButton.setVisible(false);
        }
        if(user.getUserType().equalsIgnoreCase("U")){
            viewOwnedTicketsButton.setVisible(false);
            menageUsersButton.setVisible(false);
            addNewTicketTypeButton.setVisible(false);
        }
    }
}
