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
import java.util.Optional;

public class Message {
    private final int id;
    private static final String TABLE_NAME = "MESSAGES";

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
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
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
            Date msgDate = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").parse(rs.getString("DATE"));
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
                Date msgDate = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").parse(rs.getString("DATE"));
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
}
