package lemury.biletomat.model.ticket;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lemury.biletomat.query.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TicketStructure {
    private static final String TABLE_NAME = "TICKET_STRUCTURE";
    private List<Field> fields;
    private String name;
    private int department_id;

    public TicketStructure(String name, int department_id){
        this.name = name;
        this.department_id = department_id;
        fields = new ArrayList<>();
    }


    public static ObservableList<String> getNames(){
        String getSql = String.format("SELECT name FROM %s",TABLE_NAME);
        ObservableList<String> result = FXCollections.observableArrayList();

        try {
            ResultSet rs = QueryExecutor.read(getSql);
            while (rs.next()){
                String departmentName = rs.getString("name");
                result.add(departmentName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static int getCount(int ticketStructureId) throws SQLException {
        String query = String.format("SELECT COUNT(*) as count FROM TICKET_STRUCTURE_DETAILS WHERE ticket_structure_id = %d", ticketStructureId);
        ResultSet rs = QueryExecutor.read(query);
        int counter = QueryExecutor.readIdFromResultSet(rs);
        return counter;
    }

    public static int getIdFromName(String name) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE name = '%s'", TABLE_NAME, name);
        ResultSet rs = QueryExecutor.read(query);
        int id = QueryExecutor.readIdFromResultSet(rs);
        return id;
    }

    public static int getDepartmentIdFromId(int id) throws SQLException{
        String query = String.format("SELECT department_id FROM %s WHERE id = %d", TABLE_NAME, id );
        ResultSet rs = QueryExecutor.read(query);
        int deptId = QueryExecutor.readIdFromResultSet(rs);
        return deptId;
    }


    public static void setNewTicketPane(Label[] nameFields, Label[] typeFields, Label[] reqFields, TextField[] valueFields, int ticketStructureId, int counter) throws SQLException {
        String query = String.format("SELECT * FROM TICKET_STRUCTURE_DETAILS WHERE ticket_structure_id = %d", ticketStructureId);
        ResultSet rs = QueryExecutor.read(query);
        int i = 0;
        while(rs.next()){
            nameFields[i].setText(rs.getString("name"));
            typeFields[i].setText(rs.getString("type"));
            reqFields[i].setText(rs.getString("required"));
            i++;
        }

    }

    public static int getTicketStructureDetailsIdFromId(int ticketStructureId, String name) throws SQLException {
        String query = String.format("SELECT id FROM TICKET_STRUCTURE_DETAILS WHERE ticket_structure_id = %d and name = '%s' ", ticketStructureId, name);
        ResultSet rs = QueryExecutor.read(query);
        int id = QueryExecutor.readIdFromResultSet(rs);
        return id;
    }


    public int insertToDb(){
        String insertSql = String.format("INSERT INTO %s(name, department_id) VALUES ('%s', %d)", TABLE_NAME, this.name, this.department_id);
        int ticketStructureId = 0;
        try {
            ticketStructureId = QueryExecutor.createAndObtainId(insertSql);

            //return findTicketById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(ticketStructureId != 0) {
            for (int i = 0; i < fields.size(); i++) {
                this.fields.get(i).insertToDb(ticketStructureId);
            }
        };

        return ticketStructureId;
    }


    public void addIntField(String name, boolean required, int value){
        IntField intField = new IntField(name, required, value);
        fields.add(intField);
    }

    public void addStringField(String name, boolean required, String value){
        StringField stringField = new StringField(name, required, value);
        fields.add(stringField);
    }

    public void addDateField(String name, boolean required, LocalDate date){
        DateField dateField = new DateField(name, required, date);
        fields.add(dateField);
    }

    public List<Field> getFields() {
        return fields;
    }


}
