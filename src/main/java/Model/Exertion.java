package Model;

import java.sql.Timestamp;

public class Exertion {
    Integer id;
    Person patient;
    Person nurse;
    Timestamp start;
    Office office;
    boolean takenPlace;
    String note;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Person getPatient() {
        return patient;
    }

    public void setPatient(Person patient) {
        this.patient = patient;
    }

    public Person getNurse() {
        return nurse;
    }

    public void setNurse(Person nurse) {
        this.nurse = nurse;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public boolean isTakenPlace() {
        return takenPlace;
    }

    public void setTakenPlace(boolean takenPlace) {
        this.takenPlace = takenPlace;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
