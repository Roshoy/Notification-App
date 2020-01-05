package lemury.biletomat.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lemury.biletomat.model.users.User;

import java.awt.event.ActionEvent;

public class AddTicketController {

    @FXML
    private Label login;

    @FXML
    private Button addButton;
    private User user;







    public void setLogin(String login) {
        this.login.setText(login);
    }

    public void setUser(User user){
        this.user = user;
    }

    @FXML
    public void handleAddAction(javafx.event.ActionEvent event) {
    }
}
