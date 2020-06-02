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

    List<Visit> getDayVisits(LocalDate date);

    List<Visit> getFutureVisits(Integer doctorId);

    List<Specialization> getAvailableSpecializations();

    List<Specialization> getDoctorsSpecialization(Integer doctorId);

    List<Person> getAvailableSpecialistsAtTime(int specializationId, LocalDateTime freeFrom, LocalDateTime freeTo);

    List<Person> getAvailableSpecialistsAtTimeSortedByPatient(int patientId, int specializationId, LocalDateTime freeFrom, LocalDateTime freeTo);

    List<Person> getAvailableNursesAtTimeSortedByPatient(LocalDateTime date1, LocalDateTime date2, Integer patientId);

    List<Office> getAvailableOfficesAtTime(LocalDateTime freeFrom, LocalDateTime freeTo);

    List<Office> getAvailableNurseOffices(LocalDateTime freeFrom, LocalDateTime freeTo);

    List<Office> getAvailableOfficesAtTimeSortedByDoctor(int doctorId, LocalDateTime freeFrom, LocalDateTime freeTo);

    List<Office> getAvailableOfficesAtTimeSortedByNurse(LocalDateTime freeFrom, LocalDateTime freeTo, int nurseId);

    List<Person> getDoctorsVisitCount(LocalDate date1, LocalDate date2);

    List<Person> getNursesVisitCount(LocalDate date1, LocalDate date2);

    Map<String, String> getAllDiseases();

    Visit getNextVisit(Integer doctorId);

    Disease getDisease(String code);

    Specialization getSpecialization(Integer specializationId);

    Medicine getMedicine(Integer medicineId);

    Office getOffice(Integer officeId);

    void updateVisit(Visit visit);

    void updatePerson(Person person);

    void newVisit(Visit visit);

    void newExertion(Exertion exertion);

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

    Integer getPrescriptionCount(LocalDate date1, LocalDate date2, Integer doctorId);

    Integer getZwolnienieCount(LocalDate date1, LocalDate date2);

    Integer getZwolnienieCount(LocalDate date1, LocalDate date2, Integer doctorId);

    Integer getSkierowanieCount(LocalDate date1, LocalDate date2);

    Integer getSkierowanieCount(LocalDate date1, LocalDate date2, Integer doctorId);

    Integer getLongestZwolnienie(LocalDate date1, LocalDate date2);

    Integer getLongestZwolnienie(LocalDate date1, LocalDate date2, Integer doctorId);

    Office getMostUsedOffice(LocalDate date1, LocalDate date2);

    Office getMostUsedOffice(LocalDate date1, LocalDate date2, Integer doctorId);

    Medicine getMostCommonMedicine(LocalDate date1, LocalDate date2);

    Medicine getMostCommonMedicine(LocalDate date1, LocalDate date2, Integer doctorId);

    void fireWorker(Integer workerId);

    void cancelVisit(Integer visitId);

    boolean isNonFiredWorker(String pesel);

    boolean isFiredWorker(String pesel);

    String getPrettyNameByPesel(String pesel);

    void rehire(String pesel);

    boolean isInDb(String pesel);

    void addRole(Integer id, Roles role);

    void updateMedicine(Medicine medicine);

    void newMedicine(String name);

    boolean hasMedicine(String name);

    boolean isPatientFree(LocalDateTime from, LocalDateTime to, Integer patientId);
}
