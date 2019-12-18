package lemury.biletomat.model.ticket;

import lemury.biletomat.query.QueryExecutor;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import org.graalvm.compiler.nodes.calc.IntegerDivRemNode;

import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

public class ITTicket extends Ticket {
    private static String department = "TEST";
    private  int computerNo;

    public static String getDepartment() {
        return department;
    }

    public static void create(int ticketId, int compNo) throws SQLException {
        Optional <Ticket> ticket = Ticket.findTicketById(ticketId);
        if(ticket.equals(Optional.empty())){
            return;
        }
        String sqlInsertIT = String.format("INSERT INTO ITTICKETS(id, computer_no) VALUES ('%d', '%d')", ticketId, compNo );
        QueryExecutor.createAndObtainId(sqlInsertIT);

    }

    private ITTicket(int id, Coordinator owner, User submitter, String title, String description, int computerNo, TicketStatus status, Date date){
        super(id, owner, submitter, title, description, status, date);
        this.computerNo = computerNo;
    }
}
