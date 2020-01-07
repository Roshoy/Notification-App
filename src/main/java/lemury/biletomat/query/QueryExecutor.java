package lemury.biletomat.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.logging.Logger;

import lemury.biletomat.connection.ConnectionProvider;

public final class QueryExecutor {
    private static final Logger LOGGER = Logger.getGlobal();

    private QueryExecutor() {
        throw new UnsupportedOperationException();
    }

    static {
        try {
            LOGGER.info("Creating table departments");
            create("CREATE TABLE IF NOT EXISTS DEPARTMENTS (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "UNIQUE (name)" +
                    ");");

            LOGGER.info("Creating table users");
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

            LOGGER.info("Creating table ticket_structure");
            create("CREATE TABLE IF NOT EXISTS TICKET_STRUCTURE (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "department_id INTEGER NOT NULL, " +
                    "FOREIGN KEY(department_id) references DEPARTMENTS(id)" +
                    ");");

            LOGGER.info("Creating table ticket_structure_details");
            create("CREATE TABLE IF NOT EXISTS TICKET_STRUCTURE_DETAILS (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ticket_structure_id INTEGER NOT NULL, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "required BOOLEAN NOT NULL, " +
                    "type VARCHAR(50), " +
                    "FOREIGN KEY(ticket_structure_id) references TICKET_STRUCTURE(id)" +
                    ");");

            LOGGER.info("Creating table ticket_details_date");
            create("CREATE TABLE IF NOT EXISTS TICKET_DETAILS_DATE (" +
                    "field_id INTEGER NOT NULL, " +
                    "ticket_id INTEGER NOT NULL, " +
                    "value DATE, " +
                    "PRIMARY KEY(field_id, ticket_id), " +
                    "FOREIGN KEY(field_id) references TICKET_STRUCTURE_DETAILS(id), " +
                    "FOREIGN KEY(ticket_id) references TICKETS(id)" +
                    ");");

            LOGGER.info("Creating table ticket_details_int");
            create("CREATE TABLE IF NOT EXISTS TICKET_DETAILS_INT (" +
                    "field_id INTEGER NOT NULL, " +
                    "ticket_id INTEGER NOT NULL, " +
                    "value INTEGER, " +
                    "PRIMARY KEY(field_id, ticket_id), " +
                    "FOREIGN KEY(field_id) references TICKET_STRUCTURE_DETAILS(id), " +
                    "FOREIGN KEY(ticket_id) references TICKETS(id)" +
                    ");");

            LOGGER.info("Creating table ticket_details_string");
            create("CREATE TABLE IF NOT EXISTS TICKET_DETAILS_STRING (" +
                    "field_id INTEGER NOT NULL, " +
                    "ticket_id INTEGER NOT NULL, " +
                    "value VARCHAR(100), " +
                    "PRIMARY KEY(field_id, ticket_id), " +
                    "FOREIGN KEY(field_id) references TICKET_STRUCTURE_DETAILS(id), " +
                    "FOREIGN KEY(ticket_id) references TICKETS(id)" +
                    ");");

            LOGGER.info("Creating table Tickets");
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

            LOGGER.info("Creating table ITTickets");
            create("CREATE TABLE IF NOT EXISTS ITTICKETS (" +
                    "id INTEGER PRIMARY KEY, " +
                    "computer_no INTEGER NOT NULL," +
                    "FOREIGN KEY(id) references TICKETS(id)" +
                    ");");

            LOGGER.info("Creating table Messages");
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
            LOGGER.info("Error during create tables: " + e.getMessage());
            throw new RuntimeException("Cannot create tables");
        }
    }

    public static int createAndObtainId(final String insertSql) throws SQLException {
        try (final PreparedStatement statement = ConnectionProvider.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.execute();
            try (final ResultSet resultSet = statement.getGeneratedKeys()) {
                return readIdFromResultSet(resultSet);
            }
        }
    }

    public static int readIdFromResultSet(final ResultSet resultSet) throws SQLException {
        return resultSet.next() ? resultSet.getInt(1) : -1;
    }

    public static void create(final String insertSql) throws SQLException {
        try (final PreparedStatement statement = ConnectionProvider.getConnection().prepareStatement(insertSql)) {
            statement.execute();
        }
    }

    public static ResultSet read(final String sql) throws SQLException {
        System.out.println("read");
        final Statement statement = ConnectionProvider.getConnection().createStatement();
        final ResultSet resultSet = statement.executeQuery(sql);

        LOGGER.info(String.format("query: %s executed.", sql));
        return resultSet;
    }

    public static void delete(final String sql) throws SQLException {
        executeUpdate(sql);
    }

    private static void executeUpdate(final String... sql) throws SQLException {
        try (final Statement statement = ConnectionProvider.getConnection().createStatement()) {
            ConnectionProvider.getConnection().setAutoCommit(false);
            for (String s : sql) {
                statement.executeUpdate(s);
                LOGGER.info(String.format("query: %s executed.", s));
            }
            ConnectionProvider.getConnection().commit();
            ConnectionProvider.getConnection().setAutoCommit(true);
        }
    }
}
