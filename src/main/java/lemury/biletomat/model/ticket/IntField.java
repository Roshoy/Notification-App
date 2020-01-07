package lemury.biletomat.model.ticket;

import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import lemury.biletomat.query.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class IntField extends Field {
    private int value;
    private static final String TABLE_NAME = "TICKET_DETAILS_INT";

    //TODO - public -> private
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


    public static int create(int fieldID, int ticketID) {
        String insertSql = String.format("INSERT INTO %s (field_id, ticket_id) VALUES (%d, %d);",
                TABLE_NAME, fieldID, ticketID);
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


