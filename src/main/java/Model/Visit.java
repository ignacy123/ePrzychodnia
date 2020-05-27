package Model;

import converters.OfficeConverter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Visit {
    Integer id;
    Person patient;
    Person doctor;
    Specialization specialization;
    Timestamp start;
    Timestamp end;
    Office office;
    boolean takenPlace;
    String note;
    List<String> diseases;
    boolean hasSkierowanie;
    boolean hasZwolnienie;
    Timestamp zwolnienieStart;
    Timestamp zwolnienieEnd;
    boolean hasRecepta;
    List<Medicine> medicines;
    List<Referral> referrals;

    public List<Referral> getReferrals() {
        return referrals;
    }

    public void setReferrals(List<Referral> referrals) {
        this.referrals = referrals;
    }

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

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
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

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
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

    public List<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<Medicine> medicines) {
        this.medicines = medicines;
    }

    public List<String> getDiseases() {
        return diseases;
    }



    @Override
    public String toString() {
        return "Pacjent: " + patient.getName() + " " + patient.getLastName() + " " + "\nLekarz: " + doctor.getName() + " " + doctor.getLastName() + "\nData: " + start + "\nGabinet: " + new OfficeConverter().toString(office) + "\nOdbył się: " + takenPlace + "\nNotatka: " + note;
    }
}
