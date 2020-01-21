package lemury.biletomat.model.ticket;

import lemury.biletomat.query.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Field {
    private int id;
    private String name;
    private boolean required;
    private FieldType type;

    private static final String TABLE_NAME = "TICKET_STRUCTURE_DETAILS";

    protected Field(int id, String name, boolean required, FieldType type){
        this.id = id;
        this.name = name;
        this.required = required;
        this.type = type;
    }

    public String name() {
        return this.name + ":";
    }

    public static int create(int ticketStructureId, String name, boolean required, FieldType type){
        String insertSql = String.format("INSERT INTO %s(ticket_structure_id, name, required, type) VALUES (%d, '%s', %b, '%s')",
                TABLE_NAME, ticketStructureId, name, required, type.toString().toLowerCase());

        try {
            return QueryExecutor.createAndObtainId(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static Optional<Field> findFieldById(int id) {
        String findSql = String.format("SELECT * FROM %s WHERE id = %d;", TABLE_NAME, id);

        try {
            ResultSet rs = QueryExecutor.read(findSql);
            return Optional.of(new Field(id, rs.getString("name"), rs.getBoolean("required"), FieldType.valueOf(rs.getString("type").toUpperCase())));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static int countFields(int ticket_id){
        String sql = String.format("SELECT COUNT(*) as counter FROM %s WHERE ticket_id = %d", TABLE_NAME, ticket_id);
        System.out.println(sql);
        int counter = 0;

        try {
            counter = QueryExecutor.createAndObtainId(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }
}
