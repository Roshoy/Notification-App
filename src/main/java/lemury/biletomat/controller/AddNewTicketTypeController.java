package lemury.biletomat.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lemury.biletomat.model.departments.Department;
import lemury.biletomat.model.ticket.TicketStructure;
import lemury.biletomat.utils.TicketTypeLayoutConstants;
import lemury.biletomat.model.ticket.Ticket;
import java.util.Optional;


public class AddNewTicketTypeController {
    @FXML
    private TextField ticketTypeName;
    @FXML
    private TextField fieldName;
    @FXML
    private Button addButton;
    @FXML
    private Button submitButton;
    @FXML
    private Button addNewFieldButton;
    @FXML
    private ChoiceBox<String> departmentField;
    @FXML
    private ChoiceBox<String> dataTypeField;
    @FXML
    private AnchorPane pane1;
    @FXML
    private RadioButton requiredRadioButton;
    @FXML
    private Text chooseDataTypeText;
    @FXML
    private Text nameOfFieldText;
    @FXML
    private Text declaredFieldsText;
    @FXML
    private Text nameText;
    @FXML
    private Text typeText;
    @FXML
    private Text requiredText;

    private int deptID;

    private int counter = 0;

    private ObservableList<String> ticketStructureNames;


    private Label[] nameFields = new Label[Ticket.MAX_FIELDS];
    private Label[] typeFields = new Label[Ticket.MAX_FIELDS];
    private Label[] reqFields = new Label[Ticket.MAX_FIELDS];

    private static final ObservableList<String> TYPE_OPTIONS =
            FXCollections.observableArrayList(
                    "Int",
                    "String",
                    "Date"
            );

    public void setTicketStructureNames(ObservableList<String> ticketStructureNames) {
        this.ticketStructureNames = ticketStructureNames;
    }

    @FXML
    private void initialize() {
        departmentField.setItems(Department.getNames());

        for (int i = 0; i < Ticket.MAX_FIELDS; i++) {
            nameFields[i] = new Label();
            typeFields[i] = new Label();
            reqFields[i] = new Label();
        }

        dataTypeField.setItems(TYPE_OPTIONS);
        this.requiredRadioButton.setVisible(false);
        this.fieldName.setVisible(false);
        this.dataTypeField.setVisible(false);
        this.addNewFieldButton.setVisible(false);
        this.submitButton.setVisible(false);

        this.chooseDataTypeText.setVisible(false);
        this.nameOfFieldText.setVisible(false);
        this.declaredFieldsText.setVisible(false);
        this.nameText.setVisible(false);
        this.typeText.setVisible(false);
        this.requiredText.setVisible(false);
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        System.out.println(this.departmentField.getValue());
        if (this.ticketTypeName.getText().isEmpty() || this.departmentField.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Fulfill ticket type field").showAndWait();
        } else {
            deptID = Department.findIdByName(this.departmentField.getValue());
            //System.out.println(this.departmentField.getValue());
            this.requiredRadioButton.setVisible(true);
            this.ticketTypeName.setDisable(true);
            this.departmentField.setDisable(true);
            this.fieldName.setVisible(true);
            this.dataTypeField.setVisible(true);
            this.addNewFieldButton.setVisible(true);
            this.submitButton.setVisible(true);

            this.chooseDataTypeText.setVisible(true);
            this.nameOfFieldText.setVisible(true);
            this.declaredFieldsText.setVisible(true);
            this.nameText.setVisible(true);
            this.typeText.setVisible(true);
            this.requiredText.setVisible(true);
        }
    }

    @FXML
    private void handleAddFieldAction(ActionEvent event) {
        System.out.println(counter);
        if (counter > Ticket.MAX_FIELDS - 1) {
            new Alert(Alert.AlertType.ERROR, String.format("Can't add more fields, limit reached(%s)", Ticket.MAX_FIELDS))
                    .showAndWait();
            return;
        }

        if (this.fieldName.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Field name cannot be empty").showAndWait();
            return;
        }
        if (this.dataTypeField.getValue().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Data type cannot be empty").showAndWait();
            return;
        }

        //pane1.getChildren().add(valueField[counter]);
        pane1.getChildren().add(nameFields[counter]);
        pane1.getChildren().add(reqFields[counter]);
        pane1.getChildren().add(typeFields[counter]);

        int y = TicketTypeLayoutConstants.GAP * counter;
        nameFields[counter].setLayoutX(TicketTypeLayoutConstants.NAME_X);
        nameFields[counter].setLayoutY(TicketTypeLayoutConstants.LABEL_Y + y);

        reqFields[counter].setLayoutX(TicketTypeLayoutConstants.REQUIRED_X);
        reqFields[counter].setLayoutY(TicketTypeLayoutConstants.LABEL_Y + y);

        //valueField[counter].setLayoutX(valueFieldX);
        //valueField[counter].setLayoutY(valueFieldY + y);

        typeFields[counter].setLayoutX(TicketTypeLayoutConstants.TYPE_X);
        typeFields[counter].setLayoutY(TicketTypeLayoutConstants.LABEL_Y + y);


        nameFields[counter].setText(this.fieldName.getText());
        typeFields[counter].setText(this.dataTypeField.getValue());

        if (this.requiredRadioButton.isSelected()) {
            reqFields[counter].setText("true");
        } else {
            reqFields[counter].setText("false");
        }

        this.fieldName.setText("");
        this.requiredRadioButton.setSelected(false);

        counter++;
    }

    @FXML
    private void handleSubmitAction(ActionEvent event) {

        int ticketStructureId = TicketStructure.create(this.ticketTypeName.getText(), deptID);
        Optional<TicketStructure> optionalTicketStructure = TicketStructure.findById(ticketStructureId);

        if (!optionalTicketStructure.isPresent()) {
            new Alert(Alert.AlertType.ERROR, "Error with saving ticket structure to database").showAndWait();
            return;
        }

        TicketStructure ticketStructure = optionalTicketStructure.get();
        this.ticketStructureNames.add(ticketStructure.name());

        for (int i = 0; i < counter; i++) {
            ticketStructure.addField(this.nameFields[i].getText(), Boolean.parseBoolean(this.reqFields[i].getText()), this.typeFields[i].getText().toLowerCase());
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setContentText("Ticket type has been added");

        alert.showAndWait();
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}
