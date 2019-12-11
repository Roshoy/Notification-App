package lemury.biletomat.model.users;

import lemury.biletomat.model.departments.Department;
import lemury.biletomat.query.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Administrator extends Coordinator {
    //department internal? or Base Department?
    //static final Department department = InternalDepartment;


    private Administrator(int id, String firstName, String lastName, String login, String password, Department department, String userType) {
        //Department department = Department.findById(1).get();
        super(id, firstName, lastName, login, password, department, userType);
    }

    public static Optional<Administrator> createAdministratorAccount(String login, String firstName, String lastName,
                                                                     String password){
        String insertSql = String.format("INSERT INTO %s (LOGIN, FIRST_NAME, LAST_NAME, PASSWORD, DEPARTMENT_ID, USER_TYPE) VALUES ('%s', '%s', '%s', '%s', %d, '%s');",
                TABLE_NAME, login, firstName, lastName, password, 1, "A");
        try {
            int id = QueryExecutor.createAndObtainId(insertSql);
            return findAdministratorById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static Optional<Administrator> findAdministratorById(final int id) {
        String findByIdSql = String.format("SELECT * FROM USERS WHERE id = %d AND USER_TYPE = 'A'", id);
        try {
            ResultSet rs = QueryExecutor.read(findByIdSql);
            return Optional.of(new Administrator(rs.getInt("id"), rs.getString("FIRST_NAME"),
                    rs.getString("LAST_NAME"), rs.getString("LOGIN"),
                    rs.getString("PASSWORD"), Department.findById(rs.getInt("DEPARTMENT_ID")).get(),
                    rs.getString("user_type")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
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
