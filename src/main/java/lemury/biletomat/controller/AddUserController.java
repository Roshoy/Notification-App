package lemury.biletomat.controller;

import javafx.scene.control.*;
import lemury.biletomat.model.departments.Department;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

        System.out.println(firstNameTextField.getText().toString());
        if(loginTextField.getText().isEmpty() || firstNameTextField.getText().isEmpty() || lastNameTextField.getText().isEmpty() ||
                passwordTextField.getText().isEmpty()  || (!standardUserRadioButton.isSelected() && !coordinatorRadioButton.isSelected())){
            new Alert(Alert.AlertType.ERROR, "Fulfill all fields").showAndWait();
            loginTextField.setText("");
            return;
        }

        if(standardUserRadioButton.isSelected()) {
            Optional<User> newUser = User.createUserAccount(loginTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText(), passwordTextField.getText());
            if(newUser.equals(Optional.empty())){
                new Alert(Alert.AlertType.ERROR, "User with that login already exists!").showAndWait();
                loginTextField.setText("");
                return;
            }
            this.users.add(newUser.get());

        }
        else if(coordinatorRadioButton.isSelected()) {
            departmentField.setDisable(false);
            String woow = departmentField.getSelectionModel().toString();
            System.out.println(woow);
            Optional<Coordinator> newCoordinator = Coordinator.createCoordinatorAccount(loginTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText(), passwordTextField.getText(),
                    Department.findIdByName(departmentField.getSelectionModel().getSelectedItem()));
            this.coordinators.add(newCoordinator.get());
        }

        Stage stage = (Stage) addUserButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void checkRadioButton(){
        actualiseChoiceBox();
    }


    @FXML
    public void actualiseChoiceBox(){
        if(standardUserRadioButton.isSelected()){
            this.departmentField.setDisable(true);
        }

        else if(coordinatorRadioButton.isSelected()){
            this.departmentField.setDisable(false);
        }

    }

    @FXML
    private  void handleCancelAction(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
