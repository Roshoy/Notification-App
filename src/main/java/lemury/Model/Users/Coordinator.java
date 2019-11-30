package lemury.Model.Users;

import lemury.Model.Departments.Department;
import lemury.Model.Ticket.Ticket;
import lemury.Query.QueryExecutor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class Coordinator extends User {

    private Department department;
    private List<Ticket> tickets;

    public Coordinator(int id, String firstName, String lastName, String login, String password, Department department) {
        super(id, firstName, lastName, login, password);
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }

    public void EditTicket(Ticket ticket){
        //change status of ticket
    }

    public static ObservableList<Ticket> getUsersList(int coordID) {
        ObservableList<Ticket> result = FXCollections.observableArrayList();
        String sqlQuery = String.format("SELECT * FROM %s WHERE coordinator_id = %d;", Ticket.TABLE_NAME, coordID);

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
        String findByIdSql = String.format("SELECT * FROM %s WHERE id = %d", TABLE_NAME, id);
        try {
            ResultSet rs = QueryExecutor.read(findByIdSql);
            return Optional.of(new Coordinator(rs.getInt("id"), rs.getString("first_name"),
                    rs.getString("last_name"), rs.getString("login"),
                    rs.getString("password"), Department.findById(rs.getInt("department_id")).get()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
