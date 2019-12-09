package lemury.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lemury.Model.Ticket.Ticket;
import lemury.Model.Users.User;

public class TicketMessagesController {
    private User user;
    private Ticket ticket;

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
    private TextField messageHistoryTextField;

    @FXML
    private TextField newMessageTextField;
    @FXML
    private Button sendMessageButton;

    @FXML
    private void initialize() {}

    public void setUser(User user) {
        this.user = user;
        this.login.setText(user.getLogin());
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
