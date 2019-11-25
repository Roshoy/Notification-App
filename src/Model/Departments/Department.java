package Model.Departments;

import Model.Ticket.Ticket;
import Model.Users.Coordinator;
import Query.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Department {
    private final int id;
    public static final String TABLE_NAME = "DEPARTMENTS";

    private String Name;
    private Set<Coordinator> coordinators;

    public Department(int id, String name) {
        this.id = id;
        this.Name = name;
        this.coordinators = new HashSet<>();
    }

    public int id() {
        return id;
    }

    public String name() {
        return Name;
    }

    /*
    public Ticket CreateNewTicket(){ //Is it SRP friendly?
        return new Ticket();
    }
    */

    public static Optional<Department> create(String name) {
        String insertSql = String.format("INSERT INTO %s (name) VALUES ('%s');", TABLE_NAME, name);

        try {
            int id = QueryExecutor.createAndObtainId(insertSql);
            return findById(id);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static Optional<Department> findById(final int id) {
        String findByIdSql = String.format("SELECT * FROM %s WHERE id = %d", TABLE_NAME, id);
        try {
            ResultSet rs = QueryExecutor.read(findByIdSql);
            return Optional.of(new Department(rs.getInt("id"), rs.getString("name")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
