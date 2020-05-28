package db;

import Model.*;
import enums.Roles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface DatabaseService {
    void start();

    Map<String, Integer> getNames(Roles role);

    Map<String, Integer> getPatients(Integer doctorId);

    Map<String, Integer> getPatients();

    Person getPerson(Integer personId);

    List<Visit> getDayVisitsFromDoctor(Integer doctorId, LocalDate date);

    List<Visit> getFutureVisits(Integer doctorId);

    List<Specialization> getAvailableSpecializations();

    List<Specialization> getDoctorsSpecialization(Integer doctorId);

    List<Person> getAvailableSpecialistsAtTime(int specializationId, LocalDateTime freeFrom, LocalDateTime freeTo);

    List<Person> getAvailableSpecialistsAtTimeSortedByPatient(int patientId, int specializationId, LocalDateTime freeFrom, LocalDateTime freeTo);

    List<Office> getAvailableOfficesAtTime(LocalDateTime freeFrom, LocalDateTime freeTo);

    List<Office> getAvailableOfficesAtTimeSortedByDoctor(int doctorId, LocalDateTime freeFrom, LocalDateTime freeTo);

    Map<String, String> getAllDiseases();

    Visit getNextVisit(Integer doctorId);

    Disease getDisease(String code);

    Specialization getSpecialization(Integer specializationId);

    Medicine getMedicine(Integer medicineId);

    Office getOffice(Integer officeId);

    void updateVisit(Visit visit);

    void newVisit(Visit visit);

    void addSpecialization(Integer doctorId, Integer specializationId);

    Map<String, Integer> getAllMedicines();

    Map<String, Integer> getAllSpecializations();

    List<Worker> getAllWorkers();

    Integer getTotalVisitCount(Integer doctorId);

    Integer getTotalExertionCount(Integer workerId);

    Integer getTotalPrescriptionCount(Integer doctorId);

    void fireWorker(Integer workerId);
}
