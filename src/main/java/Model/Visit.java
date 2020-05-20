package Model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Visit {
    Integer id;
    Person patient;
    Person doctor;
    Timestamp start;
    Timestamp end;
    Integer room;
    boolean takenPlace;
    String note;
    List<String> diseases;
    boolean hasSkierowanie;
    Integer specializationId;
    String skierowanieNote;
    boolean hasZwolnienie;
    Timestamp zwolnienieStart;
    Timestamp zwolnienieEnd;
    boolean hasRecepta;
    List<Integer> medicineId;
    List<String> instructions;

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

    public boolean isTakenPlace() {
        return takenPlace;
    }


    public void setDiseases(List<String> diseases) {
        this.diseases = diseases;
    }

    public boolean hasSkierowanie() {
        return hasSkierowanie;
    }

    public void setHasSkierowanie(boolean hasSkierowanie) {
        this.hasSkierowanie = hasSkierowanie;
    }

    public Integer getSpecializationId() {
        return specializationId;
    }

    public void setSpecializationId(Integer specializationId) {
        this.specializationId = specializationId;
    }

    public String getSkierowanieNote() {
        return skierowanieNote;
    }

    public void setSkierowanieNote(String skierowanieNote) {
        this.skierowanieNote = skierowanieNote;
    }

    public boolean hasZwolnienie() {
        return hasZwolnienie;
    }

    public void setHasZwolnienie(boolean hasZwolnienie) {
        this.hasZwolnienie = hasZwolnienie;
    }

    public Timestamp getZwolnienieStart() {
        return zwolnienieStart;
    }

    public void setZwolnienieStart(Timestamp zwolnienieStart) {
        this.zwolnienieStart = zwolnienieStart;
    }

    public Timestamp getZwolnienieEnd() {
        return zwolnienieEnd;
    }

    public void setZwolnienieEnd(Timestamp zwolnienieEnd) {
        this.zwolnienieEnd = zwolnienieEnd;
    }

    public boolean hasRecepta() {
        return hasRecepta;
    }

    public void setHasRecepta(boolean hasRecepta) {
        this.hasRecepta = hasRecepta;
    }


    public void setMedicineId(ArrayList<Integer> medicineId) {
        this.medicineId = medicineId;
    }

    public void setInstructions(ArrayList<String> instructions) {
        this.instructions = instructions;
    }

    public List<String> getDiseases() {
        return diseases;
    }


    public List<Integer> getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(List<Integer> medicineId) {
        this.medicineId = medicineId;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return "Pacjent: " + patient.getName() + " " + patient.getLastName() + " " + "\nLekarz: " + doctor.getName() + " " + doctor.getLastName() + "\nData: " + start + "\nGabinet: " + room + "\nOdbył się: " + takenPlace + "\nNotatka: " + note;
    }
}
