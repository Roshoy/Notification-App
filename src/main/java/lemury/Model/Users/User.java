package lemury.Model.Users;

import javafx.collections.FXCollections;
import lemury.Model.Departments.Department;
import lemury.Model.Ticket.Ticket;
import lemury.Query.QueryExecutor;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class User {
    public static final String TABLE_NAME = "USERS";

    protected final int id;
    private final String firstName;
    private final String lastName;
    private final String login;
    private String password;

    private List<Ticket> submittedTickets;


    public User(int id, String firstName, String lastName, String login, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.submittedTickets = null;

    }

        public int id() {
        return id;
    }

        public String getLogin(){return  login;}

        public String firstName() {
        return firstName;
    }

        public String lastName() {
        return lastName;
    }

        public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(login, user.login) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, login, password);
    }

    public static ObservableList<Ticket> getTicketsList(int userID) {
        ObservableList<Ticket> result = FXCollections.observableArrayList();
        String sqlQuery = String.format("SELECT * FROM %s WHERE user_id = %d;", Ticket.TABLE_NAME, userID);

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


    public static Optional<User> findById(final int id) {
        String findByIdSql = String.format("SELECT * FROM %s WHERE id = %d", User.TABLE_NAME, id);
        try {
            ResultSet rs = QueryExecutor.read(findByIdSql);
            return Optional.of(new User(rs.getInt("id"), rs.getString("first_name"),
                    rs.getString("last_name"), rs.getString("login"),
                    rs.getString("password")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}

