package Model.Users;

import Model.Ticket.Ticket;

import java.util.List;

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
}

