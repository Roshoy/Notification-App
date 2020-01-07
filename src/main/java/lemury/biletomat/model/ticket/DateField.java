package lemury.biletomat.model.ticket;

import lemury.biletomat.query.QueryExecutor;

import java.sql.ResultSet;
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
        String insertSql = String.format("INSERT INTO %s (field_id, ticket_id, value) VALUES (%d, %d, '%s');",
                TABLE_NAME, fieldID, ticketID, value.toString());
        int tickID = 0;

        try {
            tickID = QueryExecutor.createAndObtainId(insertSql);
            //return findTicketById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickID;
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
