package db;

import Model.Person;
import Model.Specialization;
import Model.Visit;
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
    List<Person> getAvailableSpecialistsAtTime(int specializationId, LocalDateTime freeFrom, LocalDateTime freeTo);
}
