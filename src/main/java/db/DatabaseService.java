package db;

import Model.Person;
import Model.Visit;
import enums.Roles;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DatabaseService {
    void start();
    Map<String, Integer> getNames(Roles role);
    Map<String, Integer> getPatients(Integer doctorId);
    Person getPerson(Integer personId);
    List<Visit> getDayVisitsFromDoctor(Integer doctorId, LocalDate date);
    List<Visit> getFutureVisits(Integer doctorId);
}
