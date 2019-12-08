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

    protected Coordinator(int id, String firstName, String lastName, String login, String password, Department department, String userType) {
        super(id, firstName, lastName, login, password, userType);
        this.department = department;
    }


    public Department getDepartment() {
        return department;
    }

    // maybe it could be done better with different type in Optional
    public static Optional<Coordinator> createCoordinatorAccount(String login, String firstName, String lastName, String password, int departmentId){
        String insertSql = String.format("INSERT INTO %s (LOGIN, FIRST_NAME, LAST_NAME, PASSWORD, DEPARTMENT_ID, USER_TYPE) VALUES ('%s', '%s', '%s', '%s', %d, '%s');",
                TABLE_NAME, login, firstName, lastName, password, departmentId, "C");
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
            return Optional.of(new Coordinator(rs.getInt("id"), rs.getString("FIRST_NAME"),
                    rs.getString("LAST_NAME"), rs.getString("LOGIN"),
                    rs.getString("PASSWORD"), Department.findById(rs.getInt("DEPARTMENT_ID")).get(),
                    rs.getString("user_type")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static ObservableList<Coordinator> getCoordinatorsList() {
        ObservableList<Coordinator> result = FXCollections.observableArrayList();
        String sqlQuery = String.format("SELECT * FROM %s WHERE USER_TYPE = 'C';", TABLE_NAME);

        try {
            ResultSet rs = QueryExecutor.read(sqlQuery);
            while(rs.next()) {
                Coordinator currentCoordinator = new Coordinator(rs.getInt("id"), rs.getString("FIRST_NAME"),
                        rs.getString("LAST_NAME"), rs.getString("LOGIN"),
                        rs.getString("PASSWORD"),
                        Department.findById(rs.getInt("DEPARTMENT_ID")).get(),
                        rs.getString("user_type"));
                result.add(currentCoordinator);
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
                    rs.getString("password"), Department.findById(rs.getInt("department_id")).get(),
                    rs.getString("user_type")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public static int findCoordinatorByDepartmentNo(int departmentNo) throws SQLException {
        String findCoordinator = String.format("SELECT * FROM USERS WHERE user_type = 'C' and department_id = '%d'", departmentNo);
        ResultSet resultSet = QueryExecutor.read(findCoordinator);
        //String numOfCoordinatorsSQL = String.format("SELECT COUNT(*) FROM USERS WHERE user_type = 'C'");
        int result = QueryExecutor.readIdFromResultSet(resultSet);
        return result;


    }


}
