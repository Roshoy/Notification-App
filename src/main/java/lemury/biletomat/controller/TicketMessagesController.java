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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lemury.biletomat.utils.TicketViewLayoutConstants;

public class TicketMessagesController {
    private User user;
    private Ticket ticket;
    private ObservableList<Message> messages;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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

    private Label[] labelsInt = new Label[Ticket.MAX_FIELDS];
    private Label[] labelsString = new Label[Ticket.MAX_FIELDS];
    private Label[] labelsDate = new Label[Ticket.MAX_FIELDS];
    private Label[] labelsNames = new Label[Ticket.MAX_FIELDS];

    @FXML
    public void initialize() {
        messageHistoryContainer.vvalueProperty().bind(messageHistoryVBox.heightProperty());

        for (int i = 0; i < Ticket.MAX_FIELDS; i++) {
            labelsInt[i] = new Label();
            labelsString[i] = new Label();
            labelsDate[i] = new Label();
            labelsNames[i] = new Label();
        }
    }

    private void addNameLabel(int index, int y, String text) {
        this.pane1.getChildren().add(labelsNames[index]);
        labelsNames[index].setLayoutX(TicketViewLayoutConstants.LABEL_X_NAME);
        labelsNames[index].setLayoutY(TicketViewLayoutConstants.LABEL_Y + y);
        labelsNames[index].setText(text);
    }

    public void actualiseView() {
        List<IntField> intFields = IntField.findFieldsForTicket(ticket.id());
        List<StringField> stringFields = StringField.findFieldsForTicket(ticket.id());
        List<DateField> dateFields = DateField.findFieldsForTicket(ticket.id());

        this.counterInt = intFields.size();
        this.counterString = stringFields.size();
        this.counterDate = dateFields.size();

        int y;
        int i = 0;

        for (IntField intField : intFields) {
            y = TicketViewLayoutConstants.GAP * i;
            pane1.getChildren().add(labelsInt[i]);
            labelsInt[i].setLayoutX(TicketViewLayoutConstants.LABEL_X_INT);
            labelsInt[i].setLayoutY(TicketViewLayoutConstants.LABEL_Y + y);
            labelsInt[i].setText(Integer.toString(intField.value()));
            addNameLabel(i, y, intField.name());

            i++;
        }

        for (StringField stringField : stringFields) {
            y = TicketViewLayoutConstants.GAP * i;
            pane1.getChildren().add(labelsString[i]);
            labelsString[i].setLayoutX(TicketViewLayoutConstants.LABEL_X_STRING);
            labelsString[i].setLayoutY(TicketViewLayoutConstants.LABEL_Y + y);
            labelsString[i].setText(stringField.value());
            addNameLabel(i, y, stringField.name());

            i++;
        }

        for (DateField dateField : dateFields) {
            y = TicketViewLayoutConstants.GAP * i;
            pane1.getChildren().add(labelsDate[i]);
            labelsDate[i].setLayoutX(TicketViewLayoutConstants.LABEL_X_DATE);
            labelsDate[i].setLayoutY(TicketViewLayoutConstants.LABEL_Y + y);
            labelsDate[i].setText(dateTimeFormatter.format(dateField.value()));
            addNameLabel(i, y, dateField.name());

            i++;
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
