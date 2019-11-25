import Connection.ConnectionProvider;
import Model.Users.Administrator;
import Model.Users.User;
import Query.QueryExecutor;
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
        QueryExecutor.delete("DELETE FROM USERS;");
    }


    @Test
    public void createUserTest() {
        Optional<User> first = Administrator.CreateUserAccount("mat", "Mateusz", "Kowal", "password");
        checkUser(first);
        Optional<User> second = Administrator.CreateUserAccount("mat2", "Mateusz1", "Kowa1l", "password");
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


    @AfterClass
    public static void cleanUp() throws SQLException {
        ConnectionProvider.close();
    }
}