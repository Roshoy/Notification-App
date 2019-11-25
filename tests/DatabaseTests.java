import Connection.ConnectionProvider;
import Model.Departments.Department;
import Model.Users.Administrator;
import Model.Users.Coordinator;
import Model.Users.User;
import Query.QueryExecutor;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.Optional;

public class DatabaseTests {
    @BeforeClass
    public static void init() {
        ConnectionProvider.init("jdbc:sqlite:data.db");
    }

    @Before
    public void setUp() throws SQLException {
        QueryExecutor.delete("DELETE FROM USERS;");
        QueryExecutor.delete("DELETE FROM DEPARTMENTS;");
    }


    @Test
    public void createUserTest() {
        Optional<User> first = Administrator.CreateUserAccount("mat", "Mateusz", "Kowal", "password");
        checkUser(first);
        Optional<User> second = Administrator.CreateUserAccount("mat2", "Mateusz1", "Kowa1l", "password");
        checkUser(second);
        Assert.assertNotEquals(first.get().id(), second.get().id());
    }

    private void checkUser(final Optional<User> user) {
        Assert.assertTrue(user.isPresent());
        user.ifPresent(u -> {
            Assert.assertTrue(u.id() > 0);
            Assert.assertNotNull(u.firstName());
            Assert.assertNotNull(u.lastName());
            Assert.assertNotNull(u.getPassword());
        });
    }

    @Test
    public void createDepartmentTest() {
        Optional<Department> first = Department.create("Dzial 1");
        checkDepartment(first);
        Optional<Department> second = Department.create("Dzial 2");
        checkDepartment(second);
        Assert.assertNotEquals(first.get().id(), second.get().id());
    }

    private void checkDepartment(final Optional<Department> department) {
        Assert.assertTrue(department.isPresent());
        department.ifPresent(d -> {
            Assert.assertTrue(d.id() > 0);
            Assert.assertNotNull(d.name());
        });
    }

    @Test
    public void createCoordinatorTest() {
        Optional<Department> coordDepartment1 = Department.create("Dzial testowy");
        Optional<Coordinator> firstCoordinator = Administrator.CreateCoordinatorAccount("coord1", "Michał",
                "Wiśniewski", "qwerty1234", coordDepartment1.get().id());
        checkCoordinator(firstCoordinator);

        Optional<Department> coordDepartment2 = Department.create("Dzial testowy 2");
        Optional<Coordinator> secondCoordinator = Administrator.CreateCoordinatorAccount("coord2", "Michał",
                "Kowalski", "qwerty1234", coordDepartment2.get().id());
        checkCoordinator(secondCoordinator);

        Assert.assertNotEquals(firstCoordinator.get().id(), secondCoordinator.get().id());
    }

    private void checkCoordinator(final Optional<Coordinator> coordinator) {
        Assert.assertTrue(coordinator.isPresent());
        coordinator.ifPresent(u -> {
            Assert.assertTrue(u.id() > 0);
            Assert.assertNotNull(u.firstName());
            Assert.assertNotNull(u.lastName());
            Assert.assertNotNull(u.getPassword());
            Assert.assertNotNull(u.getDepartment());
        });
    }

    @AfterClass
    public static void cleanUp() throws SQLException {
        ConnectionProvider.close();
    }
}