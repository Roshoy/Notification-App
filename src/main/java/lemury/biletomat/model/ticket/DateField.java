package lemury.biletomat.model.ticket;

import lemury.biletomat.query.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DateField extends Field {
    private LocalDate value;
    private static final String TABLE_NAME = "TICKET_DETAILS_DATE";
    private static final String PARENT_TABLE_NAME = "TICKET_STRUCTURE_DETAILS";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private DateField(int id, String name, boolean required, LocalDate value){
        super(id, name, required, "date");
        this.value = value;
    }

    //i dont know if it works
    public static int create(int fieldID, int ticketID, LocalDate value) {
        String dateString = dateTimeFormatter.format(value);
        String insertSql = String.format("INSERT INTO %s (field_id, ticket_id, value) VALUES (%d, %d, '%s');",
                TABLE_NAME, fieldID, ticketID, dateString);

        try {
            return QueryExecutor.createAndObtainId(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static Optional<DateField> findDateFieldByTicketId(int fieldId, int ticketId) {
        String findSql = String.format("SELECT * FROM %s AS df JOIN %s as tsd ON df.field_id = tsd.id WHERE df.ticket_id = %d AND tsd.id = %d;",
                TABLE_NAME, PARENT_TABLE_NAME, ticketId, fieldId);

        try {
            ResultSet rs = QueryExecutor.read(findSql);
            LocalDate value = LocalDate.parse(rs.getString("value"), dateTimeFormatter);
            return Optional.of(new DateField(rs.getInt("field_id"), rs.getString("name"), rs.getBoolean("required"), value));
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
