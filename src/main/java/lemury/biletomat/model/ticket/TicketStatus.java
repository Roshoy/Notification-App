package lemury.biletomat.model.ticket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public enum  TicketStatus {
    WAITING,
    IN_PROGRESS,
    DONE;

    public static ObservableList<String> getNames() {
        ObservableList<String> result = FXCollections.observableArrayList();
        for(TicketStatus stat : TicketStatus.values()) {
            result.add(stat.toString().toLowerCase());
        }

        return result;
    }
}
