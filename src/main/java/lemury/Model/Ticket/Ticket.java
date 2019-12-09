package lemury.Model.Ticket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lemury.Model.Users.Administrator;
import lemury.Model.Users.Coordinator;
import lemury.Model.Users.User;
import lemury.Query.QueryExecutor;

import javax.management.Query;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Ticket {
    private final int id;
    private static final String TABLE_NAME = "TICKETS";

    private Coordinator owner; //if feels wierd, change (guy who takes care of this ticket, coordinator)
    private User submitter;

    private String title;
    private String description;
    private TicketStatus status;
    private String releaseNotes; //what did I do whit this ticket?
    //date etc


    public Ticket(int id, Coordinator owner, User submitter, String title, String description) {
        this.id = id;
        this.owner = owner;
        this.submitter = submitter;
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

    public User submitter() {
        return this.submitter;
    }

    public String title() {
        return this.title;
    }

    public String description() {
        return this.description;
    }

    public TicketStatus status(){
        return this.status;
    }

    //Changet type of returning value from Oprional<Ticket> to int
    public static int create(int coordinatorID, int userID, String title, String description) {
        String insertSql = String.format("INSERT INTO %s (COORDINATOR_ID, USER_ID, TITLE, DESCRIPTION, STATUS) VALUES (%d, %d, '%s', '%s', 'WAITING');",
                TABLE_NAME, coordinatorID, userID, title, description);
        int ticketID = 0;

        try {
            ticketID = QueryExecutor.createAndObtainId(insertSql);

            //return findTicketById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ticketID;

        //return Optional.empty();
    }

    public static Optional<Ticket> findTicketById(final int id) {
        String findBySql = String.format("SELECT * FROM %s WHERE id = %d;", TABLE_NAME, id);
        try {
            ResultSet rs = QueryExecutor.read(findBySql);
            return Optional.of(new Ticket(id, Administrator.findCoordinatorById(rs.getInt("coordinator_id")).get(),
                    User.findById(rs.getInt("user_id")).get(),
                    rs.getString("title"),
                    rs.getString("description")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static ObservableList<Ticket> getTicketsList(User user) {
        ObservableList<Ticket> result = FXCollections.observableArrayList();
        String sqlQuery = String.format("SELECT * FROM %s WHERE user_id = %d;", Ticket.TABLE_NAME, user.id());
        return getTickets(result, sqlQuery);
    }

    public static ObservableList<Ticket> filterTicketList(User user, boolean waiting, boolean inProgress, boolean done){
        StringBuilder search = new StringBuilder();

        if(waiting){
            search.append(",'WAITING'");
        }
        if(inProgress){
            search.append(",'IN_PROGRESS'");
        }
        if(done){
            search.append(",'DONE'");
        }
        if(search.length() > 0){
            search.deleteCharAt(0); // remove unnecessary coma
        }

        ObservableList<Ticket> result = FXCollections.observableArrayList();
        String sqlQuery = String.format("SELECT * FROM %s WHERE user_id = %d AND status IN (%s)",
                Ticket.TABLE_NAME, user.id(), search.toString());
        return getTickets(result, sqlQuery);
    }

    public static ObservableList<Ticket> getTicketsListOfCoordinator(Coordinator coordinator) {
        ObservableList<Ticket> result = FXCollections.observableArrayList();
        String sqlQuery = String.format("SELECT * FROM %s WHERE coordinator_id = %d;", Ticket.TABLE_NAME, coordinator.id());

        return getTickets(result, sqlQuery);
    }

    private static ObservableList<Ticket> getTickets(ObservableList<Ticket> result, String sqlQuery) {
        try {
            ResultSet rs = QueryExecutor.read(sqlQuery);
            while(rs.next()) {
                Ticket ticket = new Ticket(rs.getInt("id"),
                        (Coordinator)Coordinator.findById(rs.getInt("coordinator_id")).get(),
                        User.findById(rs.getInt("user_id")).get(), rs.getString("title"),
                        rs.getString("description"));
                result.add(ticket);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


}
