package lemury.biletomat.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lemury.biletomat.model.departments.Department;
import lemury.biletomat.model.ticket.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    int deptID;

    int counter = 0;

    final int valueFieldX = 26;
    final int valueFieldY = 222;
    final int nameX = 242;
    final int typeX = 370;
    final int requiredX = 480;
    final int labelY = 230;
    final int gap =30;


    private Label nameFields[] = new Label[10];
    private Label typeFields[] = new Label[10];
    private Label reqFields[] = new Label[10];
    // private TextField valueField[] = new TextField[10];


    @FXML
    private void initialize() {
        departmentField.setItems(Department.getNames());

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Int",
                        "String",
                        "Date"
                );

        for(int i =0; i <10; i++){
            nameFields[i] = new Label();
            typeFields[i] = new Label();
            reqFields[i] = new Label();
            //valueField[i] = new TextField();
        }


        dataTypeField.setItems(options);
        this.requiredRadioButton.setVisible(false);
        this.fieldName.setVisible(false);
        this.dataTypeField.setVisible(false);
        this.addNewFieldButton.setVisible(false);
        this.submitButton.setVisible(false);

    }



    @FXML
    private void handleAddAction(ActionEvent event){
        System.out.println((String) this.departmentField.getValue());
        if(this.ticketTypeName.getText().isEmpty() ||  this.departmentField.getValue() == null){
            new Alert(Alert.AlertType.ERROR, "Fulfill ticket type field").showAndWait();
        }
        else{
            deptID = Department.findIdByName((String) this.departmentField.getValue());
            //System.out.println(this.departmentField.getValue());
            this.requiredRadioButton.setVisible(true);
            this.ticketTypeName.setDisable(true);
            this.departmentField.setDisable(true);
            this.fieldName.setVisible(true);
            this.dataTypeField.setVisible(true);
            this.addNewFieldButton.setVisible(true);
            this.submitButton.setVisible(true);

        }
    }

    @FXML
    private void handleAddFieldAction(ActionEvent event){
        System.out.println(counter);
        if(counter > 9){
            new Alert(Alert.AlertType.ERROR, "Fulfill ticket type field").showAndWait();
            return;
        }

        if(this.fieldName.getText().isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Field name cannot be empty").showAndWait();
            return;
        }
        if(this.dataTypeField.getValue().isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Data type cannot be empty").showAndWait();
            return;
        }


        //pane1.getChildren().add(valueField[counter]);
        pane1.getChildren().add(nameFields[counter]);
        pane1.getChildren().add(reqFields[counter]);
        pane1.getChildren().add(typeFields[counter]);

        int y = gap*counter;
        nameFields[counter].setLayoutX(nameX );
        nameFields[counter].setLayoutY(labelY + y);

        reqFields[counter].setLayoutX(requiredX );
        reqFields[counter].setLayoutY(labelY+ y);

        //valueField[counter].setLayoutX(valueFieldX);
        //valueField[counter].setLayoutY(valueFieldY + y);

        typeFields[counter].setLayoutX(typeX);
        typeFields[counter].setLayoutY(labelY + y);


        nameFields[counter].setText(this.fieldName.getText());
        typeFields[counter].setText(this.dataTypeField.getValue());

        if(this.requiredRadioButton.isSelected()){
            reqFields[counter].setText("true");
        }
        else{
            reqFields[counter].setText("false");
        }

        this.fieldName.setText("");
        this.requiredRadioButton.setSelected(false);

        counter++;
    }

    @FXML
    private void handleSubmitAction(ActionEvent event){

        int ticketStructureId = TicketStructure.create(this.ticketTypeName.getText(), deptID);
        Optional<TicketStructure> optionalTicketStructure = TicketStructure.findById(ticketStructureId);

        if(!optionalTicketStructure.isPresent())
        {
            new Alert(Alert.AlertType.ERROR, "Error with saving ticket structure to database").showAndWait();
            return;
        }

        TicketStructure ticketStructure = optionalTicketStructure.get();

        for(int i = 0; i < counter; i++){
            ticketStructure.addField(this.nameFields[i].getText(), Boolean.parseBoolean(this.reqFields[i].getText()), this.typeFields[i].getText().toLowerCase());
        }
    }
}
