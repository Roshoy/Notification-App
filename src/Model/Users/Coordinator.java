package Model.Users;

import Model.Departments.Department;
import Model.Ticket.Ticket;

import java.util.List;

public class Coordinator extends User {

    private Department department; //maybe a new type?
    private List<Ticket> tickets;

    public Coordinator(int id, String firstName, String lastName, String login, String password, Department department) {
        super(id, firstName, lastName, login, password);
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }

    public void EditTicket(Ticket ticket){
        //change status of ticket
    }
}
