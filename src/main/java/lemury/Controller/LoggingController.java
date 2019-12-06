package lemury.Controller;

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
        String findByLoginSql = String.format("SELECT * FROM '%s' WHERE login='%s' AND password='%s'", User.TABLE_NAME, login.getText(), password.getText());
        ResultSet resultSet = QueryExecutor.read(findByLoginSql);
        int id = QueryExecutor.readIdFromResultSet(resultSet);
        if(id == -1){
            informationLabel.setText("Bledny login/haslo");
            informationLabel.setVisible(true);



        }

        else if(id != -1) {
            informationLabel.setText("Zalogowano");
            informationLabel.setVisible(true);

            String userType = resultSet.getString("user_type").toLowerCase();

            if(userType.equals("u")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/UserPane.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.setScene(new Scene((Pane) loader.load()));
                UserController userController = loader.<UserController>getController();
                userController.setUserID(id);
                userController.setTickets(User.getTicketsList(id));
                userController.setLogin(login.getText());
                stage.show();
            }
            else if(userType.equals("a")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ManageUsersPane.fxml"));
                Parent manageUsers = loader.load();

                ManageUsersController controller = loader.getController();
                controller.setUsers(User.getUsersList());
                controller.setCoordinators(Coordinator.getCoordinatorsList());

                Scene scene = new Scene(manageUsers);
                Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                appStage.setScene(scene);
                appStage.show();
            }
            else if(userType.equals("c")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CoordinatorPane.fxml"));
                Parent coordinate = loader.load();

                CoordinatorController controller = loader.getController();
                controller.setTickets(Coordinator.getTicketsList(id));
//                controller.setUsers(Administrator.getUsersList());
//                controller.setCoordinators(Administrator.getCoordinatorsList());

                Scene scene = new Scene(coordinate);
                Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                appStage.setScene(scene);
                appStage.show();
            }
        }
    }




}
