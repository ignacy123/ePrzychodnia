package db;

import Model.Disease;
import Model.Person;
import Model.Specialization;
import Model.Visit;
import enums.Roles;
import javafx.util.StringConverter;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
                toReturn.setId(resultSet.getInt(1));
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
        String sql = "SELECT * FROM wizyty_info WHERE termin_wizyty >= '" + date + " 00:00:00' AND termin_wizyty<='" + date + " 23:59:59' AND lekarz=" + doctorId;
        ;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Visit visit = new Visit();
                visit.setId(resultSet.getInt(1));
                visit.setPatient(getPerson(resultSet.getInt(2)));
                visit.setDoctor(getPerson(resultSet.getInt(4)));
                visit.setStart(resultSet.getTimestamp(5));
                visit.setEnd(resultSet.getTimestamp(6));
                visit.setRoom(resultSet.getInt(7));
                visit.setTakenPlace(resultSet.getBoolean(8));
                visit.setNote(resultSet.getString(9));
                Array array = resultSet.getArray(10);
                if(array!=null){
                    List<String> diseasesCode = Arrays.asList((String[]) array.getArray());
                    visit.setDiseases(diseasesCode);
                }
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                visit.setSpecializationId(resultSet.getInt(12));
                visit.setSkierowanieNote(resultSet.getString(13));
                visit.setHasZwolnienie(resultSet.getBoolean(14));
                visit.setZwolnienieStart(resultSet.getTimestamp(15));
                visit.setZwolnienieEnd(resultSet.getTimestamp(16));
                visit.setHasRecepta(resultSet.getBoolean(17));
                array = resultSet.getArray(18);
                if(array!=null){
                    List<Integer> medicine = Arrays.asList((Integer[]) array.getArray());
                    visit.setMedicineId(medicine);
                }
                array = resultSet.getArray(19);
                if(array!=null){
                    List<String> instructions = Arrays.asList((String[]) array.getArray());
                    visit.setInstructions(instructions);
                }
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
        //String sql = "SELECT * FROM wizyty WHERE termin_wizyty > CURRENT_DATE AND termin_wizyty< CURRENT_TIMESTAMP + (INTERVAL '7 DAYS') AND lekarz=" + doctorId;
        String sql = "SELECT * FROM wizyty_info WHERE termin_wizyty > CURRENT_DATE AND lekarz=" + doctorId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Visit visit = new Visit();
                visit.setId(resultSet.getInt(1));
                visit.setPatient(getPerson(resultSet.getInt(2)));
                visit.setDoctor(getPerson(resultSet.getInt(4)));
                visit.setStart(resultSet.getTimestamp(5));
                visit.setEnd(resultSet.getTimestamp(6));
                visit.setRoom(resultSet.getInt(7));
                visit.setTakenPlace(resultSet.getBoolean(8));
                visit.setNote(resultSet.getString(9));
                Array array = resultSet.getArray(10);
                if(array!=null){
                    List<String> diseasesCode = Arrays.asList((String[]) array.getArray());
                    visit.setDiseases(diseasesCode);
                }
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                visit.setSpecializationId(resultSet.getInt(12));
                visit.setSkierowanieNote(resultSet.getString(13));
                visit.setHasZwolnienie(resultSet.getBoolean(14));
                visit.setZwolnienieStart(resultSet.getTimestamp(15));
                visit.setZwolnienieEnd(resultSet.getTimestamp(16));
                visit.setHasRecepta(resultSet.getBoolean(17));
                array = resultSet.getArray(18);
                if(array!=null){
                    List<Integer> medicine = Arrays.asList((Integer[]) array.getArray());
                    visit.setMedicineId(medicine);
                }
                array = resultSet.getArray(19);
                if(array!=null){
                    List<String> instructions = Arrays.asList((String[]) array.getArray());
                    visit.setInstructions(instructions);
                }

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
            while (resultSet.next()) {
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
        String sql = "SELECT * FROM dane_osob WHERE id IN (SELECT id_lekarza FROM lekarze_specjalizacje WHERE id_specjalizacji = " + specializationId + ") AND " +
                "id NOT IN (SELECT lekarz FROM wizyty WHERE termin_wizyty>= '" + from + "' AND koniec_wizyty<='" + to + "');";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Person person = new Person();
                person.setId(resultSet.getInt(1));
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

    @Override
    public List<Person> getAvailableSpecialistsAtTimeSortedByPatient(int patientId, int specializationId, LocalDateTime freeFrom, LocalDateTime freeTo) {
        List<Person> toRet = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String from = freeFrom.format(formatter);
        String to = freeTo.format(formatter);
        String sql = "SELECT id_pracownika, COALESCE(count, 0) FROM pracownicy LEFT OUTER JOIN (SELECT lekarz, count(id_wizyty) FROM wizyty WHERE pacjent=" + patientId + " AND lekarz IN (SELECT id_lekarza FROM lekarze_specjalizacje WHERE id_specjalizacji = " + specializationId + ") GROUP BY lekarz) AS s1 ON s1.lekarz = pracownicy.id_pracownika WHERE id_pracownika IN (SELECT id_lekarza FROM lekarze_specjalizacje WHERE id_specjalizacji = " + specializationId + ") AND id_pracownika NOT IN (SELECT lekarz FROM wizyty WHERE termin_wizyty>= '" + from + "' AND koniec_wizyty<='" + to + "') ORDER BY 2 DESC;";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Person person = getPerson(resultSet.getInt(1));
                toRet.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Map<String, String> getAllDiseases() {
        Map<String, String> toRet = new HashMap<>();
        String sql = "SELECT * FROM dolegliwosci;";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet.put(resultSet.getString(2), resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Disease getDisease(String code) {
        Disease toReturn = new Disease();
        String sql = "SELECT * FROM dolegliwosci WHERE kod_icd10='" + code+"';";
        ;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toReturn.setIcd10Code(resultSet.getString(1));
                toReturn.setPrettyName(resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public Specialization getSpecialization(Integer specializationId) {
       Specialization toReturn = new Specialization();
        String sql = "SELECT * FROM specjalizacje WHERE id_specjalizacji=" + specializationId;
        ;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toReturn.setId(resultSet.getInt(1));
                toReturn.setPrettyName(resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public void updateVisit(Visit visit) {
        //SELECT insert_wizyta(1, true, 'test', array['A00', 'A01', 'A02'], true, 1, 'gupi jest', true, CURRENT_DATE, '2020-05-20', true, array[1, 2, 3], array['ulotka', 'ulotka', 'ulotka']);
        String choroby = "null";
        String lekiId = "null";
        String instructions = "null";
        if(visit.getDiseases()!=null && visit.getDiseases().size()!=0){
            choroby = "array[";
            for(String s: visit.getDiseases()){
                choroby += "'"+s+"'"+", ";
            }
            choroby = choroby.substring(0, choroby.length()-2);
            choroby += "]";
        }
        if(visit.hasRecepta() && visit.getMedicineId().size()!=0){
            lekiId = "array[";
            for(Integer s: visit.getMedicineId()){
                lekiId += s+", ";
            }
            lekiId = choroby.substring(choroby.length()-2);
            lekiId += "]";
        }
        if(visit.hasRecepta() && visit.getMedicineId().size()!=0){
            instructions = "array[";
            for(String s: visit.getInstructions()){
                instructions += "'"+s+"'"+", ";
            }
            instructions = choroby.substring(choroby.length()-2);
            instructions += "]";
        }
        if(visit.getSkierowanieNote()==null){
            visit.setSkierowanieNote("");
        }
        if(visit.getZwolnienieStart()==null){
            visit.setZwolnienieStart(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
        }
        if(visit.getZwolnienieEnd()==null){
            visit.setZwolnienieEnd(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
        }
        String sql = "SELECT insert_wizyta("+visit.getId()+", "+visit.hasTakenPlace()+", '"+visit.getNote()+"', "+choroby+", "+visit.hasSkierowanie()+", "+visit.getSpecializationId()+", '"+visit.getSkierowanieNote()+"', "+visit.hasZwolnienie()+", '"+visit.getZwolnienieStart()+"', '"+visit.getZwolnienieEnd()+"', "+visit.hasRecepta()+", "+lekiId+", "+instructions+");";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
