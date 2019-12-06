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


    protected User(int id, String firstName, String lastName, String login, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.submittedTickets = null;

    }

    public static Optional<User> createUserAccount(String login, String firstName, String lastName, String password){
        String insertSql = String.format("INSERT INTO %s (LOGIN, FIRST_NAME, LAST_NAME, PASSWORD, USER_TYPE) VALUES ('%s', '%s', '%s', '%s', '%s');", TABLE_NAME, login,firstName
                , lastName, password, "U");

        try {
            int id = QueryExecutor.createAndObtainId(insertSql);
            return findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
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

    public static void deleteUserById(final int id) {
        String sqlQuery = String.format("DELETE FROM %s WHERE id = %d;", TABLE_NAME, id);

        try {
            QueryExecutor.delete(sqlQuery);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }


    public static ObservableList<User> getUsersList() {
        ObservableList<User> result = FXCollections.observableArrayList();
        String sqlQuery = String.format("SELECT * FROM %s WHERE USER_TYPE = 'U';", TABLE_NAME);

        try {
            ResultSet rs = QueryExecutor.read(sqlQuery);
            while(rs.next()) {
                User currentUser = new User(rs.getInt("id"), rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"), rs.getString("LOGIN"), rs.getString("PASSWORD"));
                result.add(currentUser);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}

