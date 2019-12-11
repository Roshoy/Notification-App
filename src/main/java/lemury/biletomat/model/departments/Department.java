package lemury.biletomat.model.departments;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.query.QueryExecutor;

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
    public ticket CreateNewTicket(){ //Is it SRP friendly?
        return new ticket();
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

    public static ObservableList<String> getNames(){
        String getSql = String.format("SELECT name FROM %s",TABLE_NAME);
        ObservableList<String> result = FXCollections.observableArrayList();

        try {
            ResultSet rs = QueryExecutor.read(getSql);
            while (rs.next()){
                String departmentName = rs.getString("name");
                result.add(departmentName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
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

    public static int findIdByName(final String name) {
        String findByIdSql = String.format("SELECT * FROM %s WHERE name = '%s'", TABLE_NAME, name);
        try {
            ResultSet rs = QueryExecutor.read(findByIdSql);
            return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
