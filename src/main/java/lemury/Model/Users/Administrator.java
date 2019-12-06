package lemury.Model.Users;

import lemury.Model.Departments.Department;
import lemury.Query.QueryExecutor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Administrator extends Coordinator {
    //department internal? or Base Department?
    //static final Department department = InternalDepartment;


    public Administrator(int id, String firstName, String lastName, String login, String password, Department department) {
        super(id, firstName, lastName, login, password, department);
    }


//Optional<Student>

    public static Optional<User> findUserById(final int id) {
        String findByIdSql = String.format("SELECT * FROM USERS WHERE id = %d AND USER_TYPE = 'U'", id);
        try {
            ResultSet rs = QueryExecutor.read(findByIdSql);
            return Optional.of(new User(rs.getInt("id"),rs.getString("LOGIN"), rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"),rs.getString("PASSWORD")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    // maybe it could be done better with different type in Optional
    public static Optional<Coordinator> CreateCoordinatorAccount(String login, String firstName, String lastName, String password, int departmentId){
        String insertSql = String.format("INSERT INTO %s (LOGIN, FIRST_NAME, LAST_NAME, PASSWORD, DEPARTMENT_ID, USER_TYPE) VALUES ('%s', '%s', '%s', '%s', %d, '%s');", TABLE_NAME, login,firstName
                , lastName, password, departmentId, "C");

        try {
            int id = QueryExecutor.createAndObtainId(insertSql);
            return findCoordinatorById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static Optional<Coordinator> findCoordinatorById(final int id) {
        String findByIdSql = String.format("SELECT * FROM USERS WHERE id = %d AND USER_TYPE = 'C'", id);
        try {
            ResultSet rs = QueryExecutor.read(findByIdSql);

            return Optional.of(new Coordinator(rs.getInt("id"), rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"),
                    rs.getString("LOGIN"), rs.getString("PASSWORD"), Department.findById(rs.getInt("DEPARTMENT_ID")).get()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
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

    public static ObservableList<Coordinator> getCoordinatorsList() {
        ObservableList<Coordinator> result = FXCollections.observableArrayList();
        String sqlQuery = String.format("SELECT * FROM %s WHERE USER_TYPE = 'C';", TABLE_NAME);

        try {
            ResultSet rs = QueryExecutor.read(sqlQuery);
            while(rs.next()) {
                Coordinator currentCoordinator = new Coordinator(rs.getInt("id"), rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"),
                        rs.getString("LOGIN"), rs.getString("PASSWORD"), Department.findById(rs.getInt("DEPARTMENT_ID")).get());
                result.add(currentCoordinator);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void deleteUserById(final int id) {
        String sqlQuery = String.format("DELETE FROM %s WHERE id = %d;", TABLE_NAME, id);

        try {
            QueryExecutor.delete(sqlQuery);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
