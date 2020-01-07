package lemury.biletomat.model.ticket;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lemury.biletomat.query.QueryExecutor;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Ticket {
    private final int id;
    private static final String TABLE_NAME = "TICKETS";
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ObjectProperty<Coordinator> owner; //if feels wierd, change (guy who takes care of this ticket, coordinator)
    private ObjectProperty<User> submitter;

    private StringProperty title;
    private StringProperty description;
    private ObjectProperty<TicketStatus> status;
    private ObjectProperty<Date> date;

    private List<Message> ticketMessages;

    protected Ticket(int id, Coordinator owner, User submitter, String title, String description,
                     TicketStatus status, Date date) {
        this.id = id;
        this.owner = new SimpleObjectProperty<>(owner);
        this.submitter = new SimpleObjectProperty<>(submitter);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleObjectProperty<>(status);
        this.date = new SimpleObjectProperty<>(date);
        this.status.addListener(c->updateTicket());
    }

    public int id() {
        return this.id;
    }

    public Coordinator owner() {
        return this.owner.get();
    }

    public ObjectProperty<Coordinator> getOwnerProperty() { return this.owner; }

    public User submitter() {
        return this.submitter.get();
    }

    public ObjectProperty<User> getSubmitterProperty() { return this.submitter; }

    public String title() {
        return this.title.get();
    }

    public StringProperty getTitleProperty() { return  this.title; }

    public String description() {
        return this.description.get();
    }

    public StringProperty getDescriptionProperty() { return this.description; }

    public TicketStatus status(){
        return this.status.get();
    }

    public ObjectProperty<TicketStatus> getStatusProperty() { return this.status; }

    public Date date() { return this.date.get(); }

    public ObjectProperty<Date> getDateProperty() { return this.date; }

    public List<Message> ticketMessages() { return this.ticketMessages; }

    //Changet type of returning value from Oprional<ticket> to int
    public static int create(int coordinatorID, int userID, String title, String description) {
        Date date = Calendar.getInstance().getTime();
        String dateString = dateFormat.format(date);
        String insertSql = String.format("INSERT INTO %s (COORDINATOR_ID, USER_ID, TITLE, DESCRIPTION, STATUS, DATE) VALUES (%d, %d, '%s', '%s', 'WAITING', '%s');",
                TABLE_NAME, coordinatorID, userID, title, description, dateString);
        int ticketID = 0;

        if(Coordinator.findById(coordinatorID).isPresent() && User.findById(userID).isPresent()) {
            try {
                ticketID = QueryExecutor.createAndObtainId(insertSql);

                //return findTicketById(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ticketID;
    }

    public static Optional<Ticket> findTicketById(final int id) {
        String findBySql = String.format("SELECT * FROM %s WHERE id = %d;", TABLE_NAME, id);
        try {
            ResultSet rs = QueryExecutor.read(findBySql);
            Date ticketDate = dateFormat.parse(rs.getString("DATE"));
            System.out.println(ticketDate);
            return Optional.of(new Ticket(id, Coordinator.findCoordinatorById(rs.getInt("coordinator_id")).get(),
                    User.findById(rs.getInt("user_id")).get(),
                    rs.getString("title"),
                    rs.getString("description"),
                    TicketStatus.valueOf(rs.getString("status")),
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
        return getTickets(result, sqlQuery, user, null);
    }

    public static ObservableList<Ticket> getTicketsListOfCoordinator(Coordinator coordinator) {
        ObservableList<Ticket> result = FXCollections.observableArrayList();
        String sqlQuery = String.format("SELECT * FROM %s WHERE coordinator_id = %d;",
                Ticket.TABLE_NAME, coordinator.id());

        return getTickets(result, sqlQuery, null, coordinator);
    }

    private static ObservableList<Ticket> getTickets(ObservableList<Ticket> result, String sqlQuery, User submitter,
                                                     Coordinator owner) {
        try {
            ResultSet rs = QueryExecutor.read(sqlQuery);
            while(rs.next()) {
                Date ticketDate = dateFormat.parse(rs.getString("DATE"));
                if(submitter == null){
                    submitter = User.findById(rs.getInt("user_id")).get();
                }
                if(owner == null){
                    owner = (Coordinator)Coordinator.findById(rs.getInt("coordinator_id")).get();
                }

                Ticket ticket = new Ticket(rs.getInt("id"),
                        owner, submitter, rs.getString("title"),
                        rs.getString("description"),
                        TicketStatus.valueOf(rs.getString("status")),
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

    public static void deleteTicketById(int ticketId) {
        String sqlQuery = String.format("DELETE FROM %s WHERE id = %d;", TABLE_NAME, ticketId);

        try {
            QueryExecutor.delete(sqlQuery);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTicket(){
        String sqlQuery = String.format(
                "UPDATE %s SET COORDINATOR_ID = %d, USER_ID = %d, TITLE = '%s', DESCRIPTION = '%s', STATUS = '%s' WHERE ID = %d;",
                TABLE_NAME, this.owner.get().id(), this.submitter.get().id(), this.title, this.description, this.status.get(), this.id);
        try {
            QueryExecutor.create(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTicketStatus(TicketStatus newTicketStatus) {
        String sqlQuery = String.format("UPDATE %s SET STATUS = '%s' WHERE ID = %d;", TABLE_NAME, newTicketStatus.toString(), this.id);
        try {
            QueryExecutor.create(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.status.set(newTicketStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return id == ticket.id() &&
                this.owner().equals(ticket.owner()) &&
                this.submitter().equals(ticket.submitter()) &&
                this.title().equals(ticket.title()) &&
                this.description().equals(ticket.description()) &&
                this.status().equals(ticket.status()) &&
                this.date().equals(ticket.date());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, submitter, title, description, status, date);
    }
}
