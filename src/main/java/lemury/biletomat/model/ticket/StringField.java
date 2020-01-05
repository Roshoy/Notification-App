package lemury.biletomat.model.ticket;

public class StringField extends Field {
    private String value;

    public StringField(String name, boolean required, String value){
        super(name, required, "string");
        this.value = value;
    }
}
