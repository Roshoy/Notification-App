package lemury.biletomat.model.ticket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lemury.biletomat.query.QueryExecutor;
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

public class Message {
    private final int id;
    private static final String TABLE_NAME = "MESSAGES";
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Date date;
    private Ticket referencedTicket;
    private User author;
    private String text;

    private Message(int id, Date date, Ticket referencedTicket, User author, String text) {
        this.id = id;
        this.date = date;
        this.referencedTicket = referencedTicket;
        this.author = author;
        this.text = text;
    }

    public int id() {
        return id;
    }

    public Date date() {
        return date;
    }

    public Ticket referencedTicket() {
        return referencedTicket;
    }

    public User author() {
        return author;
    }

    public String text() {
        return text;
    }

    public static Optional<Message> create(int ticketID, int authorID, String text) {
        Optional<Ticket> ticket = Ticket.findTicketById(ticketID);
        Optional<User> author = User.findById(authorID);

        if(ticket.equals(Optional.empty()) || author.equals(Optional.empty())){
            return Optional.empty();
        }

        Date date = Calendar.getInstance().getTime();
        String dateString = dateFormat.format(date);
        System.out.println("Date: " + date);
        System.out.println("Date string: " + dateString);
        String insertSql = String.format("INSERT INTO %s (DATE, TICKET_ID, AUTHOR_ID, TEXT) VALUES ('%s', %d, %d, '%s');", TABLE_NAME, dateString, ticketID, authorID, text);

        try {
            int id = QueryExecutor.createAndObtainId(insertSql);
            return findMessageById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static Optional<Message> findMessageById(final int id) {
        String findByIdSql = String.format("SELECT * FROM %s WHERE id = %d;", TABLE_NAME, id);

        try {
            ResultSet rs = QueryExecutor.read(findByIdSql);
            Date msgDate = dateFormat.parse(rs.getString("DATE"));
            Optional<Ticket> referencedTicketOpt = Ticket.findTicketById(rs.getInt("TICKET_ID"));
            Optional<User> msgAuthorOpt = User.findById(rs.getInt("AUTHOR_ID"));

            if(referencedTicketOpt.isPresent() && msgAuthorOpt.isPresent()) {
                return Optional.of(new Message(id, msgDate, referencedTicketOpt.get(), msgAuthorOpt.get(), rs.getString("TEXT")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Error with parsing date!");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static ObservableList<Message> getMessagesForTicket(Ticket referencedTicket) {
        ObservableList<Message> result = FXCollections.observableArrayList();
        String sqlQuery = String.format("SELECT * FROM %s WHERE TICKET_ID = %d ORDER BY DATE;", TABLE_NAME, referencedTicket.id());

        try {
            ResultSet rs = QueryExecutor.read(sqlQuery);
            while (rs.next()) {
                Date msgDate = dateFormat.parse(rs.getString("DATE"));
                Optional<User> msgAuthorOpt = User.findById(rs.getInt("AUTHOR_ID"));

                if(msgAuthorOpt.isPresent()) {
                    result.add(new Message(rs.getInt("ID"), msgDate, referencedTicket, msgAuthorOpt.get(), rs.getString("TEXT")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Error with parsing date!");
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id &&
                date.equals(message.date) &&
                referencedTicket.equals(message.referencedTicket) &&
                author.equals(message.author) &&
                text.equals(message.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, referencedTicket, author, text);
    }
}
