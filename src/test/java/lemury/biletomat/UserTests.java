package lemury.biletomat;

import lemury.biletomat.connection.ConnectionProvider;
import lemury.biletomat.model.ticket.Ticket;
import lemury.biletomat.model.users.User;
import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UserTests extends DatabaseSafeTests {
    @Test
    public void createUserWithIncompleteDataTest() {
        Optional<User> user = User.createUserAccount("", "Mateusz", "Kowal", "");
        Assert.assertEquals(Optional.empty(), user);
    }

    @Test
    public void createUserTest() {
        Optional<User> optUser = User.createUserAccount("mat", "Mateusz", "Kowalski", "qwerty");
        if (optUser.isPresent()) {
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
        if (optUser.isPresent()) {
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
        if (optUser.isPresent()) {
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
        if (optUser.isPresent()) {
            User.deleteUserById(optUser.get().id());

            Assert.assertEquals(Optional.empty(), User.findById(optUser.get().id()));
        }
    }

    @Test
    public void checkSubmittedTicketsListTest() {
        Optional<User> optUser = User.findById(exampleUserId);

        Optional<Ticket> optTicket = Ticket.findTicketById(exampleTicketId);

        if (optTicket.isPresent() && optUser.isPresent()) {
            User user = optUser.get();
            user.updateTickets();
            Ticket ticket = optTicket.get();

            Assert.assertTrue(user.getSubmittedTickets().size() >= 1);
            Assert.assertTrue(user.getSubmittedTickets().contains(ticket));
        }
    }
}
