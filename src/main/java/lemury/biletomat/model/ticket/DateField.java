package lemury.biletomat.model.ticket;

import java.time.LocalDate;
import java.util.Date;

public class DateField extends Field {
    private LocalDate date;

    public DateField(String name, boolean required, LocalDate date){
        super(name, required, "date");
        this.date = date;
    }



}
