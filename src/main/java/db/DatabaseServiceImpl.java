package db;

import Model.Person;
import Model.Specialization;
import Model.Visit;
import enums.Roles;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseServiceImpl implements DatabaseService {
    Connection c = null;
    Statement statement = null;

    public void start() {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/ignacy",
                            "ignacy", "root");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    @Override
    public Map<String, Integer> getNames(Roles role) {
        //SELECT id, imie, nazwisko FROM dane_osob WHERE id IN (SELECT id_pracownika FROM pracownicy WHERE etat='LEKARZ');
        Map<String, Integer> toReturn = new HashMap<>();
        String sql = "SELECT id, imie, nazwisko FROM dane_osob WHERE id IN (SELECT id_pracownika FROM pracownicy WHERE etat='" + role + "')";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2) + " " + resultSet.getString(3);
                toReturn.put(name, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public Map<String, Integer> getPatients(Integer doctorId) {

        //SELECT id, imie, nazwisko FROM dane_osob WHERE id IN (SELECT id_pracownika FROM pracownicy WHERE etat='LEKARZ');
        Map<String, Integer> toReturn = new HashMap<>();
        String sql = "SELECT id, imie, nazwisko FROM dane_osob WHERE id IN (SELECT pacjent FROM wizyty WHERE lekarz=" + doctorId + ")";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2) + " " + resultSet.getString(3);
                toReturn.put(name, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public Map<String, Integer> getPatients() {
        Map<String, Integer> toReturn = new HashMap<>();
        String sql = "SELECT id, imie, nazwisko FROM dane_osob;";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2) + " " + resultSet.getString(3);
                toReturn.put(name, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public Person getPerson(Integer personId) {
        Person toReturn = new Person();
        String sql = "SELECT * FROM dane_osob WHERE id=" + personId;
        ;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toReturn.setName(resultSet.getString(2));
                toReturn.setLastName(resultSet.getString(3));
                toReturn.setPesel(resultSet.getString(4));
                toReturn.setDateOfBirth(resultSet.getDate(5));
                toReturn.setPhoneNumber(resultSet.getString(6));
                toReturn.setEmail(resultSet.getString(7));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public List<Visit> getDayVisitsFromDoctor(Integer doctorId, LocalDate date) {
        List<Visit> toReturn = new ArrayList<>();
        String sql = "SELECT * FROM wizyty WHERE termin_wizyty >= '" + date + " 00:00:00' AND termin_wizyty<='" + date + " 23:59:59' AND lekarz=" + doctorId;
        ;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                //TODO koniec wizyty
                Visit visit = new Visit();
                visit.setPatient(getPerson(resultSet.getInt(2)));
                visit.setDoctor(getPerson(resultSet.getInt(3)));
                visit.setStart(resultSet.getTimestamp(4));
                visit.setRoom(resultSet.getInt(6));
                visit.setTakenPlace(resultSet.getBoolean(7));
                visit.setNote(resultSet.getString(8));
                toReturn.add(visit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public List<Visit> getFutureVisits(Integer doctorId) {
        List<Visit> toReturn = new ArrayList<>();
        String sql = "SELECT * FROM wizyty WHERE termin_wizyty > CURRENT_DATE AND termin_wizyty< CURRENT_TIMESTAMP + (INTERVAL '7 DAYS') AND lekarz=" + doctorId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Visit visit = new Visit();
                visit.setPatient(getPerson(resultSet.getInt(2)));
                visit.setDoctor(getPerson(resultSet.getInt(3)));
                visit.setStart(resultSet.getTimestamp(4));
                visit.setRoom(resultSet.getInt(6));
                visit.setTakenPlace(resultSet.getBoolean(7));
                visit.setNote(resultSet.getString(8));
                toReturn.add(visit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public List<Specialization> getAvailableSpecializations() {
        List<Specialization> toRet = new ArrayList<>();
        String sql = "SELECT * FROM specjalizacje WHERE id_specjalizacji IN (SELECT specjalizacje.id_specjalizacji FROM lekarze_specjalizacje LEFT OUTER JOIN specjalizacje ON specjalizacje.id_specjalizacji = lekarze_specjalizacje.id_specjalizacji GROUP BY specjalizacje.id_specjalizacji);";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                Specialization specialization = new Specialization();
                specialization.setId(resultSet.getInt(1));
                specialization.setPrettyName(resultSet.getString(2));
                toRet.add(specialization);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public List<Person> getAvailableSpecialistsAtTime(int specializationId, LocalDateTime freeFrom, LocalDateTime freeTo) {
        List<Person> toRet = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String from = freeFrom.format(formatter);
        String to = freeTo.format(formatter);
        String sql = "SELECT * FROM dane_osob WHERE id IN (SELECT id_lekarza FROM lekarze_specjalizacje WHERE id_specjalizacji = "+specializationId+") AND " +
                "id NOT IN (SELECT lekarz FROM wizyty WHERE termin_wizyty>= '"+from+"' AND koniec_wizyty<='"+to+"');";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                Person person = new Person();
                person.setName(resultSet.getString(2));
                person.setLastName(resultSet.getString(3));
                person.setPesel(resultSet.getString(4));
                person.setDateOfBirth(resultSet.getDate(5));
                person.setPhoneNumber(resultSet.getString(6));
                person.setEmail(resultSet.getString(7));
                toRet.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;

    }
}
