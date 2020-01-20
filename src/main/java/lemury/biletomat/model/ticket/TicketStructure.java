package lemury.biletomat.model.ticket;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lemury.biletomat.model.departments.Department;
import lemury.biletomat.query.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketStructure {
    private static final String TABLE_NAME = "TICKET_STRUCTURE";
    private int id;
    private List<Field> fields;
    private String name;

    private TicketStructure(int id, String name){
        this.id = id;
        this.name = name;
        fields = new ArrayList<>();
    }

    public String name() {
        return this.name;
    }

    public static int create(String name, int department_id) {
        String insertSql = String.format("INSERT INTO %s (name, department_id) VALUES ('%s', %d)", TABLE_NAME, name, department_id);
        if(Department.findById(department_id).equals(Optional.empty())) {
            return -1;
        }

        try {
            return QueryExecutor.createAndObtainId(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static Optional<TicketStructure> findById(int id) {
        String findByIdSql = String.format("SELECT * FROM %s WHERE id = %d;", TABLE_NAME, id);

        try {
            ResultSet rs = QueryExecutor.read(findByIdSql);

            return Optional.of(new TicketStructure(rs.getInt("id"), rs.getString("name")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
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
        return QueryExecutor.readIdFromResultSet(rs);
    }

    public static int getIdFromName(String name) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE name = '%s'", TABLE_NAME, name);
        ResultSet rs = QueryExecutor.read(query);
        return QueryExecutor.readIdFromResultSet(rs);
    }

    public static int getDepartmentIdFromId(int id) throws SQLException{
        String query = String.format("SELECT department_id FROM %s WHERE id = %d", TABLE_NAME, id );
        ResultSet rs = QueryExecutor.read(query);
        return QueryExecutor.readIdFromResultSet(rs);
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

    public static int getTicketStructureDetailsIdFromTicketId(int ticketStructureId, String name) throws SQLException {
        String query = String.format("SELECT id FROM TICKET_STRUCTURE_DETAILS WHERE ticket_structure_id = %d and name = '%s';", ticketStructureId, name);
        ResultSet rs = QueryExecutor.read(query);
        return QueryExecutor.readIdFromResultSet(rs);
    }

/*
    public int insertToDb(){
        String insertSql = String.format("INSERT INTO %s(name, department_id) VALUES ('%s', %d)", TABLE_NAME, this.name, this.department.id());
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
*/

    public void addField(String name, boolean required, String type){
        int fieldId = Field.create(this.id, name, required, FieldType.valueOf(type.toUpperCase()));
        Optional<Field> newField = Field.findFieldById(fieldId);

        newField.ifPresent(field -> fields.add(field));
    }

    public List<Field> getFields() {
        return fields;
    }
}
