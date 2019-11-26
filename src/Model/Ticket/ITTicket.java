package Model.Ticket;

import Model.Users.Coordinator;
import Model.Users.User;

public class ITTicket extends Ticket {
    private  int computerNo;

    public ITTicket(int id, Coordinator owner, User submitee, String title, String description, int computerNo){
        super(id, owner, submitee, title, description);
        this.computerNo = computerNo;
    }
}
