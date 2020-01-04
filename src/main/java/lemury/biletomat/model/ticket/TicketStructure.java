package lemury.biletomat.model.ticket;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TicketStructure {
    private List<Field> fields;

    public TicketStructure(){
        fields = new ArrayList<>();
    }

    public void addIntField(String name, boolean required, int value){
        IntField intField = new IntField(name, required, value);
        fields.add(intField);
    }

    public void addStringField(String name, boolean required, String value){
        StringField stringField = new StringField(name, required, value);
        fields.add(stringField);
    }

    public void addDateField(String name, boolean required, LocalDate date){
        DateField dateField = new DateField(name, required, date);
        fields.add(dateField);
    }

    public List<Field> getFields() {
        return fields;
    }


}
