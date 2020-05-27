package Model;

import enums.Roles;

import java.sql.Date;

public class Worker extends Person{
    Roles role;
    Date hiredFrom;

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public Date getHiredFrom() {
        return hiredFrom;
    }

    public void setHiredFrom(Date hiredFrom) {
        this.hiredFrom = hiredFrom;
    }
}
