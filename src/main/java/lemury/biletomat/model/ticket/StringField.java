package lemury.biletomat.model.ticket;

import lemury.biletomat.query.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class StringField extends Field {
    private String value;
    private static final String TABLE_NAME = "TICKET_DETAILS_STRING";
    private static final String PARENT_TABLE_NAME = "TICKET_STRUCTURE_DETAILS";

    private StringField(int id, String name, boolean required, String value){
        super(id, name, required, "string");
        this.value = value;
    }

    public static int create(int fieldID, int ticketID, String value) {
        String insertSql = String.format("INSERT INTO %s (field_id, ticket_id, value) VALUES (%d, %d, '%s');",
                TABLE_NAME, fieldID, ticketID, value);

        try {
            return QueryExecutor.createAndObtainId(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Optional<StringField> findStringFieldByTicketId(int fieldId, int ticketId) {
        String findSql = String.format("SELECT * FROM %s AS sf JOIN %s as tsd ON sf.field_id = tsd.id WHERE sf.ticket_id = %d AND tsd.id = %d;",
                TABLE_NAME, PARENT_TABLE_NAME, ticketId, fieldId);

        try {
            ResultSet rs = QueryExecutor.read(findSql);
            return Optional.of(new StringField(fieldId, rs.getString("name"), rs.getBoolean("required"), rs.getString("value")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    public static int countFields(int ticket_id){
        String sql = String.format("SELECT COUNT(*) as counter FROM %s WHERE ticket_id = %d", TABLE_NAME, ticket_id);
        System.out.println(sql);
        int counter = 0;

        try {
            ResultSet rs = QueryExecutor.read(sql);
            counter = QueryExecutor.readIdFromResultSet(rs);
            System.out.println(counter);
            //return findTicketById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }
}
