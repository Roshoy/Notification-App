package lemury.biletomat;

import lemury.biletomat.connection.ConnectionProvider;
import lemury.biletomat.model.departments.Department;
import lemury.biletomat.model.ticket.Message;
import lemury.biletomat.model.users.Administrator;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import lemury.biletomat.query.QueryExecutor;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Optional;

public class DatabaseTests {
    @BeforeClass
    public static void init() {
        ConnectionProvider.init("jdbc:sqlite:data.db");
    }

    @Before
    public void setUp() throws SQLException {
        QueryExecutor.delete("DELETE FROM USERS WHERE LOGIN = 'mat3' OR LOGIN = 'mat4';");
        QueryExecutor.delete("DELETE FROM USERS WHERE LOGIN = 'coord1' OR LOGIN = 'coord2';");
        QueryExecutor.delete("DELETE FROM DEPARTMENTS WHERE NAME = 'Dzial 1' OR NAME = 'Dzial 2';");
        QueryExecutor.delete("DELETE FROM DEPARTMENTS WHERE NAME = 'Dzial testowy 1' OR NAME = 'Dzial testowy 2';");
        //QueryExecutor.delete("DELETE FROM TICKETS;");
        QueryExecutor.delete("DELETE FROM MESSAGES;");
    }


    @Test
    public void createUserTest() {
        Optional<User> first = User.createUserAccount("mat3", "Mateusz", "Kowal", "password");
        checkUser(first);
        Optional<User> second = User.createUserAccount("mat4", "Mateusz1", "Kowa1l", "password");
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
        Optional<Coordinator> firstCoordinator = Administrator.createCoordinatorAccount("coord1", "Michał",
                "Wiśniewski", "qwerty1234", coordDepartment1.get().id());
        checkCoordinator(firstCoordinator);

        Optional<Department> coordDepartment2 = Department.create("Dzial testowy 2");
        Optional<Coordinator> secondCoordinator = Administrator.createCoordinatorAccount("coord2", "Michał",
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

    @Test
    public void addNewMessageTest() {
        // 15 - ticket komputer nie działa, 143 - koordynator (Adam Małysz)
        Optional<Message> msg = Message.create(15, 143, "Witam, tu Adam Małysz, czy próbował Pan podłączyć zasilanie do prądu? PZDR z fartem");
        Assert.assertNotEquals(Optional.empty(), msg);
    }

    /*
    @Test
    public void createTicketTest() {
        Optional<Department> department = Department.create("Administracja budynku");
        Optional<Coordinator> coordinator = Administrator.createCoordinatorAccount("pan_kaziu", "Kazimierz",
                "Kazimierowicz", "12345", department.get().id());
        Optional<User> user = User.createUserAccount("pani_wladzia", "Władysława",
                "Władysławowicz", "54321");

        Optional<ticket> ticket = ticket.create(coordinator.get().id(), user.get().id(), "Awaria w budynku", "Bo światło w piwnicy znowu nie działa, proszę naprawić");

        Assert.assertTrue(ticket.isPresent());
        ticket.ifPresent(t -> {
            Assert.assertTrue(t.id() > 0);
            Assert.assertNotNull(t.owner());
            Assert.assertEquals(t.owner(), coordinator.get());
            Assert.assertNotNull(t.submitter());
            Assert.assertEquals(t.submitter(), user.get());
            Assert.assertNotNull(t.title());
            Assert.assertNotNull(t.description());
        });
    }
*/
    @AfterClass
    public static void cleanUp() throws SQLException {
        ConnectionProvider.close();
    }
}