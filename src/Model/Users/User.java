package Model.Users;

import Model.Ticket.Ticket;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Objects;

public class User {
    public static final String TABLE_NAME = "USERS";

    private final int id;
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
}

