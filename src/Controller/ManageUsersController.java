package Controller;

import Model.Users.Coordinator;
import Model.Users.User;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ManageUsersController {
    private ObservableList<User> users;
    private ObservableList<Coordinator> coordinators;

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> userIDColumn;
    @FXML
    private TableColumn<User, String> userFirstNameColumn;
    @FXML
    private TableColumn<User, String> userLastNameColumn;
    @FXML
    private TableColumn<User, String> userLoginColumn;
    @FXML
    private TableColumn<User, String> userPasswordColumn;

    @FXML
    private TableView<Coordinator> coordinatorsTable;
    @FXML
    private TableColumn<Coordinator, Integer> coordinatorIDColumn;
    @FXML
    private TableColumn<Coordinator, String> coordinatorFirstNameColumn;
    @FXML
    private TableColumn<Coordinator, String> coordinatorLastNameColumn;
    @FXML
    private TableColumn<Coordinator, String> coordinatorLoginColumn;
    @FXML
    private TableColumn<Coordinator, String> coordinatorPasswordColumn;
    @FXML
    private TableColumn<Coordinator, String> coordinatorDepartmentColumn;

    @FXML
    private void initialize() {
        userIDColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().id()));
        userFirstNameColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().firstName()));
        userLastNameColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().lastName()));
        userLoginColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().getLogin()));
        userPasswordColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().getPassword()));

        coordinatorIDColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().id()));
        coordinatorFirstNameColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().firstName()));
        coordinatorLastNameColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().lastName()));
        coordinatorLoginColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().getLogin()));
        coordinatorPasswordColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().getPassword()));
        coordinatorDepartmentColumn.setCellValueFactory(dataValue -> new SimpleObjectProperty<>(dataValue.getValue().getDepartment().name()));
    }

    public void setUsers(ObservableList<User> users) {
        this.users = users;
        usersTable.setItems(users);
    }

    public void setCoordinators(ObservableList<Coordinator> coordinators) {
        this.coordinators = coordinators;
        coordinatorsTable.setItems(coordinators);
    }
}
