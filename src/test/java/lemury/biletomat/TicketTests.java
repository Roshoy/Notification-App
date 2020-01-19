package lemury.biletomat;

import lemury.biletomat.model.ticket.Message;
import lemury.biletomat.model.ticket.Ticket;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

public class TicketTests extends DatabaseSafeTests {
    @Test
    public void addNewTicketTest() {
        Assert.assertNotEquals(0, Ticket.create(exampleCoordinatorId, exampleUserId, "Nowy ticket", "opis...", exampleTicketStructureId));
    }

    @Test
    public void addIncorrectTicketTest() {
        Assert.assertEquals(0, Ticket.create(-9, -7, "aaa", "bbb", exampleTicketStructureId));
    }

    @Test
    public void findTicketByIdTest() {
        Optional<Ticket> optTicket = Ticket.findTicketById(exampleTicketId);

        if (optTicket.isPresent()) {
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

        if (optTicket.isPresent() && optUser.isPresent()) {
            List<Ticket> ticketList = Ticket.getTicketsList(optUser.get());

            Assert.assertTrue(ticketList.size() >= 1);
            Assert.assertTrue(ticketList.contains(optTicket.get()));
        }
    }

    @Test
    public void getCoordinatorTicketsTest() {
        Optional<User> optCoordinator = Coordinator.findById(exampleCoordinatorId);
        Optional<Ticket> optTicket = Ticket.findTicketById(exampleTicketId);

        if (optTicket.isPresent() && optCoordinator.isPresent()) {
            List<Ticket> ticketList = Ticket.getTicketsListOfCoordinator((Coordinator) optCoordinator.get());

            Assert.assertTrue(ticketList.size() >= 1);
            Assert.assertTrue(ticketList.contains(optTicket.get()));
        }
    }

    @Test
    public void deleteTicketTest() {
        int newTicketId = Ticket.create(exampleCoordinatorId, exampleUserId, "testowy", "opisopis", exampleTicketStructureId);

        Ticket.deleteTicketById(newTicketId);
        Optional<Ticket> optTicket = Ticket.findTicketById(newTicketId);
        Assert.assertEquals(Optional.empty(), optTicket);
    }

    @Test
    public void checkMessagesListTest() {
        Optional<Message> optMessage = Message.findMessageById(exampleMessageId);
        Optional<Ticket> optTicket = Ticket.findTicketById(exampleTicketId);

        if (optMessage.isPresent() && optTicket.isPresent()) {
            Message message = optMessage.get();
            Ticket ticket = optTicket.get();

            Assert.assertTrue(ticket.ticketMessages().size() >= 1);
            Assert.assertTrue(ticket.ticketMessages().contains(message));
        }
    }
}
