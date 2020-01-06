package lemury.biletomat.model.ticket;

import lemury.biletomat.query.QueryExecutor;

import java.sql.SQLException;

public class Field {
    private String name;
    private boolean required;
    private String type;

    private static final String TABLE_NAME = "TICKET_STRUCTURE_DETAILS";

    public Field(String name, boolean required, String type){
        this.name = name;
        this.required = required;
        this.type = type;
    }

    public int insertToDb(int ticketStructureId){
        String insertSql = String.format("INSERT INTO %s(ticket_structure_id, name, required, type) VALUES (%d, '%s', %b, '%s')", TABLE_NAME, ticketStructureId, this.name, this.required, this.type);
        System.out.println(insertSql);
        int id =0;
        try {
            id = QueryExecutor.createAndObtainId(insertSql);

            //return findTicketById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static int countFields(int ticket_id){
        String sql = String.format("SELECT COUNT(*) as counter FROM %s WHERE ticket_id = %d", TABLE_NAME, ticket_id);
        System.out.println(sql);
        int counter = 0;

        try {
            counter = QueryExecutor.createAndObtainId(sql);
            //return findTicketById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }
}
