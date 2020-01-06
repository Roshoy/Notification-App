package lemury.biletomat.model.ticket;

import lemury.biletomat.query.QueryExecutor;

import java.sql.SQLException;

public class StringField extends Field {
    private String value;
    private static final String TABLE_NAME = "TICKET_DETAILS_STRING";

    public StringField(String name, boolean required, String value){
        super(name, required, "string");
        this.value = value;
    }

    public static int create(int fieldID, int ticketID, String value) {
        String insertSql = String.format("INSERT INTO %s (field_id, ticket_id, value) VALUES (%d, %d, '%s');",
                TABLE_NAME, fieldID, ticketID, value);
        int tickID = 0;

        try {
            tickID = QueryExecutor.createAndObtainId(insertSql);
            //return findTicketById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickID;
    }
}
