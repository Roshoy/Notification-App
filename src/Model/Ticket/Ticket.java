package Model.Ticket;

import Model.Users.Administrator;
import Model.Users.Coordinator;
import Model.Users.User;
import Query.QueryExecutor;

import javax.management.Query;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Ticket {
    private final int id;
    public static final String TABLE_NAME = "TICKETS";

    private Coordinator owner; //if feels wierd, change (guy who takes care of this ticket, coordinator)
    private User submitee;

    private String title;
    private String description;
    private TicketStatus status;
    private String releaseNotes; //what did I do whit this ticket?
    //date etc


    public Ticket(int id, Coordinator owner, User submitee, String title, String description) {
        this.id = id;
        this.owner = owner;
        this.submitee = submitee;
        this.title = title;
        this.description = description;
        this.status = TicketStatus.WAITING;
    }

   // public Ticket(){};

    public int id() {
        return this.id;
    }

    public Coordinator owner() {
        return this.owner;
    }

    public User submitee() {
        return this.submitee;
    }

    public String title() {
        return this.title;
    }

    public String description() {
        return this.description;
    }

    public TicketStatus status(){return  this.status;}

    public static Optional<Ticket> create(int coordinatorID, int userID, String title, String description) {
        String insertSql = String.format("INSERT INTO %s (COORDINATOR_ID, USER_ID, TITLE, DESCRIPTION, STATUS) VALUES (%d, %d, '%s', '%s', 'WAITING');", TABLE_NAME,
                coordinatorID, userID, title, description);

        try {
            int id = QueryExecutor.createAndObtainId(insertSql);
            return findTicketById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static Optional<Ticket> findTicketById(final int id) {
        String findBySql = String.format("SELECT * FROM %s WHERE id = %d;", TABLE_NAME, id);
        try {
            ResultSet rs = QueryExecutor.read(findBySql);
            return Optional.of(new Ticket(id, Administrator.findCoordinatorById(rs.getInt("coordinator_id")).get(),
                    Administrator.findUserById(rs.getInt("user_id")).get(),
                    rs.getString("title"),
                    rs.getString("description")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
