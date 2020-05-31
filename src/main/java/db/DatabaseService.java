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

    Person getPerson(String pesel);

    List<Visit> getDayVisitsFromDoctor(Integer doctorId, LocalDate date);

    List<Visit> getFutureVisits(Integer doctorId);

    List<Specialization> getAvailableSpecializations();

    List<Specialization> getDoctorsSpecialization(Integer doctorId);

    List<Person> getAvailableSpecialistsAtTime(int specializationId, LocalDateTime freeFrom, LocalDateTime freeTo);

    List<Person> getAvailableSpecialistsAtTimeSortedByPatient(int patientId, int specializationId, LocalDateTime freeFrom, LocalDateTime freeTo);

    List<Office> getAvailableOfficesAtTime(LocalDateTime freeFrom, LocalDateTime freeTo);

    List<Office> getAvailableOfficesAtTimeSortedByDoctor(int doctorId, LocalDateTime freeFrom, LocalDateTime freeTo);

    List<Person> getDoctorsVisitCount(LocalDate date1, LocalDate date2);

    List<Person> getNursesVisitCount(LocalDate date1, LocalDate date2);

    Map<String, String> getAllDiseases();

    Visit getNextVisit(Integer doctorId);

    Disease getDisease(String code);

    Specialization getSpecialization(Integer specializationId);

    Medicine getMedicine(Integer medicineId);

    Office getOffice(Integer officeId);

    void updateVisit(Visit visit);

    void newVisit(Visit visit);

    Integer addPerson(Person person);

    void addSpecialization(Integer doctorId, Integer specializationId);

    Map<String, Integer> getAllMedicines();

    Map<String, Integer> getAllSpecializations();

    List<Worker> getAllWorkers();

    Integer getTotalVisitCount(Integer doctorId);

    Integer getTotalExertionCount(Integer workerId);

    Integer getTotalPrescriptionCount(Integer doctorId);

    Integer getVisitCount(LocalDate date1, LocalDate date2);

    Integer getExertionCount(LocalDate date1, LocalDate date2);

    Integer getPrescriptionCount(LocalDate date1, LocalDate date2);

    Integer getZwolnienieCount(LocalDate date1, LocalDate date2);

    Integer getSkierowanieCount(LocalDate date1, LocalDate date2);

    Integer getLongestZwolnienie(LocalDate date1, LocalDate date2);

    Office getMostUsedOffice(LocalDate date1, LocalDate date2);

    Medicine getMostCommonMedicine(LocalDate date1, LocalDate date2);

    void fireWorker(Integer workerId);

    boolean isNonFiredWorker(String pesel);

    boolean isFiredWorker(String pesel);

    String getPrettyNameByPesel(String pesel);

    void rehire(String pesel);

    boolean isInDb(String pesel);

    void addRole(Integer id, Roles role);
}
