package lemury.biletomat;

import javafx.collections.ObservableList;
import lemury.biletomat.connection.ConnectionProvider;
import lemury.biletomat.model.departments.Department;
import lemury.biletomat.model.ticket.*;
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
import java.time.LocalDate;
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

    final Date date = Calendar.getInstance().getTime();
    final DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    final String dateString = dateFormat.format(date);
    final String exampleTicketTitle = "Zgloszenie";
    final String exampleTicketDescription = "Description...";

    final int exampleMessageId = 910;


    @BeforeClass
    public static void init() {
            ConnectionProvider.init("jdbc:sqlite:Database");
        }

    @Before
    public void setUp() throws SQLException {
        QueryExecutor.delete("DELETE FROM USERS;");
        QueryExecutor.delete("DELETE FROM DEPARTMENTS;");
        QueryExecutor.delete("DELETE FROM TICKETS;");
        QueryExecutor.delete("DELETE FROM MESSAGES;");
        QueryExecutor.delete("DELETE FROM TICKET_STRUCTURE;");
        QueryExecutor.delete("DELETE FROM TICKET_STRUCTURE_DETAILS;");
        QueryExecutor.delete("DELETE FROM TICKET_DETAILS_DATE;");
        QueryExecutor.delete("DELETE FROM TICKET_DETAILS_INT;");
        QueryExecutor.delete("DELETE FROM TICKET_DETAILS_STRING;");

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

        String insertMessageSql = String.format("INSERT INTO MESSAGES (ID, DATE, TICKET_ID, AUTHOR_ID, TEXT) VALUES (%d, '%s', %d, %d, '%s');",
                exampleMessageId, dateString, exampleTicketId, exampleUserId, "Pomocy");
        QueryExecutor.create(insertMessageSql);
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

    @Test
    public void checkSubmittedTicketsListTest() {
        Optional<User> optUser = User.findById(exampleUserId);
        Optional<Ticket> optTicket = Ticket.findTicketById(exampleTicketId);

        if(optTicket.isPresent() && optUser.isPresent()) {
            User user = optUser.get();
            Ticket ticket = optTicket.get();

            Assert.assertTrue(user.submittedTickets().size() >= 1);
            Assert.assertTrue(user.submittedTickets().contains(ticket));
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

    @Test
    public void checkCoordinatorsListTest() {
        Optional<Department> optDepartment = Department.findById(exampleDepartmentId);
        Optional<User> optCoordinator = Coordinator.findById(exampleCoordinatorId);

        if(optCoordinator.isPresent() && optDepartment.isPresent()) {
            Coordinator coordinator = (Coordinator) optCoordinator.get();
            Department department = optDepartment.get();

            Assert.assertTrue(department.coordinators().size() >= 1);
            Assert.assertTrue(department.coordinators().contains(coordinator));
        }
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

    @Test
    public void checkOwnedTicketsListTest() {
        Optional<Ticket> optTicket = Ticket.findTicketById(exampleTicketId);
        Optional<User> optCoordinator = Coordinator.findById(exampleCoordinatorId);

        if(optCoordinator.isPresent() && optTicket.isPresent()) {
            Ticket ticket = optTicket.get();
            Coordinator coordinator = (Coordinator) optCoordinator.get();

            Assert.assertTrue(coordinator.ownedTickets().size() >= 1);
            Assert.assertTrue(coordinator.ownedTickets().contains(ticket));
        }
    }

    // TICKET CLASS TESTS ----------------------------------------------------------------------------------------------
    @Test
    public void addNewTicketTest() {
        Assert.assertNotEquals(0, Ticket.create(exampleCoordinatorId, exampleUserId, "Nowy ticket", "opis..."));
    }

    @Test
    public void addIncorrectTicketTest() {
        Assert.assertEquals(0, Ticket.create(-9, -7, "aaa", "bbb"));
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

    @Test
    public void deleteTicketTest() {
        int newTicketId = Ticket.create(exampleCoordinatorId, exampleUserId, "testowy", "opisopis");

        Ticket.deleteTicketById(newTicketId);
        Optional<Ticket> optTicket = Ticket.findTicketById(newTicketId);
        Assert.assertEquals(Optional.empty(), optTicket);
    }

    @Test
    public void checkMessagesListTest() {
        Optional<Message> optMessage = Message.findMessageById(exampleMessageId);
        Optional<Ticket> optTicket = Ticket.findTicketById(exampleTicketId);

        if(optMessage.isPresent() && optTicket.isPresent()) {
            Message message = optMessage.get();
            Ticket ticket = optTicket.get();

            Assert.assertTrue(ticket.ticketMessages().size() >= 1);
            Assert.assertTrue(ticket.ticketMessages().contains(message));
        }
    }

    // MESSAGE CLASS TESTS ---------------------------------------------------------------------------------------------
    @Test
    public void createMessageTest() {
        Optional<Message> optMessage = Message.create(exampleTicketId, exampleUserId, "AAAAA");
        Assert.assertTrue(optMessage.isPresent());
    }

    @Test
    public void createMessageWithIncorrectAuthorIDTest() throws SQLException {
        Message.create(1, 2, "Test");
        String sql = "SELECT * FROM MESSAGES WHERE MESSAGES.ticket_id = 2";

        final Statement statement = ConnectionProvider.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Assert.assertFalse(resultSet.next());
    }

    @Test
    public void getMessageByIdTest() {
        Assert.assertTrue(Message.findMessageById(exampleMessageId).isPresent());
    }

    @Test
    public void getMessagesForTicketTest() {
        Optional<Ticket> referencedTicket = Ticket.findTicketById(exampleTicketId);
        Optional<Message> exampleMessage = Message.findMessageById(exampleMessageId);

        if(referencedTicket.isPresent() && exampleMessage.isPresent()) {
            List<Message> messageList = Message.getMessagesForTicket(referencedTicket.get());

            Assert.assertTrue(messageList.size() >= 1);
            Assert.assertTrue(messageList.contains(exampleMessage.get()));
        }
    }

    // TICKET STRUCTURE CLASS TEST -------------------------------------------------------------------------------------
    @Test
    public void createTicketStructureTest() {
        TicketStructure addedStructure = new TicketStructure("Test", exampleDepartmentId);
        int id = addedStructure.insertToDb();

        Assert.assertNotEquals(0, id);
        ObservableList<String> names = TicketStructure.getNames();

        Assert.assertTrue(names.contains("Test"));
    }

    @Test
    public void createTicketWithFieldsTest() throws SQLException {
        TicketStructure addedStructure = new TicketStructure("TestF", exampleDepartmentId);
        addedStructure.addDateField("data", true, LocalDate.now());
        addedStructure.addIntField("liczba", false, 12);
        addedStructure.addStringField("tekst", true, "AAAA");

        int id = addedStructure.insertToDb();

        String sqlQuery = String.format("SELECT COUNT(*) AS COUNTER FROM TICKET_STRUCTURE_DETAILS WHERE ticket_structure_id = %d;", id);
        ResultSet rs = QueryExecutor.read(sqlQuery);

        int count = rs.getInt("COUNTER");

        Assert.assertEquals(3, count);
    }

    @AfterClass
    public static void cleanUp() throws SQLException {
        ConnectionProvider.close();
    }
}
