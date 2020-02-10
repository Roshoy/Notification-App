package lemury.biletomat;

import lemury.biletomat.connection.ConnectionProvider;
import lemury.biletomat.model.departments.Department;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DepartmentTests extends DatabaseSafeTests {
    @Test
    public void createDepartmentTest() {
        Optional<Department> optDepartment = Department.create("IT department");
        optDepartment.ifPresent(department -> Assert.assertEquals("IT department", department.name()));
    }

    @Test
    public void createTwoIdenticalDepartmentsTest() throws SQLException {
        String name = "Unique Dep";
        Department.create(name);
        Department.create(name);

        String sqlQuery = String.format("SELECT * FROM DEPARTMENTS WHERE NAME = '%s';", name);
        ResultSet rs = ConnectionProvider.getConnection().createStatement().executeQuery(sqlQuery);

        int rows = 0;
        while (rs.next()) rows++;

        Assert.assertEquals(1, rows);
    }

    @Test
    public void findDepartmentByNameTest() {
        int id = Department.findIdByName(exampleDepartmentName);
        Assert.assertEquals(exampleDepartmentId, id);
    }

    @Test
    public void checkCoordinatorsListTest() {
        Optional<Department> optDepartment = Department.findById(exampleDepartmentId);
        Optional<User> optCoordinator = Coordinator.findById(exampleCoordinatorId);

        if (optCoordinator.isPresent() && optDepartment.isPresent()) {
            Coordinator coordinator = (Coordinator) optCoordinator.get();
            Department department = optDepartment.get();
            department.updateCoordinators();

            Assert.assertTrue(department.coordinators().size() >= 1);
            Assert.assertTrue(department.coordinators().contains(coordinator));
        }
    }
}
