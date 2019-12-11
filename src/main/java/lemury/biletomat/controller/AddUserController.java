package lemury.biletomat.controller;

import javafx.scene.control.ChoiceBox;
import lemury.biletomat.model.departments.Department;
import lemury.biletomat.model.users.Administrator;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
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
    private ChoiceBox<String> departmentField;

    @FXML
    private void initialize() {
        departmentField.setItems(Department.getNames());
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
            Optional<User> newUser = User.createUserAccount(loginTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText(), passwordTextField.getText());
            this.users.add(newUser.get());

        }
        else if(coordinatorRadioButton.isSelected()) {
            String woow = departmentField.getSelectionModel().toString();
            System.out.println(woow);
            Optional<Coordinator> newCoordinator = Administrator.createCoordinatorAccount(loginTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText(), passwordTextField.getText(),
                    Department.findIdByName(departmentField.getSelectionModel().getSelectedItem()));
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
