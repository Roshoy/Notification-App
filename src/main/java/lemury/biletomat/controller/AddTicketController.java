package lemury.biletomat.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lemury.biletomat.model.ticket.*;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import lemury.biletomat.utils.DateFormatter;
//import lemury.biletomat.utils.DateFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddTicketController {
    @FXML
    private AnchorPane pane1;
    @FXML
    private Label login;
    @FXML
    private Label ticketName;
    @FXML
    private Button addButton;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;

    int ticketStructureId;
    int departmentId;

    private User user;

    private Label nameFields[] = new Label[10];
    private Label typeFields[] = new Label[10];
    private Label reqFields[] = new Label[10];
    private TextField valueField[] = new TextField[10];

    int counter = 0;


    final int valueFieldX = 45;
    final int valueFieldY = 157;
    final int nameX = 200;
    final int typeX = 388;
    final int requiredX = 481;
    final int labelY = 162;
    final int gap = 30;


    public void setTicketName(String name){

        this.ticketName.setText(name);
    }


    public void setLogin(String login) {
        this.login.setText(login);
    }

    public void setUser(User user){
        this.user = user;
    }


    public void actualiseView() throws SQLException {
        this.ticketStructureId = TicketStructure.getIdFromName(this.ticketName.getText());
        //this.ticketStructureId = TicketStructure.getCount(ticketName.getText());
        this.departmentId = TicketStructure.getDepartmentIdFromId(this.ticketStructureId);
        this.counter = TicketStructure.getCount(ticketStructureId);
        System.out.println("DEPT ID = " + this.departmentId);
        System.out.println("TICKET STRUCTURE ID = " + this.ticketStructureId);
        System.out.println("COUNTER = " + this.counter);

        TicketStructure.setNewTicketPane(nameFields, typeFields, reqFields, valueField, ticketStructureId, counter);
        System.out.println(nameFields[0].getText()+ typeFields[0].getText() + reqFields[0].getText());

        for (int i = 0; i< counter; i++) {
            pane1.getChildren().add(valueField[i]);
            pane1.getChildren().add(nameFields[i]);
            pane1.getChildren().add(reqFields[i]);
            pane1.getChildren().add(typeFields[i]);

            int y = gap * i;
            nameFields[i].setLayoutX(nameX);
            nameFields[i].setLayoutY(labelY + y);

            reqFields[i].setLayoutX(requiredX);
            reqFields[i].setLayoutY(labelY + y);

            valueField[i].setLayoutX(valueFieldX);
            valueField[i].setLayoutY(valueFieldY + y);

            typeFields[i].setLayoutX(typeX);
            typeFields[i].setLayoutY(labelY + y);
        }


    }

    @FXML
    public void initialize() throws SQLException {

        //this.ticketName.setText(this.ticketName.getText());
        //this.ticketStructureId = TicketStructure.getCount(ticketName.getText());
        //System.out.println("TICKET STRUCTURE ID = " + this.ticketStructureId);

        for(int i =0; i <10; i++){
            nameFields[i] = new Label();
            typeFields[i] = new Label();
            reqFields[i] = new Label();
            valueField[i] = new TextField();
        }

    };


    @FXML
    public void handleAddAction(javafx.event.ActionEvent event) throws SQLException {

        ObservableList<Coordinator> depCoordinators = Coordinator.findCoordinatorsByDepartmentId(this.departmentId);
        if(depCoordinators.size() == 0){
            new Alert(Alert.AlertType.ERROR, "Empty department").showAndWait();
            return;
        }

        int minimalTicketsNumber = Integer.MAX_VALUE;
        int coordinatorID = -1;
        for(Coordinator coordinator : depCoordinators) {
            if(coordinator.ownedTickets().size() < minimalTicketsNumber) {
                minimalTicketsNumber = coordinator.ownedTickets().size();
                coordinatorID = coordinator.id();
            }
        }

        if(coordinatorID < 0){
            System.out.println("Brak dostepnych koordynatorów");
            new Alert(Alert.AlertType.ERROR, "No coordinators").showAndWait();
            return;
        }
        if(titleField.getText().isEmpty() || descriptionField.getText().isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Fulfill title field and description field  ").showAndWait();
        }

        DateFormatter dateFormatter = new lemury.biletomat.utils.DateFormatter("dd.MM.yyyy", "M/dd/yyyy");

        for (int i =0; i<counter; i++){
            if(valueField[i].getText().isEmpty() && reqFields[i].getText().equals("1")){
                new Alert(Alert.AlertType.ERROR, "Fulfill required fields  ").showAndWait();
            }
            if(typeFields[i].getText().equals("int") && reqFields[i].getText().equals("1")) {
                try {
                    Integer.parseInt(valueField[i].getText());
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Only numbers are allowed in int fields ").showAndWait();
                }
            }

            if(typeFields[i].getText().equals("date") && reqFields[i].getText().equals("1")){
                try {
                    LocalDate date = dateFormatter.parse(valueField[i].getText());
                } catch (IllegalArgumentException e) {
                    new Alert(Alert.AlertType.ERROR, "Date must be in format dd.MM.yyyy or M/dd/yyyy ").showAndWait();
                }
            }
        }

        int ticketId = Ticket.create(coordinatorID, user.id(), titleField.getText(), descriptionField.getText());
        for(int i = 0; i<counter; i++){
            if(typeFields[i].getText().equals("int")){
                int field_id = TicketStructure.getTicketStructureDetailsIdFromId(ticketStructureId, nameFields[i].getText());
                if(valueField[i].getText().isEmpty()){
                    IntField.create(field_id, ticketId);
                }
                else {
                    IntField.create(field_id, ticketId, Integer.parseInt(valueField[i].getText()));
                }
            }
            else if(typeFields[i].getText().equals("string")){
                int field_id = TicketStructure.getTicketStructureDetailsIdFromId(ticketStructureId, nameFields[i].getText());
                StringField.create(field_id, ticketId, valueField[i].getText());
            }
            else if(typeFields[i].getText().equals("date")){
                int field_id = TicketStructure.getTicketStructureDetailsIdFromId(ticketStructureId, nameFields[i].getText());
                DateField.create(field_id, ticketId, dateFormatter.parse(valueField[i].getText()));
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setContentText("Ticket has been added");

        alert.showAndWait();


    }
}
