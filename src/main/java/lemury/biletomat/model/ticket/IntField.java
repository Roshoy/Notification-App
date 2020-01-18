package lemury.biletomat.model.ticket;

import lemury.biletomat.query.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class IntField extends Field {
    private int value;
    private static final String TABLE_NAME = "TICKET_DETAILS_INT";
    private static final String PARENT_TABLE_NAME = "TICKET_STRUCTURE_DETAILS";

    private IntField(int id, String name, boolean required, int value) {
        super(id, name, required, "int");
        this.value = value;
    }

    public static int create(int fieldID, int ticketID, int value) {
        String insertSql = String.format("INSERT INTO %s (field_id, ticket_id, value) VALUES (%d, %d, %d);",
                TABLE_NAME, fieldID, ticketID, value);

        try {
            return QueryExecutor.createAndObtainId(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static Optional<IntField> findIntFieldByTicketId(int fieldId, int ticketId) {
        String findSql = String.format("SELECT * FROM %s AS if JOIN %s as tsd ON if.field_id = tsd.id WHERE if.ticket_id = %d AND tsd.id = %d;",
                TABLE_NAME, PARENT_TABLE_NAME, ticketId, fieldId);

        try {
            ResultSet rs = QueryExecutor.read(findSql);
            return Optional.of(new IntField(rs.getInt("field_id"), rs.getString("name"), rs.getBoolean("required"), rs.getInt("value")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static int countFields(int ticket_id) {
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


