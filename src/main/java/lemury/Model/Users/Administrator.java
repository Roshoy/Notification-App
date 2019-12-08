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


    private Administrator(int id, String firstName, String lastName, String login, String password,
                          Department department, String userType) {
        super(id, firstName, lastName, login, password, department, userType);
    }


//Optional<Student>
/*
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
*/
}
