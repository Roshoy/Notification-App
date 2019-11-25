package Model.Users;

import Model.Departments.Department;
import Query.QueryExecutor;

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
    public static Optional<User> CreateUserAccount(String login, String firstName, String lastName, String password){
        String insertSql = String.format("INSERT INTO %s (LOGIN, FIRST_NAME, LAST_NAME, PASSWORD) VALUES ('%s', '%s', '%s', '%s');", TABLE_NAME, login,firstName
        , lastName, password);

        try {
            int id = QueryExecutor.createAndObtainId(insertSql);
            return findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return Optional.empty();

    }

    public static Optional<User> findById(final int id) {
        String findByIdSql = String.format("SELECT * FROM USERS WHERE id = %d", id);
        try {
            ResultSet rs = QueryExecutor.read(findByIdSql);
            return Optional.of(new User(rs.getInt("id"),rs.getString("LOGIN"), rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"),rs.getString("PASSWORD")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void CreateCoordinatorAccount(String login /*...*/){

    }
}
