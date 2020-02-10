package lemury.biletomat;

import lemury.biletomat.connection.ConnectionProvider;
import lemury.biletomat.model.ticket.Message;
import lemury.biletomat.model.ticket.Ticket;
import lemury.biletomat.query.QueryExecutor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class MessageTests extends DatabaseSafeTests {
    @Before
    @Override
    public void setUp() throws SQLException {
        super.setUp();
        String insertTicketSql = String.format("INSERT INTO TICKETS (ID, COORDINATOR_ID, USER_ID, TITLE, DESCRIPTION, STATUS, DATE) VALUES (%d, %d, %d, '%s', '%s', 'WAITING', '%s');",
                exampleTicketId, exampleCoordinatorId, exampleUserId, exampleTicketTitle, exampleTicketDescription, dateString);
        QueryExecutor.create(insertTicketSql);

        String insertMessageSql = String.format("INSERT INTO MESSAGES (ID, DATE, TICKET_ID, AUTHOR_ID, TEXT) VALUES (%d, '%s', %d, %d, '%s');",
                exampleMessageId, dateString, exampleTicketId, exampleUserId, "Pomocy");
        QueryExecutor.create(insertMessageSql);
    }

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

        if (referencedTicket.isPresent() && exampleMessage.isPresent()) {
            List<Message> messageList = Message.getMessagesForTicket(referencedTicket.get());

            Assert.assertTrue(messageList.size() >= 1);
            Assert.assertTrue(messageList.contains(exampleMessage.get()));
        }
    }
}
