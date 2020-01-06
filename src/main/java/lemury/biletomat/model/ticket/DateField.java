package lemury.biletomat.model.ticket;

import lemury.biletomat.query.QueryExecutor;

import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;

public class DateField extends Field {
    private LocalDate date;
    private static final String TABLE_NAME = "TICKET_DETAILS_DATE";

    public DateField(String name, boolean required, LocalDate date){
        super(name, required, "date");
        this.date = date;
    }
//i dont know if it works
    public static int create(int fieldID, int ticketID, LocalDate value) {
        Date date = Date.valueOf(value);
        String insertSql = String.format("INSERT INTO %s (field_id, ticket_id, value) VALUES (%d, %d, %d);",
                TABLE_NAME, fieldID, ticketID,value);
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
