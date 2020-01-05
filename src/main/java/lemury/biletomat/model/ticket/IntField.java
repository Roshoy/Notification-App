package lemury.biletomat.model.ticket;

import java.time.LocalDate;

public class IntField extends Field {
    private int value;

    public IntField(String name, boolean required, int value){
        super(name, required, "int");
        this.value = value;
    }
}
