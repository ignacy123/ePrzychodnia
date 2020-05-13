package Model;

import java.sql.Timestamp;

public class Visit {
    Person patient;
    Person doctor;
    Timestamp start;
    Timestamp end;
    Integer room;
    boolean takenPlace;
    String note;

    public Person getPatient() {
        return patient;
    }

    public void setPatient(Person patient) {
        this.patient = patient;
    }

    public Person getDoctor() {
        return doctor;
    }

    public void setDoctor(Person doctor) {
        this.doctor = doctor;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public boolean hasTakenPlace() {
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

    @Override
    public String toString() {
        return "Pacjent: "+patient.getName()+" "+patient.getLastName()+" "+"\nLekarz: "+doctor.getName()+" "+doctor.getLastName()+"\nData: "+start+"\nGabinet: "+room+"\nOdbył się: "+takenPlace+"\nNotatka: "+note;
    }
}
