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
}
