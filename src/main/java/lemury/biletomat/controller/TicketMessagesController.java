package lemury.biletomat.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import lemury.biletomat.model.ticket.*;
import lemury.biletomat.model.users.User;
import lemury.biletomat.query.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class TicketMessagesController {
    private User user;
    private Ticket ticket;
    private ObservableList<Message> messages;

    @FXML
    private AnchorPane pane1;
    @FXML
    private Label login;

    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label ownerLabel;
    @FXML
    private Label statusLabel;

    @FXML
    private TextField newMessageTextField;

    @FXML
    private VBox messageHistoryVBox;
    @FXML
    private ScrollPane messageHistoryContainer;
    @FXML
    private Button sendMessageButton;
    @FXML
    private Button refreshButton;

    private int counterInt = 0;
    private int counterString = 0;
    private int counterDate = 0;

    private final int labelXInt = 320;
    private final int labelXString = 350;
    private final int labelXDate = 380;

    private final int labelY = 15;
    private final int gap = 20;

    private Label labelsInt[] = new Label[10];
    private Label labelsString[] = new Label[10];
    private Label labelsDate[] = new Label[10];

    @FXML
    public void initialize() {
        messageHistoryContainer.vvalueProperty().bind(messageHistoryVBox.heightProperty());

        for (int i = 0; i < 10; i++) {
            labelsInt[i] = new Label();
            labelsString[i] = new Label();
            labelsDate[i] = new Label();
        }
    }

    public void actualiseView() throws SQLException {
        int counter1 = IntField.countFields(ticket.id());
        int counter2 = StringField.countFields(ticket.id());
        int counter3 = DateField.countFields(ticket.id());
        this.counterInt = counter1;
        this.counterString = counter2;
        this.counterDate = counter3;


        int y;
        for (int i = 0; i < this.counterInt; i++) {
            y = gap * i;
            pane1.getChildren().add(labelsInt[i]);
            labelsInt[i].setLayoutX(labelXInt);
            labelsInt[i].setLayoutY(labelY + y);

//////Why is this here?????
            String query1 = String.format("SELECT value from TICKET_DETAILS_INT WHERE ticket_id = %d ", ticket.id());
            ResultSet rs1 = QueryExecutor.read(query1);

            while (rs1.next()) {
                System.out.println("RS1  ");
                System.out.println(rs1.getString(1));
                System.out.println(i);
                labelsInt[i].setText(rs1.getString(1));
            }
        }
        for (int i = 0; i < this.counterString; i++) {
            y = gap * i;
            pane1.getChildren().add(labelsString[i]);
            labelsString[i].setLayoutX(labelXString);
            labelsString[i].setLayoutY(labelY + y);

            String query2 = String.format("SELECT value from TICKET_DETAILS_STRING WHERE ticket_id = %d ", ticket.id());
            ResultSet rs2 = QueryExecutor.read(query2);

            while (rs2.next()) {
                System.out.println("RS2  ");
                System.out.println(rs2.getString(1));
                System.out.println(i);
                labelsString[i].setText(rs2.getString(1));
            }
        }

        for (int i = 0; i < this.counterDate; i++) {
            y = gap * i;
            pane1.getChildren().add(labelsDate[i]);
            labelsDate[i].setLayoutX(labelXDate);
            labelsDate[i].setLayoutY(labelY + y);

            String query3 = String.format("SELECT value from TICKET_DETAILS_DATE WHERE ticket_id = %d ", ticket.id());
            ResultSet rs3 = QueryExecutor.read(query3);

            while (rs3.next()) {
                System.out.println(rs3.getString(1));
                System.out.println(i);
                labelsDate[i].setText(rs3.getString(1));
            }
        }
    }


    public void setUser(User user) {
        this.user = user;
        this.login.setText(user.getLogin());
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        this.titleLabel.setText(ticket.title());
        this.descriptionLabel.setText(ticket.description());
        this.ownerLabel.setText(ticket.owner().getLogin());
        this.statusLabel.setText(ticket.status().toString());

        this.messages = Message.getMessagesForTicket(ticket);

        messageHistoryContainer.setContent(messageHistoryVBox);
        readMessages();
    }

    private void readMessages() {
        messageHistoryVBox.getChildren().clear();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Message msg : messages) {
            String formattedMsg = String.format("%s\n%s\n%s\n\n", msg.author().getLogin(), dateFormat.format(msg.date()), msg.text());
            Label msgLabel = new Label(formattedMsg);
            msgLabel.setWrapText(true);

            HBox msgHBox = new HBox();

            if (msg.author().id() == this.user.id()) {
                msgLabel.setTextAlignment(TextAlignment.RIGHT);
                msgHBox.setAlignment(Pos.BASELINE_RIGHT);
            } else {
                msgLabel.setTextAlignment(TextAlignment.LEFT);
                msgHBox.setAlignment(Pos.BASELINE_LEFT);
            }

            msgHBox.getChildren().add(msgLabel);
            messageHistoryVBox.getChildren().add(msgHBox);
        }
    }

    @FXML
    public void handleRefreshAction() {
        this.messages = Message.getMessagesForTicket(this.ticket);
        readMessages();
    }

    @FXML
    public void handleSendMessageAction() {
        if (this.newMessageTextField.getText().trim().length() > 0) {
            Optional<Message> newMessage = Message.create(this.ticket.id(), this.user.id(), this.newMessageTextField.getText());
            if (newMessage.isPresent()) {
                this.messages.add(newMessage.get());
                readMessages();
            }
        }
    }
}
