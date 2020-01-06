package lemury.biletomat.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import lemury.biletomat.model.ticket.Message;
import lemury.biletomat.model.ticket.Ticket;
import lemury.biletomat.model.users.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class TicketMessagesController {
    private User user;
    private Ticket ticket;
    private ObservableList<Message> messages;

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

    @FXML
    private void initialize() {
        messageHistoryContainer.vvalueProperty().bind(messageHistoryVBox.heightProperty());
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

        for(Message msg: messages) {
            String formattedMsg = String.format("%s\n%s\n%s\n\n", msg.author().getLogin(), dateFormat.format(msg.date()), msg.text());
            Label msgLabel = new Label(formattedMsg);
            msgLabel.setWrapText(true);

            HBox msgHBox = new HBox();

            if(msg.author().id() == this.user.id()) {
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
