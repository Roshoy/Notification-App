package lemury.biletomat.model.ticket;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    public TicketStructure(String name){
        this.name = name;
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


    public int insertToDb(){
        String insertSql = String.format("INSERT INTO %s(name) VALUES ('%s')", TABLE_NAME, this.name);
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
