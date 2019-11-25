package Model.Departments;

import Model.Ticket.Ticket;
import Model.Users.Coordinator;

import java.util.Set;

public class Department {
    private String Name;
    private Set<Coordinator> coordinators;

    public Ticket CreateNewTicket(){ //Is it SRP friendly?
        return new Ticket();
    }



}
