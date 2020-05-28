package Model;

import enums.Roles;

import java.sql.Date;
import java.util.List;

public class Worker extends Person{
    Roles role;
    Date hiredFrom;
    boolean active;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
