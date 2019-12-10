package lemury.Model.Ticket;

import lemury.Model.Users.Coordinator;
import lemury.Model.Users.User;
import lemury.Query.QueryExecutor;

import java.sql.SQLException;
import java.util.Date;

public class ITTicket extends Ticket {
    private static String department = "TEST";
    private  int computerNo;

    public static String getDepartment() {
        return department;
    }

    public static void create(int ticketId, int compNo) throws SQLException {
        String sqlInsertIT = String.format("INSERT INTO ITTICKETS(id, computer_no) VALUES ('%d', '%d')", ticketId, compNo );
        QueryExecutor.createAndObtainId(sqlInsertIT);
    }

    private ITTicket(int id, Coordinator owner, User submitter, String title, String description, int computerNo, Date date){
        super(id, owner, submitter, title, description, date);
        this.computerNo = computerNo;
    }
}
