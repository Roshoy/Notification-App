package lemury.Controller;

import lemury.Model.Ticket.Ticket;
import lemury.Model.Users.Administrator;
import lemury.Model.Users.Coordinator;
import lemury.Model.Users.User;
import lemury.Query.QueryExecutor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
//import oracle.jrockit.jfr.events.EventControl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class LoggingController {
    @FXML
    private TextField login;
    @FXML
    private TextField password;
    @FXML
    private Label informationLabel;
    @FXML
    private Button loginButton;


    @FXML
    private void handleLoggingAction(ActionEvent event) throws SQLException, IOException {
        Optional<User> optionalUser = User.findByLogin(login.getText(), password.getText());

        if(!optionalUser.isPresent()){
            informationLabel.setText("Bledny login/haslo");
            informationLabel.setVisible(true);
        }
        else{
            informationLabel.setText("Zalogowano");
            informationLabel.setVisible(true);
            User user = optionalUser.get();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/UserPane.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(loader.load()));
            UserController userController = loader.<UserController>getController();
            userController.setUser(user);
            userController.setTickets(Ticket.getTicketsList(user));
            userController.setLogin(login.getText());
            stage.show();
        }
    }}