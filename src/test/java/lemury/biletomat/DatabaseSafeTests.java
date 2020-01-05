package lemury.biletomat;

import lemury.biletomat.connection.ConnectionProvider;
import lemury.biletomat.model.departments.Department;
import lemury.biletomat.model.ticket.ITTicket;
import lemury.biletomat.model.ticket.Message;
import lemury.biletomat.model.ticket.Ticket;
import lemury.biletomat.model.ticket.TicketStatus;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import lemury.biletomat.query.QueryExecutor;
import org.junit.*;

import javax.management.Query;
import javax.swing.text.html.Option;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static lemury.biletomat.query.QueryExecutor.create;


//zrobic testy dluzsze czy się tworzy ticket, czy potem koordyntor go widzi


public class DatabaseSafeTests {
    final int exampleUserId = 11;
    final String exampleUserLogin = "michal";
    final String exampleUserFirstName = "Michał";
    final String exampleUserLastName = "Michaiłowicz";
    final String exampleUserPassword = "qwerty";

    final String exampleDepartmentName = "Example department";
    final int exampleDepartmentId = 5;

    final int exampleCoordinatorId = 198;
    final String exampleCoordinatorLogin = "amalysz";
    final String exampleCoordinatorFirstName = "Adam";
    final String exampleCoordinatorLastName = "Małysz";
    final String exampleCoordinatorPassword = "qwerty456";

    final int exampleCoordinator2Id = 199;
    final String exampleCoordinator2Login = "mmichalski";
    final String exampleCoordinator2FirstName = "Michał";
    final String exampleCoordinator2LastName = "Michałowski";
    final String exampleCoordinator2Password = "qwerty997";

    final int exampleTicketId = 997;

    Date date = Calendar.getInstance().getTime();
    DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    String dateString = dateFormat.format(date);
    final String exampleTicketTitle = "Zgloszenie";
    final String exampleTicketDescription = "Description...";

    @BeforeClass
    public static void init() {
            ConnectionProvider.init("jdbc:sqlite:Database");
        }


    @BeforeClass
    public static void createTables(){
        try {
            create("CREATE TABLE IF NOT EXISTS DEPARTMENTS (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "UNIQUE (name)" +
                    ");");

            create("CREATE TABLE IF NOT EXISTS USERS (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "first_name VARCHAR(50) NOT NULL, " +
                    "last_name VARCHAR(50) NOT NULL, " +
                    "login VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(50) NOT NULL, " +
                    "department_id INTEGER, " +
                    "user_type CHAR(1) NOT NULL, " +
                    "FOREIGN KEY(department_id) references DEPARTMENTS(id) " +
                    ");");

            create("CREATE TABLE IF NOT EXISTS TICKETS (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "coordinator_id INTEGER NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "title VARCHAR(128) NOT NULL, " +
                    "description VARCHAR(500) NOT NULL, " +
                    "status CHAR(15) NOT NULL, " +
                    "release_notes VARCHAR(500), " +
                    "date DATE NOT NULL," +
                    "FOREIGN KEY(coordinator_id) references USERS(id), " +
                    "FOREIGN KEY(user_id) references USERS(id) " +
                    ");");

            create("CREATE TABLE IF NOT EXISTS ITTICKETS (" +
                    "id INTEGER PRIMARY KEY, " +
                    "computer_no INTEGER NOT NULL," +
                    "FOREIGN KEY(id) references TICKETS(id)" +
                    ");");

            create("CREATE TABLE IF NOT EXISTS MESSAGES (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "date DATE NOT NULL, " +
                    "ticket_id INT NOT NULL, " +
                    "author_id INT NOT NULL, " +
                    "text VARCHAR(500) NOT NULL, " +
                    "FOREIGN KEY(ticket_id) references TICKETS(id), " +
                    "FOREIGN KEY(author_id) references USERS(id)" +
                    ");");


        } catch (SQLException e) {
            //LOGGER.info("Error during create tables: " + e.getMessage());
            throw new RuntimeException("Cannot create tables");
        }
    }


    @Before
    public void setUp() throws SQLException {
        QueryExecutor.delete("DELETE FROM USERS;");
        QueryExecutor.delete("DELETE FROM DEPARTMENTS;");
        QueryExecutor.delete("DELETE FROM TICKETS;");
        QueryExecutor.delete("DELETE FROM MESSAGES;");

        String insertUserSql = String.format("INSERT INTO USERS (ID, LOGIN, FIRST_NAME, LAST_NAME, PASSWORD, USER_TYPE) VALUES (%d, '%s', '%s', '%s', '%s', '%s');",
                exampleUserId, exampleUserLogin, exampleUserFirstName, exampleUserLastName, exampleUserPassword, "U");
        QueryExecutor.create(insertUserSql);

        QueryExecutor.create(String.format("INSERT INTO DEPARTMENTS (ID, NAME) VALUES (%d, '%s');", exampleDepartmentId, exampleDepartmentName));

        String insertCoordinatorSql = String.format("INSERT INTO USERS (ID, LOGIN, FIRST_NAME, LAST_NAME, PASSWORD, DEPARTMENT_ID, USER_TYPE) VALUES (%d, '%s', '%s', '%s', '%s', %d, '%s');",
                exampleCoordinatorId, exampleCoordinatorLogin, exampleCoordinatorFirstName, exampleCoordinatorLastName, exampleCoordinatorPassword, exampleDepartmentId, "C");
        QueryExecutor.create(insertCoordinatorSql);

        insertCoordinatorSql = String.format("INSERT INTO USERS (ID, LOGIN, FIRST_NAME, LAST_NAME, PASSWORD, DEPARTMENT_ID, USER_TYPE) VALUES (%d, '%s', '%s', '%s', '%s', %d, '%s');",
                exampleCoordinator2Id, exampleCoordinator2Login, exampleCoordinator2FirstName, exampleCoordinator2LastName, exampleCoordinator2Password, exampleDepartmentId, "C");
        QueryExecutor.create(insertCoordinatorSql);

        String insertTicketSql = String.format("INSERT INTO TICKETS (ID, COORDINATOR_ID, USER_ID, TITLE, DESCRIPTION, STATUS, DATE) VALUES (%d, %d, %d, '%s', '%s', 'WAITING', '%s');",
                exampleTicketId, exampleCoordinatorId, exampleUserId, exampleTicketTitle, exampleTicketDescription, dateString);
        QueryExecutor.create(insertTicketSql);
        //QueryExecutor.delete("DELETE FROM USERS WHERE LOGIN = 'coord1' OR LOGIN = 'coord2';");
        //QueryExecutor.delete("DELETE FROM DEPARTMENTS WHERE NAME = 'Dzial 1' OR NAME = 'Dzial 2';");
        //QueryExecutor.delete("DELETE FROM DEPARTMENTS WHERE NAME = 'Dzial testowy 1' OR NAME = 'Dzial testowy 2';");
        //QueryExecutor.delete("DELETE FROM TICKETS;");
        //QueryExecutor.delete("DELETE FROM MESSAGES;");
    }

    // USER CLASS TESTS ------------------------------------------------------------------------------------------------
    @Test
    public void createUserWithIncompleteDataTest() {
        Optional<User> user = User.createUserAccount("", "Mateusz", "Kowal", "");
        Assert.assertEquals(Optional.empty(), user);
    }

    @Test
    public void createUserTest() {
        Optional<User> optUser = User.createUserAccount("mat", "Mateusz", "Kowalski", "qwerty");
        if(optUser.isPresent()) {
            User user = optUser.get();
            Assert.assertEquals("mat", user.getLogin());
            Assert.assertEquals("Mateusz", user.firstName());
            Assert.assertEquals("Kowalski", user.lastName());
            Assert.assertEquals("qwerty", user.getPassword());
        }
    }

    @Test
    public void createUsersWithSameLoginTest() throws SQLException {
        Optional<User> first = User.createUserAccount("mat3", "Mateusz", "Kowal", "password");
        checkUser(first);
        Optional<User> second = User.createUserAccount("mat3", "Mateusz1", "Kowa1l", "password1");

        String sql = "SELECT * FROM USERS WHERE login = 'mat3' AND password='password1'";
        final Statement statement = ConnectionProvider.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Assert.assertFalse(resultSet.next());
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
    public void findUserByLoginTest() {
        Optional<User> optUser = User.findByLogin(exampleUserLogin);
        if(optUser.isPresent()) {
            User user = optUser.get();
            Assert.assertEquals(exampleUserLogin, user.getLogin());
            Assert.assertEquals(exampleUserFirstName, user.firstName());
            Assert.assertEquals(exampleUserLastName, user.lastName());
            Assert.assertEquals(exampleUserPassword, user.getPassword());
        }
    }

    @Test
    public void findUserByLoginAndPasswordTest() {
        Optional<User> optUser = User.findByLogin(exampleUserLogin, exampleUserPassword);
        if(optUser.isPresent()) {
            User user = optUser.get();
            Assert.assertEquals(exampleUserLogin, user.getLogin());
            Assert.assertEquals(exampleUserFirstName, user.firstName());
            Assert.assertEquals(exampleUserLastName, user.lastName());
            Assert.assertEquals(exampleUserPassword, user.getPassword());
        }
    }

    @Test
    public void deleteUserTest() {
        String login = "iwan123";
        String password = "321nawi";
        String firstName = "Iwan";
        String lastName = "Groźny";

        Optional<User> optUser = User.createUserAccount(login, password, firstName, lastName);
        if(optUser.isPresent()) {
            User.deleteUserById(optUser.get().id());

            Assert.assertEquals(Optional.empty(), User.findById(optUser.get().id()));
        }
    }

    // DEPARTMENT CLASS TESTS ------------------------------------------------------------------------------------------
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
        while(rs.next()) rows++;

        Assert.assertEquals(1, rows);
    }

    @Test
    public void findDepartmentByNameTest() {
        int id = Department.findIdByName(exampleDepartmentName);
        Assert.assertEquals(exampleDepartmentId, id);
    }

    // COORDINATOR CLASS TESTS -----------------------------------------------------------------------------------------
    @Test
    public void createCoordinatorWithImproperDepartmentIdTest() {
        Optional<Coordinator> optCoordinator = Coordinator.createCoordinatorAccount("aaa", "bbb", "ccc", "ddd", -1);
        Assert.assertEquals(Optional.empty(), optCoordinator);
    }

    @Test
    public void coordinatorsListTest() {
        List<Coordinator> coordinatorList = Coordinator.getCoordinatorsList();
        Assert.assertTrue(coordinatorList.size() >= 2);

        Optional<Coordinator> optCoordinator1 = Coordinator.findCoordinatorById(exampleCoordinatorId);
        Optional<Coordinator> optCoordinator2 = Coordinator.findCoordinatorById(exampleCoordinator2Id);

        if(optCoordinator1.isPresent() && optCoordinator2.isPresent()) {
            Coordinator coordinator1 = optCoordinator1.get();
            Coordinator coordinator2 = optCoordinator2.get();

            Assert.assertTrue(coordinatorList.contains(coordinator1));
            Assert.assertTrue(coordinatorList.contains(coordinator2));
        }
    }

    @Test
    public void findCoordinatorsByDepartmentIdTest() {
        List<Coordinator> coordinatorList = Coordinator.findCoordinatorsByDepartmentId(exampleDepartmentId);
        Assert.assertEquals(2, coordinatorList.size());

        Optional<Coordinator> optCoordinator1 = Coordinator.findCoordinatorById(exampleCoordinatorId);
        Optional<Coordinator> optCoordinator2 = Coordinator.findCoordinatorById(exampleCoordinator2Id);

        if(optCoordinator1.isPresent() && optCoordinator2.isPresent()) {
            Coordinator coordinator1 = optCoordinator1.get();
            Coordinator coordinator2 = optCoordinator2.get();

            Assert.assertTrue(coordinatorList.contains(coordinator1));
            Assert.assertTrue(coordinatorList.contains(coordinator2));
        }
    }

    // TICKET CLASS TESTS ----------------------------------------------------------------------------------------------
    @Test
    public void addNewTicketTest() {
        Assert.assertNotEquals(0, Ticket.create(exampleCoordinatorId, exampleUserId, "Nowy ticket", "opis..."));
    }

    @Test
    public void findTicketByIdTest() {
        Optional<Ticket> optTicket = Ticket.findTicketById(exampleTicketId);

        if(optTicket.isPresent()) {
            Ticket ticket = optTicket.get();

            Assert.assertEquals(exampleCoordinatorId, ticket.owner().id());
            Assert.assertEquals(exampleUserId, ticket.submitter().id());
            Assert.assertEquals(exampleTicketTitle, ticket.title());
            Assert.assertEquals(exampleTicketDescription, ticket.description());
        }
    }

    @Test
    public void getTicketsListTest() {
        Optional<Ticket> optTicket = Ticket.findTicketById(exampleTicketId);
        Optional<User> optUser = User.findById(exampleUserId);

        if(optTicket.isPresent() && optUser.isPresent()) {
            List<Ticket> ticketList = Ticket.getTicketsList(optUser.get());

            Assert.assertTrue(ticketList.size() >= 1);
            Assert.assertTrue(ticketList.contains(optTicket.get()));
        }
    }

    @Test
    public void getCoordinatorTicketsTest() {
        Optional<User> optCoordinator = Coordinator.findById(exampleCoordinatorId);
        Optional<Ticket> optTicket = Ticket.findTicketById(exampleTicketId);

        if(optTicket.isPresent() && optCoordinator.isPresent()) {
            List<Ticket> ticketList = Ticket.getTicketsListOfCoordinator((Coordinator) optCoordinator.get());

            Assert.assertTrue(ticketList.size() >= 1);
            Assert.assertTrue(ticketList.contains(optTicket.get()));
        }
    }
    @Test
    public void filterTest() {
        Optional<Ticket> optTicket = Ticket.findTicketById(exampleTicketId);
        Optional<User> optUser = User.findById(exampleUserId);

        if(optTicket.isPresent() && optUser.isPresent()) {
            List<Ticket> filteredList1 = Ticket.filterTicketList(optUser.get(), true, false, false);
            Assert.assertTrue(filteredList1.contains(optTicket.get()));

            optTicket.get().setTicketStatus(TicketStatus.IN_PROGRESS);
            List<Ticket> filteredList2 = Ticket.filterTicketList(optUser.get(), false, true, true);
            Assert.assertTrue(filteredList2.contains(optTicket.get()));
        }
    }

    //we should not be able to create it ticket without base ticket in database
    @Test
    public void createITTicketWithoutTicketTest() throws SQLException {
        ITTicket.create(2,12);
        String sql = "SELECT * FROM ITTICKETS WHERE id = 2;";
        final Statement statement = ConnectionProvider.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Assert.assertFalse(resultSet.next());
    }

    // MESSAGE CLASS TESTS ---------------------------------------------------------------------------------------------
    @Test
    public void createMessageWithUncorrectAuthorIDTest() throws SQLException {
        Message.create(1, 2, "Test");
        String sql = "SELECT * FROM MESSAGES WHERE MESSAGES.ticket_id = 2";

        final Statement statement = ConnectionProvider.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Assert.assertFalse(resultSet.next());
    }



    @AfterClass
    public static void cleanUp() throws SQLException {
        ConnectionProvider.close();
    }





}
