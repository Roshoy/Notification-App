package lemury.biletomat;

import lemury.biletomat.model.ticket.Ticket;
import lemury.biletomat.model.users.Coordinator;
import lemury.biletomat.model.users.User;
import lemury.biletomat.query.QueryExecutor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CoordinatorTests extends DatabaseSafeTests {
    @Before
    @Override
    public void setUp() throws SQLException {
        super.setUp();
        String exampleCoordinator2Login = "mmichalski";
        String exampleCoordinator2FirstName = "Michał";
        String exampleCoordinator2LastName = "Michałowski";
        String exampleCoordinator2Password = "qwerty997";
        String insertCoordinatorSql = String.format("INSERT INTO USERS (ID, LOGIN, FIRST_NAME, LAST_NAME, PASSWORD, DEPARTMENT_ID, USER_TYPE) VALUES (%d, '%s', '%s', '%s', '%s', %d, '%s');",
                exampleCoordinator2Id, exampleCoordinator2Login, exampleCoordinator2FirstName, exampleCoordinator2LastName, exampleCoordinator2Password, exampleDepartmentId, "C");
        QueryExecutor.create(insertCoordinatorSql);
    }

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

        if (optCoordinator1.isPresent() && optCoordinator2.isPresent()) {
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

        if (optCoordinator1.isPresent() && optCoordinator2.isPresent()) {
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

        if (optCoordinator.isPresent() && optTicket.isPresent()) {
            Ticket ticket = optTicket.get();
            Coordinator coordinator = (Coordinator) optCoordinator.get();
            coordinator.updateTickets();

            Assert.assertTrue(coordinator.getOwnedTickets().size() >= 1);
            Assert.assertTrue(coordinator.getOwnedTickets().contains(ticket));
        }
    }
}
