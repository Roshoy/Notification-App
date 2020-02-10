package lemury.biletomat;

import javafx.collections.ObservableList;
import lemury.biletomat.model.ticket.TicketStructure;
import lemury.biletomat.query.QueryExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class TicketStructureTests extends DatabaseSafeTests {
    @Test
    public void createTicketStructureTest() {
        int id = TicketStructure.create("Test", exampleDepartmentId);

        Assert.assertTrue(id > 0);
        ObservableList<String> names = TicketStructure.getNames();
        Assert.assertTrue(names.contains("Test"));
    }

    @Test
    public void createTicketWithFieldsTest() throws SQLException {
        int id = TicketStructure.create("TestF", exampleDepartmentId);
        Optional<TicketStructure> optionalTicketStructure = TicketStructure.findById(id);

        if (optionalTicketStructure.isPresent()) {
            TicketStructure addedStructure = optionalTicketStructure.get();
            addedStructure.addField("data", true, "date");
            addedStructure.addField("liczba", false, "int");
            addedStructure.addField("tekst", true, "string");
        }

        String sqlQuery = String.format("SELECT COUNT(*) AS COUNTER FROM TICKET_STRUCTURE_DETAILS WHERE ticket_structure_id = %d;", id);
        ResultSet rs = QueryExecutor.read(sqlQuery);

        int count = rs.getInt("COUNTER");

        Assert.assertEquals(3, count);
    }
}
