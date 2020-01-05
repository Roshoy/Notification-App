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
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class Ticket {
    private final int id;
    private static final String TABLE_NAME = "TICKETS";

    private ObjectProperty<Coordinator> owner; //if feels wierd, change (guy who takes care of this ticket, coordinator)
    private ObjectProperty<User> submitter;

    private StringProperty title;
    private StringProperty description;
    private ObjectProperty<TicketStatus> status;
    private ObjectProperty<Date> date;

    protected Ticket(int id, Coordinator owner, User submitter, String title, String description, TicketStatus status, Date date) {
        this.id = id;
        this.owner = new SimpleObjectProperty<>(owner);
        this.submitter = new SimpleObjectProperty<>(submitter);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleObjectProperty<>(status);
        this.date = new SimpleObjectProperty<>(date);
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

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", owner=" + owner +
                ", submitter=" + submitter +
                ", title=" + title +
                ", description=" + description +
                ", status=" + status +
                ", date=" + date +
                '}';
    }
}
