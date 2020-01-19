package lemury.biletomat;

import lemury.biletomat.connection.ConnectionProvider;
import lemury.biletomat.query.QueryExecutor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatabaseSafeTests {
    final int exampleUserId = 11;
    final String exampleUserLogin = "michal";
    final String exampleUserFirstName = "Michał";
    final String exampleUserLastName = "Michaiłowicz";
    final String exampleUserPassword = "qwerty";

    final String exampleDepartmentName = "Example department";
    final int exampleDepartmentId = 5;

    final int exampleCoordinatorId = 198;

    final int exampleCoordinator2Id = 199;

    final int exampleTicketId = 997;

    private final Date date = Calendar.getInstance().getTime();
    private final DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    final String dateString = dateFormat.format(date);
    final String exampleTicketTitle = "Zgloszenie";
    final String exampleTicketDescription = "Description...";

    final int exampleMessageId = 910;
    final int exampleTicketStructureId = 6;

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

        String exampleCoordinatorLogin = "amalysz";
        String exampleCoordinatorFirstName = "Adam";
        String exampleCoordinatorLastName = "Małysz";
        String exampleCoordinatorPassword = "qwerty456";
        String insertCoordinatorSql = String.format("INSERT INTO USERS (ID, LOGIN, FIRST_NAME, LAST_NAME, PASSWORD, DEPARTMENT_ID, USER_TYPE) VALUES (%d, '%s', '%s', '%s', '%s', %d, '%s');",
                exampleCoordinatorId, exampleCoordinatorLogin, exampleCoordinatorFirstName, exampleCoordinatorLastName, exampleCoordinatorPassword, exampleDepartmentId, "C");
        QueryExecutor.create(insertCoordinatorSql);
    }

    @AfterClass
    public static void cleanUp() throws SQLException {
        ConnectionProvider.close();
    }
}
