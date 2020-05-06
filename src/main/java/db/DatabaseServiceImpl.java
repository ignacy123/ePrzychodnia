package db;

import Model.Person;
import Model.Visit;
import enums.Roles;

import java.sql.*;
import java.time.LocalDate;
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
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    @Override
    public Map<String, Integer> getNames(Roles role) {
        //SELECT id, imie, nazwisko FROM dane_osob WHERE id IN (SELECT id_pracownika FROM pracownicy WHERE etat='LEKARZ');
        Map<String, Integer> toReturn = new HashMap<>();
        String sql = "SELECT id, imie, nazwisko FROM dane_osob WHERE id IN (SELECT id_pracownika FROM pracownicy WHERE etat='"+role+"')";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2)+" "+resultSet.getString(3);
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
        String sql = "SELECT id, imie, nazwisko FROM dane_osob WHERE id IN (SELECT pacjent FROM wizyty WHERE lekarz="+doctorId+")";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2)+" "+resultSet.getString(3);
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
        String sql = "SELECT * FROM dane_osob WHERE id="+personId;
        ;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
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
        String sql = "SELECT * FROM wizyty WHERE termin_wizyty >= '"+date+" 00:00:00' AND termin_wizyty<='"+date+" 23:59:59' AND lekarz="+doctorId;
        ;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                Visit visit = new Visit();
                visit.setPatient(getPerson(resultSet.getInt(2)));
                visit.setDoctor(getPerson(resultSet.getInt(3)));
                visit.setDate(resultSet.getDate(4));
                visit.setRoom(resultSet.getInt(5));
                visit.setTakenPlace(resultSet.getBoolean(6));
                visit.setNote(resultSet.getString(7));
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
        String sql = "SELECT * FROM wizyty WHERE termin_wizyty > CURRENT_DATE AND lekarz="+doctorId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                Visit visit = new Visit();
                visit.setPatient(getPerson(resultSet.getInt(2)));
                visit.setDoctor(getPerson(resultSet.getInt(3)));
                visit.setDate(resultSet.getDate(4));
                visit.setRoom(resultSet.getInt(5));
                visit.setTakenPlace(resultSet.getBoolean(6));
                visit.setNote(resultSet.getString(7));
                toReturn.add(visit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }
}
