package lemury.biletomat;

import lemury.biletomat.connection.ConnectionProvider;
import lemury.biletomat.model.ticket.ITTicket;
import lemury.biletomat.model.ticket.Message;
import lemury.biletomat.model.ticket.Ticket;
import lemury.biletomat.model.users.User;
import lemury.biletomat.query.QueryExecutor;
import org.junit.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static lemury.biletomat.query.QueryExecutor.create;

public class DatabaseSafeTests {
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
        //QueryExecutor.delete("DELETE FROM USERS WHERE LOGIN = 'coord1' OR LOGIN = 'coord2';");
        //QueryExecutor.delete("DELETE FROM DEPARTMENTS WHERE NAME = 'Dzial 1' OR NAME = 'Dzial 2';");
        //QueryExecutor.delete("DELETE FROM DEPARTMENTS WHERE NAME = 'Dzial testowy 1' OR NAME = 'Dzial testowy 2';");
        //QueryExecutor.delete("DELETE FROM TICKETS;");
        //QueryExecutor.delete("DELETE FROM MESSAGES;");
    }

    @Test
    public void createUsersWithSameLoginTest() throws SQLException {
        Optional<User> first = User.createUserAccount("mat3", "Mateusz", "Kowal", "password");
        checkUser(first);
        Optional<User> second = User.createUserAccount("mat3", "Mateusz1", "Kowa1l", "password1");

        String sql = "SELECT * FROM USERS WHERE login = 'mat3' AND password='password1'";
        final Statement statement = ConnectionProvider.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Assert.assertEquals(false, resultSet.next());
    }


//we should not be able to create it ticket without base ticket in database
    @Test
    public void createITTicketWithoutTicketTest() throws SQLException {
        ITTicket.create(2,12);
        String sql = "SELECT * FROM ITTICKETS WHERE id = 2";
        final Statement statement = ConnectionProvider.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Assert.assertEquals(false, resultSet.next());
    }


    @Test
    public void createMessageWithUncorrectAuthorIDTest() throws SQLException {
        Message.create(1, 2, "Test");
        String sql = "SELECT * FROM MESSAGES WHERE MESSAGES.ticket_id = 2";

        final Statement statement = ConnectionProvider.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Assert.assertEquals(false, resultSet.next());
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

    @AfterClass
    public static void cleanUp() throws SQLException {
        ConnectionProvider.close();
    }





}
