package lemury.biletomat.model.ticket;

import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import lemury.biletomat.query.QueryExecutor;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class IntField extends Field {
    private int value;
    private static final String TABLE_NAME = "TICKET_DETAILS_INT";

    public IntField(String name, boolean required, int value) {
        super(name, required, "int");
        this.value = value;
    }

    public static int create(int fieldID, int ticketID, int value) {
        String insertSql = String.format("INSERT INTO %s (field_id, ticket_id, value) VALUES (%d, %d, %d);",
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


        //return Optional.empty();
}


