package lemury.Controller;

import lemury.Model.Departments.Department;
import lemury.Model.Users.Administrator;
import lemury.Model.Users.Coordinator;
import lemury.Model.Users.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;


public class AddUserController {
    private ObservableList<User> users;
    private ObservableList<Coordinator> coordinators;

    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField loginTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Button addUserButton;
    @FXML
    private Button cancelButton;
    @FXML
    private RadioButton standardUserRadioButton;
    @FXML
    private RadioButton coordinatorRadioButton;
    @FXML
    private TextField departmentTextField;

    @FXML
    private void initialize() {
        departmentTextField.disableProperty().bind(coordinatorRadioButton.selectedProperty().not());
    }

    public void setUsers(ObservableList<User> users) {
        this.users = users;
    }

    public void setCoordinators(ObservableList<Coordinator> coordinators) {
        this.coordinators = coordinators;
    }

    @FXML
    private void handleAddAction(ActionEvent actionEvent) {
        if(standardUserRadioButton.isSelected()) {
            Optional<User> newUser = Administrator.CreateUserAccount(loginTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText(), passwordTextField.getText());
            this.users.add(newUser.get());

        }
        else if(coordinatorRadioButton.isSelected()) {
            Optional<Coordinator> newCoordinator = Administrator.CreateCoordinatorAccount(loginTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText(), passwordTextField.getText(),
                    Department.findIdByName(departmentTextField.getText()));
            this.coordinators.add(newCoordinator.get());
        }

        Stage stage = (Stage) addUserButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private  void handleCancelAction(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
