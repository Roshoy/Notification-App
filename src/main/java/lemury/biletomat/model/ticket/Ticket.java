package lemury.biletomat.model.ticket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lemury.biletomat.model.users.Administrator;
import lemury.biletomat.query.QueryExecutor;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private Date date;
    //date etc


    public Ticket(int id, Coordinator owner, User submitter, String title, String description, Date date) {
        this.id = id;
        this.owner = owner;
        this.submitter = submitter;
        this.title = title;
        this.description = description;
        this.status = TicketStatus.WAITING;
        this.date = date;
    }

   // public ticket(){};

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

    public Date date() { return this.date; }

    //Changet type of returning value from Oprional<ticket> to int
    public static int create(int coordinatorID, int userID, String title, String description) {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        String insertSql = String.format("INSERT INTO %s (COORDINATOR_ID, USER_ID, TITLE, DESCRIPTION, STATUS, DATE) VALUES (%d, %d, '%s', '%s', 'WAITING', '%s');",
                TABLE_NAME, coordinatorID, userID, title, description, dateString);
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
            Date ticketDate = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").parse(rs.getString("DATE"));
            return Optional.of(new Ticket(id, Administrator.findCoordinatorById(rs.getInt("coordinator_id")).get(),
                    User.findById(rs.getInt("user_id")).get(),
                    rs.getString("title"),
                    rs.getString("description"),
                    ticketDate));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch(ParseException e) {
            System.out.println("Error with date parsing");
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
                Date ticketDate = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").parse(rs.getString("DATE"));
                Ticket ticket = new Ticket(rs.getInt("id"),
                        (Coordinator)Coordinator.findById(rs.getInt("coordinator_id")).get(),
                        User.findById(rs.getInt("user_id")).get(), rs.getString("title"),
                        rs.getString("description"),
                        ticketDate);
                result.add(ticket);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } catch(ParseException e) {
            System.out.println("Error with date parsing");
            e.printStackTrace();
        }

        return result;
    }


}
