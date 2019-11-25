package Model.Ticket;

import Model.Users.Coordinator;
import Model.Users.User;

public class Ticket {
    private Coordinator owner; //if feels wierd, change (guy who takes care of this ticket, coordinator)
    private User submitee;

    private String title;
    private String description;
    private TicketStatus status;
    private String releaseNotes; //what did I do whit this ticket?
    //date etc


    public Ticket(Coordinator owner, User submitee, String title, String description) {
        this.owner = owner;
        this.submitee = submitee;
        this.title = title;
        this.description = description;
        this.status = TicketStatus.WAITING;
    }

    public Ticket(){};



}
