package Controller;

import Model.Users.User;
import Query.QueryExecutor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import oracle.jrockit.jfr.events.EventControl;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoggingController {
    @FXML
    private TextField login;
    @FXML
    private TextField password;
    @FXML
    private Button loginButton;

    @FXML
    private void handleLoggingAction(ActionEvent event) throws SQLException {
        String findByLoginSql = String.format("SELECT * FROM '%s' WHERE login='%s' AND password='%s'", User.TABLE_NAME, login.getText(), password.getText());
        ResultSet resultSet = QueryExecutor.read(findByLoginSql);
        if(resultSet == null){
            System.out.println("Nie ma mnie");
            System.out.println(login.getText());
        }

        if(QueryExecutor.readIdFromResultSet(resultSet) > 0){
            System.out.println("JESTEM");
            System.out.println(login.getText());

        }


    }




}
