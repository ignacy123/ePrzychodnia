package db;

import Model.*;
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
        String sql = "SELECT id, imie, nazwisko FROM dane_osob WHERE id IN (SELECT id_pracownika FROM pracownicy WHERE etat='" + role + "' AND status_zatrudnienia=true) ";
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
    public Person getPerson(String pesel) {
        Person toReturn = new Person();
        String sql = "SELECT * FROM dane_osob WHERE pesel='" + pesel + "'";
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
        String sql = "SELECT * FROM wizyty_info WHERE termin_wizyty >= '" + date + " 00:00:00' AND termin_wizyty<='" + date + " 23:59:59' AND lekarz=" + doctorId + " ORDER BY 5";
        ;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Visit visit = new Visit();
                visit.setId(resultSet.getInt(1));
                visit.setPatient(getPerson(resultSet.getInt(2)));
                visit.setSpecialization(getSpecialization(resultSet.getInt(3)));
                visit.setDoctor(getPerson(resultSet.getInt(4)));
                visit.setStart(resultSet.getTimestamp(5));
                visit.setEnd(resultSet.getTimestamp(6));
                visit.setOffice(getOffice(resultSet.getInt(7)));
                visit.setTakenPlace(resultSet.getBoolean(8));
                visit.setNote(resultSet.getString(9));
                Array array = resultSet.getArray(10);
                if (array != null) {
                    List<String> diseasesCode = Arrays.asList((String[]) array.getArray());
                    visit.setDiseases(diseasesCode);
                }
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                array = resultSet.getArray(12);
                Array array2 = resultSet.getArray(13);
                if (array != null) {
                    List<Referral> list = new ArrayList<>();
                    List<Integer> ids = Arrays.asList((Integer[]) array.getArray());
                    List<String> desc = Arrays.asList((String[]) array2.getArray());
                    for (int i = 0; i < ids.size(); i++) {
                        Referral ref = new Referral();
                        ref.setSpecialization(getSpecialization(ids.get(i)));
                        ref.setNote(desc.get(i));
                        list.add(ref);
                    }
                    visit.setReferrals(list);
                }
                visit.setHasZwolnienie(resultSet.getBoolean(14));
                visit.setZwolnienieStart(resultSet.getTimestamp(15));
                visit.setZwolnienieEnd(resultSet.getTimestamp(16));
                visit.setHasRecepta(resultSet.getBoolean(17));
                array = resultSet.getArray(18);
                List<Integer> medicines = null;
                List<String> instructions = null;
                List<Medicine> med = new ArrayList<>();
                if (array != null) {
                    medicines = Arrays.asList((Integer[]) array.getArray());
                }
                array = resultSet.getArray(19);
                if (array != null) {
                    instructions = Arrays.asList((String[]) array.getArray());
                }
                if (medicines != null) {
                    for (int i = 0; i < medicines.size(); i++) {
                        Medicine medicine = getMedicine(medicines.get(i));
                        medicine.setInstruction(instructions.get(i));
                        med.add(medicine);
                    }
                }
                visit.setMedicines(med);
                toReturn.add(visit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public List<Visit> getDayVisits(LocalDate date) {
        List<Visit> toReturn = new ArrayList<>();
        String sql = "SELECT * FROM wizyty_info WHERE termin_wizyty >= '" + date + " 00:00:00' AND termin_wizyty<='" + date + " 23:59:59' ORDER BY 5";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Visit visit = new Visit();
                visit.setId(resultSet.getInt(1));
                visit.setPatient(getPerson(resultSet.getInt(2)));
                visit.setSpecialization(getSpecialization(resultSet.getInt(3)));
                visit.setDoctor(getPerson(resultSet.getInt(4)));
                visit.setStart(resultSet.getTimestamp(5));
                visit.setEnd(resultSet.getTimestamp(6));
                visit.setOffice(getOffice(resultSet.getInt(7)));
                visit.setTakenPlace(resultSet.getBoolean(8));
                visit.setNote(resultSet.getString(9));
                Array array = resultSet.getArray(10);
                if (array != null) {
                    List<String> diseasesCode = Arrays.asList((String[]) array.getArray());
                    visit.setDiseases(diseasesCode);
                }
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                array = resultSet.getArray(12);
                Array array2 = resultSet.getArray(13);
                if (array != null) {
                    List<Referral> list = new ArrayList<>();
                    List<Integer> ids = Arrays.asList((Integer[]) array.getArray());
                    List<String> desc = Arrays.asList((String[]) array2.getArray());
                    for (int i = 0; i < ids.size(); i++) {
                        Referral ref = new Referral();
                        ref.setSpecialization(getSpecialization(ids.get(i)));
                        ref.setNote(desc.get(i));
                        list.add(ref);
                    }
                    visit.setReferrals(list);
                }
                visit.setHasZwolnienie(resultSet.getBoolean(14));
                visit.setZwolnienieStart(resultSet.getTimestamp(15));
                visit.setZwolnienieEnd(resultSet.getTimestamp(16));
                visit.setHasRecepta(resultSet.getBoolean(17));
                array = resultSet.getArray(18);
                List<Integer> medicines = null;
                List<String> instructions = null;
                List<Medicine> med = new ArrayList<>();
                if (array != null) {
                    medicines = Arrays.asList((Integer[]) array.getArray());
                }
                array = resultSet.getArray(19);
                if (array != null) {
                    instructions = Arrays.asList((String[]) array.getArray());
                }
                if (medicines != null) {
                    for (int i = 0; i < medicines.size(); i++) {
                        Medicine medicine = getMedicine(medicines.get(i));
                        medicine.setInstruction(instructions.get(i));
                        med.add(medicine);
                    }
                }
                visit.setMedicines(med);
                toReturn.add(visit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public List<Exertion> getDayExertions(LocalDate date) {
        List<Exertion> toReturn = new ArrayList<>();
        String sql = "SELECT * FROm zabiegi_pielegniarskie WHERE termin_zabiegu >= '" + date + " 10:00' AND termin_zabiegu <= '" + date + " 23:59'";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Exertion exertion = new Exertion();
                exertion.setId(resultSet.getInt(1));
                exertion.setPatient(getPerson(resultSet.getInt(2)));
                exertion.setNurse(getPerson(resultSet.getInt(3)));
                exertion.setStart(resultSet.getTimestamp(4));
                exertion.setOffice(getOffice(resultSet.getInt(5)));
                exertion.setTakenPlace(resultSet.getBoolean(6));
                exertion.setNote(resultSet.getString(7));
                toReturn.add(exertion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public List<Exertion> getPastExertions(Integer patientId) {
        List<Exertion> toReturn = new ArrayList<>();
        String sql = "SELECT * FROm zabiegi_pielegniarskie WHERE termin_zabiegu < CURRENT_TIMESTAMP AND pacjent=" + patientId + " ORDER BY termin_zabiegu DESC";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Exertion exertion = new Exertion();
                exertion.setId(resultSet.getInt(1));
                exertion.setPatient(getPerson(resultSet.getInt(2)));
                exertion.setNurse(getPerson(resultSet.getInt(3)));
                exertion.setStart(resultSet.getTimestamp(4));
                exertion.setOffice(getOffice(resultSet.getInt(5)));
                exertion.setTakenPlace(resultSet.getBoolean(6));
                exertion.setNote(resultSet.getString(7));
                toReturn.add(exertion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public List<Exertion> getFutureExertions(Integer patientId) {
        List<Exertion> toReturn = new ArrayList<>();
        String sql = "SELECT * FROm zabiegi_pielegniarskie WHERE termin_zabiegu >= CURRENT_TIMESTAMP AND pacjent=" + patientId + " ORDER BY termin_zabiegu";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Exertion exertion = new Exertion();
                exertion.setId(resultSet.getInt(1));
                exertion.setPatient(getPerson(resultSet.getInt(2)));
                exertion.setNurse(getPerson(resultSet.getInt(3)));
                exertion.setStart(resultSet.getTimestamp(4));
                exertion.setOffice(getOffice(resultSet.getInt(5)));
                exertion.setTakenPlace(resultSet.getBoolean(6));
                exertion.setNote(resultSet.getString(7));
                toReturn.add(exertion);
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
        String sql = "SELECT * FROM wizyty_info WHERE termin_wizyty > CURRENT_DATE AND lekarz=" + doctorId + "ORDER BY 4 DESC";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Visit visit = new Visit();
                visit.setId(resultSet.getInt(1));
                visit.setPatient(getPerson(resultSet.getInt(2)));
                visit.setSpecialization(getSpecialization(resultSet.getInt(3)));
                visit.setDoctor(getPerson(resultSet.getInt(4)));
                visit.setStart(resultSet.getTimestamp(5));
                visit.setEnd(resultSet.getTimestamp(6));
                visit.setOffice(getOffice(resultSet.getInt(7)));
                visit.setTakenPlace(resultSet.getBoolean(8));
                visit.setNote(resultSet.getString(9));
                Array array = resultSet.getArray(10);
                if (array != null) {
                    List<String> diseasesCode = Arrays.asList((String[]) array.getArray());
                    visit.setDiseases(diseasesCode);
                }
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                array = resultSet.getArray(12);
                Array array2 = resultSet.getArray(13);
                if (array != null) {
                    List<Referral> list = new ArrayList<>();
                    List<Integer> ids = Arrays.asList((Integer[]) array.getArray());
                    List<String> desc = Arrays.asList((String[]) array2.getArray());
                    for (int i = 0; i < ids.size(); i++) {
                        Referral ref = new Referral();
                        ref.setSpecialization(getSpecialization(ids.get(i)));
                        ref.setNote(desc.get(i));
                        list.add(ref);
                    }
                    visit.setReferrals(list);
                }
                visit.setHasZwolnienie(resultSet.getBoolean(14));
                visit.setZwolnienieStart(resultSet.getTimestamp(15));
                visit.setZwolnienieEnd(resultSet.getTimestamp(16));
                visit.setHasRecepta(resultSet.getBoolean(17));
                array = resultSet.getArray(18);
                List<Integer> medicines = null;
                List<String> instructions = null;
                List<Medicine> med = new ArrayList<>();
                if (array != null) {
                    medicines = Arrays.asList((Integer[]) array.getArray());
                }
                array = resultSet.getArray(19);
                if (array != null) {
                    instructions = Arrays.asList((String[]) array.getArray());
                }
                if (medicines != null) {
                    for (int i = 0; i < medicines.size(); i++) {
                        Medicine medicine = getMedicine(medicines.get(i));
                        medicine.setInstruction(instructions.get(i));
                        med.add(medicine);
                    }
                }
                visit.setMedicines(med);

                toReturn.add(visit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public List<Visit> getFutureVisitsPatient(Integer patientId) {
        List<Visit> toReturn = new ArrayList<>();
        //String sql = "SELECT * FROM wizyty WHERE termin_wizyty > CURRENT_DATE AND termin_wizyty< CURRENT_TIMESTAMP + (INTERVAL '7 DAYS') AND lekarz=" + doctorId;
        String sql = "SELECT * FROM wizyty_info WHERE termin_wizyty >= CURRENT_TIMESTAMP AND pacjent=" + patientId + "ORDER BY 4";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Visit visit = new Visit();
                visit.setId(resultSet.getInt(1));
                visit.setPatient(getPerson(resultSet.getInt(2)));
                visit.setSpecialization(getSpecialization(resultSet.getInt(3)));
                visit.setDoctor(getPerson(resultSet.getInt(4)));
                visit.setStart(resultSet.getTimestamp(5));
                visit.setEnd(resultSet.getTimestamp(6));
                visit.setOffice(getOffice(resultSet.getInt(7)));
                visit.setTakenPlace(resultSet.getBoolean(8));
                visit.setNote(resultSet.getString(9));
                Array array = resultSet.getArray(10);
                if (array != null) {
                    List<String> diseasesCode = Arrays.asList((String[]) array.getArray());
                    visit.setDiseases(diseasesCode);
                }
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                array = resultSet.getArray(12);
                Array array2 = resultSet.getArray(13);
                if (array != null) {
                    List<Referral> list = new ArrayList<>();
                    List<Integer> ids = Arrays.asList((Integer[]) array.getArray());
                    List<String> desc = Arrays.asList((String[]) array2.getArray());
                    for (int i = 0; i < ids.size(); i++) {
                        Referral ref = new Referral();
                        ref.setSpecialization(getSpecialization(ids.get(i)));
                        ref.setNote(desc.get(i));
                        list.add(ref);
                    }
                    visit.setReferrals(list);
                }
                visit.setHasZwolnienie(resultSet.getBoolean(14));
                visit.setZwolnienieStart(resultSet.getTimestamp(15));
                visit.setZwolnienieEnd(resultSet.getTimestamp(16));
                visit.setHasRecepta(resultSet.getBoolean(17));
                array = resultSet.getArray(18);
                List<Integer> medicines = null;
                List<String> instructions = null;
                List<Medicine> med = new ArrayList<>();
                if (array != null) {
                    medicines = Arrays.asList((Integer[]) array.getArray());
                }
                array = resultSet.getArray(19);
                if (array != null) {
                    instructions = Arrays.asList((String[]) array.getArray());
                }
                if (medicines != null) {
                    for (int i = 0; i < medicines.size(); i++) {
                        Medicine medicine = getMedicine(medicines.get(i));
                        medicine.setInstruction(instructions.get(i));
                        med.add(medicine);
                    }
                }
                visit.setMedicines(med);

                toReturn.add(visit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public List<Visit> getPastVisits(Integer patientId) {
        List<Visit> toReturn = new ArrayList<>();
        //String sql = "SELECT * FROM wizyty WHERE termin_wizyty > CURRENT_DATE AND termin_wizyty< CURRENT_TIMESTAMP + (INTERVAL '7 DAYS') AND lekarz=" + doctorId;
        String sql = "SELECT * FROM wizyty_info WHERE termin_wizyty < CURRENT_TIMESTAMP AND pacjent=" + patientId + "ORDER BY 4 DESC ";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Visit visit = new Visit();
                visit.setId(resultSet.getInt(1));
                visit.setPatient(getPerson(resultSet.getInt(2)));
                visit.setSpecialization(getSpecialization(resultSet.getInt(3)));
                visit.setDoctor(getPerson(resultSet.getInt(4)));
                visit.setStart(resultSet.getTimestamp(5));
                visit.setEnd(resultSet.getTimestamp(6));
                visit.setOffice(getOffice(resultSet.getInt(7)));
                visit.setTakenPlace(resultSet.getBoolean(8));
                visit.setNote(resultSet.getString(9));
                Array array = resultSet.getArray(10);
                if (array != null) {
                    List<String> diseasesCode = Arrays.asList((String[]) array.getArray());
                    visit.setDiseases(diseasesCode);
                }
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                array = resultSet.getArray(12);
                Array array2 = resultSet.getArray(13);
                if (array != null) {
                    List<Referral> list = new ArrayList<>();
                    List<Integer> ids = Arrays.asList((Integer[]) array.getArray());
                    List<String> desc = Arrays.asList((String[]) array2.getArray());
                    for (int i = 0; i < ids.size(); i++) {
                        Referral ref = new Referral();
                        ref.setSpecialization(getSpecialization(ids.get(i)));
                        ref.setNote(desc.get(i));
                        list.add(ref);
                    }
                    visit.setReferrals(list);
                }
                visit.setHasZwolnienie(resultSet.getBoolean(14));
                visit.setZwolnienieStart(resultSet.getTimestamp(15));
                visit.setZwolnienieEnd(resultSet.getTimestamp(16));
                visit.setHasRecepta(resultSet.getBoolean(17));
                array = resultSet.getArray(18);
                List<Integer> medicines = null;
                List<String> instructions = null;
                List<Medicine> med = new ArrayList<>();
                if (array != null) {
                    medicines = Arrays.asList((Integer[]) array.getArray());
                }
                array = resultSet.getArray(19);
                if (array != null) {
                    instructions = Arrays.asList((String[]) array.getArray());
                }
                if (medicines != null) {
                    for (int i = 0; i < medicines.size(); i++) {
                        Medicine medicine = getMedicine(medicines.get(i));
                        medicine.setInstruction(instructions.get(i));
                        med.add(medicine);
                    }
                }
                visit.setMedicines(med);

                toReturn.add(visit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public List<Exertion> getDayExertions(Integer nurseId, LocalDate date) {
        List<Exertion> toReturn = new ArrayList<>();
        String sql = "SELECT * FROM zabiegi_pielegniarskie WHERE termin_zabiegu > '" + date + " 00:00' AND termin_zabiegu <= '" + date + " 23:59' AND pielegniarka_arz=" + nurseId;
        System.out.println(sql);
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Exertion exertion = new Exertion();
                exertion.setId(resultSet.getInt(1));
                exertion.setPatient(getPerson(resultSet.getInt(2)));
                exertion.setNurse(getPerson(resultSet.getInt(3)));
                exertion.setStart(resultSet.getTimestamp(4));
                exertion.setOffice(getOffice(resultSet.getInt(5)));
                exertion.setTakenPlace(resultSet.getBoolean(6));
                exertion.setNote(resultSet.getString(7));
                toReturn.add(exertion);
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
    public List<Specialization> getDoctorsSpecialization(Integer doctorId) {
        List<Specialization> toRet = new ArrayList<>();
        String sql = "SELECT * FROM lekarze_specjalizacje WHERE id_lekarza=" + doctorId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet.add(getSpecialization(resultSet.getInt(2)));
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
                "id NOT IN (SELECT lekarz FROM wizyty WHERE (" + from + "<= termin_wizyty AND " + to + " > termin.wizyty) OR " + from + " > termin_wizyty AND " + from + " < koniec_wizyty)" + "');";
        //SELECT lekarz FROM wizyty WHERE termin_wizyty>= '2020-05-25 12:30:00' AND koniec_wizyty<='2020-05-25 13:30:00'
        //(NEW.termin_wizyty <= termin_wizyty AND NEW.koniec_wizyty > termin_wizyty) OR
        //		(NEW.termin_wizyty > termin_wizyty AND NEW.termin_wizyty < koniec_wizyty)
        //		)
        //(from <= termin_wizyty AND to > termin.wizyty) OR from > termin_wizyty AND from < koniec_wizyty)
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
        String sql = "SELECT id_pracownika, COALESCE(count, 0) FROM pracownicy LEFT OUTER JOIN (SELECT lekarz, count(id_wizyty) FROM wizyty WHERE pacjent=" + patientId +
                " AND lekarz IN (SELECT id_lekarza FROM lekarze_specjalizacje WHERE id_specjalizacji = " + specializationId +
                ") GROUP BY lekarz) AS s1 ON s1.lekarz = pracownicy.id_pracownika WHERE id_pracownika IN (SELECT id_lekarza FROM lekarze_specjalizacje WHERE id_specjalizacji = " +
                specializationId + ") AND id_pracownika NOT IN (SELECT lekarz FROM wizyty WHERE ('" + from + "'<= termin_wizyty AND '" + to + "' > termin_wizyty) OR ('" + from + "' > termin_wizyty AND '" + from + "' < koniec_wizyty))" + " AND status_zatrudnienia = true ORDER BY 2 DESC;";
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
    public List<Person> getAvailableNursesAtTimeSortedByPatient(LocalDateTime freeFrom, LocalDateTime freeTo, Integer patientId) {

        List<Person> toRet = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String from = freeFrom.format(formatter);
        String to = freeTo.format(formatter);
        String sql = "SELECT pracownicy.id_pracownika, COALESCE(s.count, 0) FROM pracownicy LEFT OUTER JOIN zabiegi_pielegniarskie ON zabiegi_pielegniarskie.pielegniarka_arz=pracownicy.id_pracownika\n" +
                "LEFT OUTER JOIN (SELECT id_pracownika, COUNT(id_zabiegu) AS count FROM pracownicy LEFT OUTER JOIN zabiegi_pielegniarskie ON zabiegi_pielegniarskie.pielegniarka_arz=pracownicy.id_pracownika " +
                "WHERE etat='PIELEGNIARKA_ARZ' AND id_pracownika NOT IN (SELECT pielegniarka_arz FROM zabiegi_pielegniarskie WHERE ('" + from + "'<= termin_zabiegu AND '" + to + "' > termin_zabiegu)) AND pacjent=" + patientId + " GROUP BY id_pracownika) AS s ON s.id_pracownika = pracownicy.id_pracownika" +
                " WHERE etat='PIELEGNIARKA_ARZ' AND pracownicy.id_pracownika NOT IN (SELECT pielegniarka_arz FROM zabiegi_pielegniarskie WHERE ('" + from + "'<= termin_zabiegu AND '" + to + "' > termin_zabiegu)) AND status_zatrudnienia=true GROUP BY pracownicy.id_pracownika, s.count ORDER BY 2 DESC;";
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
    public List<Office> getAvailableOfficesAtTime(LocalDateTime freeFrom, LocalDateTime freeTo) {
        //SELECT lekarz FROM wizyty WHERE ('"+from +"'<= termin_wizyty AND '"+to+"' > termin_wizyty) OR '"+from+"' > termin_wizyty AND '"+from+"' < koniec_wizyty
        List<Office> toRet = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String from = freeFrom.format(formatter);
        String to = freeTo.format(formatter);
        String sql = "SELECT * FROM gabinety_typy WHERE typ='LEKARSKI' AND gabinet NOT IN (SELECT gabinet FROM wizyty WHERE('" + from + "'<= termin_wizyty AND '" + to + "' > termin_wizyty) OR ('" + from + "' > termin_wizyty AND '" + from + "' < koniec_wizyty));";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Office office = new Office();
                office.setId(resultSet.getInt(1));
                office.setType(resultSet.getString(2));
                toRet.add(office);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public List<Office> getAvailableNurseOffices(LocalDateTime freeFrom, LocalDateTime freeTo) {
        List<Office> toRet = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String from = freeFrom.format(formatter);
        String to = freeTo.format(formatter);
        String sql = "SELECT * FROM gabinety_typy WHERE typ='ZABIEGOWY' AND gabinet NOT IN (SELECT gabinet FROM zabiegi_pielegniarskie WHERE('" + from + "'<= termin_zabiegu AND '" + to + "' > termin_zabiegu));";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Office office = new Office();
                office.setId(resultSet.getInt(1));
                office.setType(resultSet.getString(2));
                toRet.add(office);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public List<Office> getAvailableOfficesAtTimeSortedByDoctor(int doctorId, LocalDateTime freeFrom, LocalDateTime freeTo) {
        //SELECT gabinety_typy.gabinet, COALESCE(s.count, 0) FROM gabinety_typy LEFT OUTER JOIN (SELECt gabinet, COUNT(id_wizyty) AS count FROM wizyty WHERE lekarz=4 GROUP BY gabinet) AS s ON s.gabinet=gabinety_typy.gabinet WHERE gabinet IN();
        List<Office> toRet = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String from = freeFrom.format(formatter);
        String to = freeTo.format(formatter);
        String sql = "SELECT gabinety_typy.gabinet, COALESCE(s.count, 0), gabinety_typy.typ FROM gabinety_typy LEFT OUTER JOIN (SELECt gabinet, COUNT(id_wizyty) AS count FROM wizyty WHERE lekarz=" + doctorId + " GROUP BY gabinet) AS s ON s.gabinet=gabinety_typy.gabinet WHERE gabinety_typy.gabinet IN" +
                "(SELECT gabinet FROM gabinety_typy WHERE typ='LEKARSKI' AND gabinet NOT IN (SELECT gabinet FROM wizyty WHERE('" + from + "'<= termin_wizyty AND '" + to + "' > termin_wizyty) OR ('" + from + "' > termin_wizyty AND '" + from + "' < koniec_wizyty))) ORDER BY 2 DESC;";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Office office = new Office();
                office.setId(resultSet.getInt(1));
                office.setType(resultSet.getString(3));
                toRet.add(office);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public List<Office> getAvailableOfficesAtTimeSortedByNurse(LocalDateTime freeFrom, LocalDateTime freeTo, int nurseId) {
        List<Office> toRet = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String from = freeFrom.format(formatter);
        String to = freeTo.format(formatter);
        String sql = "SELECT gabinety_typy.gabinet, COALESCE(s.count, 0), gabinety_typy.typ FROM gabinety_typy LEFT OUTER JOIN (SELECt gabinet, COUNT(id_zabiegu) AS count FROM zabiegi_pielegniarskie WHERE pielegniarka_arz=" + nurseId + " GROUP BY gabinet) AS s ON s.gabinet=gabinety_typy.gabinet WHERE gabinety_typy.gabinet IN" +
                "(SELECT gabinet FROM gabinety_typy WHERE typ='ZABIEGOWY' AND gabinet NOT IN (SELECT gabinet FROM zabiegi_pielegniarskie WHERE('" + from + "'<= termin_zabiegu AND '" + to + "' > termin_zabiegu))) ORDER BY 2 DESC;";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Office office = new Office();
                office.setId(resultSet.getInt(1));
                office.setType(resultSet.getString(3));
                toRet.add(office);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public List<Person> getDoctorsVisitCount(LocalDate from, LocalDate to) {
        List<Person> toRet = new ArrayList<>();
        String sql = "SELECT dane_osob.imie, dane_osob.nazwisko, COALESCE(s.count, 0), dane_osob.id FROM dane_osob " +
                "LEFT OUTER JOIN (SELECT id, COUNT(id_wizyty) FROM dane_osob LEFT OUTER JOIN wizyty ON wizyty.lekarz=dane_osob.id " +
                "WHERE dane_osob.id IN (SELECT id_pracownika FROM pracownicy WHERE etat='LEKARZ') " +
                "AND termin_wizyty<'" + to + " 23:59' AND termin_wizyty>'" + from + " 00:00' GROUP BY id, imie, nazwisko) AS s ON s.id = dane_osob.id " +
                "WHERE dane_osob.id IN (SELECT id_pracownika FROM pracownicy WHERE etat='LEKARZ') ORDER BY 3 DESC";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Person person = new Person();
                person.setName(resultSet.getString(1));
                person.setLastName(resultSet.getString(2));
                person.setVisitCount(resultSet.getInt(3));
                person.setId(resultSet.getInt(4));
                toRet.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public List<Person> getNursesVisitCount(LocalDate from, LocalDate to) {
        List<Person> toRet = new ArrayList<>();
        String sql = "SELECT dane_osob.imie, dane_osob.nazwisko, COALESCE(s.count, 0) FROM dane_osob " +
                "LEFT OUTER JOIN (SELECT id, COUNT(id_zabiegu) FROM dane_osob LEFT OUTER JOIN zabiegi_pielegniarskie ON zabiegi_pielegniarskie.pielegniarka_arz=dane_osob.id " +
                "WHERE dane_osob.id IN (SELECT id_pracownika FROM pracownicy WHERE etat='PIELEGNIARKA_ARZ') " +
                "AND termin_zabiegu<'" + to + " 23:59' AND termin_zabiegu>'" + from + " 00:00' GROUP BY id, imie, nazwisko) AS s ON s.id = dane_osob.id " +
                "WHERE dane_osob.id IN (SELECT id_pracownika FROM pracownicy WHERE etat='PIELEGNIARKA_ARZ') ORDER BY 3 DESC";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Person person = new Person();
                person.setName(resultSet.getString(1));
                person.setLastName(resultSet.getString(2));
                person.setVisitCount(resultSet.getInt(3));
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
    public Visit getNextVisit(Integer doctorId) {
        Visit visit = new Visit();
        String sql = "SELECT * FROM wizyty_info WHERE termin_wizyty > CURRENT_DATE AND lekarz=" + doctorId + " ORDER BY 5 LIMIT 1;\n";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                visit.setId(resultSet.getInt(1));
                visit.setPatient(getPerson(resultSet.getInt(2)));
                visit.setSpecialization(getSpecialization(resultSet.getInt(3)));
                visit.setDoctor(getPerson(resultSet.getInt(4)));
                visit.setStart(resultSet.getTimestamp(5));
                visit.setEnd(resultSet.getTimestamp(6));
                visit.setOffice(getOffice(resultSet.getInt(7)));
                visit.setTakenPlace(resultSet.getBoolean(8));
                visit.setNote(resultSet.getString(9));
                Array array = resultSet.getArray(10);
                if (array != null) {
                    List<String> diseasesCode = Arrays.asList((String[]) array.getArray());
                    visit.setDiseases(diseasesCode);
                }
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                array = resultSet.getArray(12);
                Array array2 = resultSet.getArray(13);
                if (array != null) {
                    List<Referral> list = new ArrayList<>();
                    List<Integer> ids = Arrays.asList((Integer[]) array.getArray());
                    List<String> desc = Arrays.asList((String[]) array2.getArray());
                    for (int i = 0; i < ids.size(); i++) {
                        Referral ref = new Referral();
                        ref.setSpecialization(getSpecialization(ids.get(i)));
                        ref.setNote(desc.get(i));
                        list.add(ref);
                    }
                    visit.setReferrals(list);
                }
                visit.setHasZwolnienie(resultSet.getBoolean(14));
                visit.setZwolnienieStart(resultSet.getTimestamp(15));
                visit.setZwolnienieEnd(resultSet.getTimestamp(16));
                visit.setHasRecepta(resultSet.getBoolean(17));
                array = resultSet.getArray(18);
                List<Integer> medicines = null;
                List<String> instructions = null;
                List<Medicine> med = new ArrayList<>();
                if (array != null) {
                    medicines = Arrays.asList((Integer[]) array.getArray());
                }
                array = resultSet.getArray(19);
                if (array != null) {
                    instructions = Arrays.asList((String[]) array.getArray());
                }
                if (medicines != null) {
                    for (int i = 0; i < medicines.size(); i++) {
                        Medicine medicine = getMedicine(medicines.get(i));
                        medicine.setInstruction(instructions.get(i));
                        med.add(medicine);
                    }
                }
                visit.setMedicines(med);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return visit;
    }

    @Override
    public Disease getDisease(String code) {
        Disease toReturn = new Disease();
        String sql = "SELECT * FROM dolegliwosci WHERE kod_icd10='" + code + "';";
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
    public Medicine getMedicine(Integer medicineId) {
        Medicine toReturn = new Medicine();
        String sql = "SELECT * FROM produkty WHERE id_produktu=" + medicineId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toReturn.setId(resultSet.getInt(1));
                toReturn.setName(resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public Office getOffice(Integer officeId) {
        Office toReturn = new Office();
        String sql = "SELECT * FROM gabinety_typy WHERE gabinet=" + officeId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toReturn.setId(resultSet.getInt(1));
                toReturn.setType(resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public void updateVisit(Visit visit) {
        //SELECT insert_wizyta(1, true, 'test', array['A00', 'A01', 'A02'], true, 1, 'gupi jest', true, CURRENT_DATE, '2020-05-20', true, array[1, 2, 3], array['ulotka', 'ulotka', 'ulotka']);
        String choroby = "array[]::character(5)[]";
        String lekiId = "null";
        String instructions = "null";
        String specId = "null";
        String desc = "null";
        if (visit.getDiseases() != null && visit.getDiseases().size() != 0) {
            choroby = "array[";
            for (String s : visit.getDiseases()) {
                choroby += "'" + s + "'" + ", ";
            }
            choroby = choroby.substring(0, choroby.length() - 2);
            choroby += "]";
        }
        if (visit.hasRecepta() && visit.getMedicines().size() != 0) {
            lekiId = "array[";
            for (Medicine s : visit.getMedicines()) {
                lekiId += s.getId() + ", ";
            }
            lekiId = lekiId.substring(0, lekiId.length() - 2);
            lekiId += "]";
        }
        if (visit.hasRecepta() && visit.getMedicines().size() != 0) {
            instructions = "array[";
            for (Medicine s : visit.getMedicines()) {
                instructions += "'" + s.getInstruction() + "'" + ", ";
            }
            instructions = instructions.substring(0, instructions.length() - 2);
            instructions += "]";
        }
        if (visit.hasSkierowanie() && visit.getReferrals().size() > 0) {
            specId = "array[";
            desc = "array[";
            for (Referral ref : visit.getReferrals()) {
                specId += ref.getSpecialization().getId() + ", ";
                if (ref.getNote() == null) {
                    ref.setNote("");
                }
                desc += "'" + ref.getNote() + "'" + ", ";
            }
            specId = specId.substring(0, specId.length() - 2);
            desc = desc.substring(0, desc.length() - 2);
            specId += "]";
            desc += "]";
        }
        if (visit.getZwolnienieStart() == null) {
            visit.setZwolnienieStart(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
        }
        if (visit.getZwolnienieEnd() == null) {
            visit.setZwolnienieEnd(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
        }
        if (visit.getNote() == null) {
            visit.setNote("");
        }
        String sql = "SELECT insert_wizyta(" + visit.getId() + ", " + visit.hasTakenPlace() + ", '" + visit.getNote() + "', " + choroby + ", " + visit.hasSkierowanie() + ", " + specId + ", " + desc + ", " + visit.hasZwolnienie() + ", '" + visit.getZwolnienieStart() + "', '" + visit.getZwolnienieEnd() + "', " + visit.hasRecepta() + ", " + lekiId + ", " + instructions + ");";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePerson(Person person) {
        String sql = "UPDATE dane_osob SET imie = '" + person.getName() + "', nazwisko = '" + person.getLastName() + "', data_urodzenia = '" + person.getDateOfBirth() + "', email = '" + person.getEmail() + "', telefon = '" + person.getPhoneNumber() + "' WHERE id=" + person.getId() + "";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateExertion(Exertion exertion) {
        String sql = "UPDATE zabiegi_pielegniarskie SET odbyl_sie = " + exertion.isTakenPlace() + ", notatka='" + exertion.getNote() + "' WHERE id_zabiegu=" + exertion.getId();
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void newVisit(Visit visit) {
        String sql = "SELECT new_wizyta(" + visit.getPatient().getId() + ", " + visit.getDoctor().getId() + ", '" + visit.getStart() + "', '" + visit.getEnd() + "', " + visit.getOffice().getId() + ", " + visit.getSpecialization().getId() + ");";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newExertion(Exertion exertion) {
        String sql = "INSERT INTO zabiegi_pielegniarskie (pacjent, pielegniarka_arz, termin_zabiegu, gabinet, notatka) VALUES (" + exertion.getPatient().getId() + ", " + exertion.getNurse().getId() + ", '" + exertion.getStart() + "', " + exertion.getOffice().getId() + ", '" + exertion.getNote() + "')";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Integer addPerson(Person person) {
        String sql = "SELECT add('" + person.getName() + "', '" + person.getLastName() + "', '" + person.getPesel() + "', '" + person.getDateOfBirth() + "', '" + person.getPhoneNumber() + "', '" + person.getEmail() + "')";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void addSpecialization(Integer doctorId, Integer specializationId) {
        String sql = "INSERT INTO lekarze_specjalizacje VALUES (" + doctorId + ", " + specializationId + ")";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Integer> getAllMedicines() {
        Map<String, Integer> toRet = new HashMap<>();
        String sql = "SELECT * FROM produkty;";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet.put(resultSet.getString(2), resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Map<String, Integer> getAllSpecializations() {
        Map<String, Integer> toRet = new HashMap<>();
        String sql = "SELECT * FROM specjalizacje;";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet.put(resultSet.getString(2), resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public List<Worker> getAllWorkers() {
        List<Worker> toRet = new ArrayList<>();
        String sql = "SELECT * FROM pracownicy_info";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Worker worker = new Worker();
                worker.setId(resultSet.getInt(1));
                worker.setName(resultSet.getString(2));
                worker.setLastName(resultSet.getString(3));
                worker.setPesel(resultSet.getString(4));
                worker.setDateOfBirth(resultSet.getDate(5));
                worker.setPhoneNumber(resultSet.getString(6));
                worker.setEmail(resultSet.getString(7));
                String role = resultSet.getString(8);
                switch (role) {
                    case "LEKARZ":
                        worker.setRole(Roles.LEKARZ);
                        break;
                    case "PIELEGNIARKA_ARZ":
                        worker.setRole(Roles.PIELEGNIARKA_ARZ);
                        break;
                    case "ADMINISTRACJA":
                        worker.setRole(Roles.ADMINISTRACJA);
                        break;
                    case "RECEPCJONISTKA_TA":
                        worker.setRole(Roles.RECEPCJONISTKA_TA);
                        break;
                    case "LABORANT_KA":
                        worker.setRole(Roles.LABORANT_KA);
                        break;
                    case "DYREKCJA":
                        worker.setRole(Roles.DYREKCJA);
                        break;
                    default:
                        worker.setRole(Roles.OBSŁUGA_TECHNICZNA);
                }
                worker.setHiredFrom(resultSet.getDate(9));
                worker.setActive(resultSet.getBoolean(10));
                toRet.add(worker);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;

    }

    @Override
    public Integer getTotalVisitCount(Integer doctorId) {
        Integer toRet = null;
        String sql = "SELECT COUNT(id_wizyty) FROm wizyty WHERE lekarz=" + doctorId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Integer getTotalExertionCount(Integer workerId) {
        Integer toRet = null;
        String sql = "SELECT count(id_zabiegu) FROM zabiegi_pielegniarskie WHERE pielegniarka_arz=" + workerId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Integer getTotalPrescriptionCount(Integer doctorId) {
        Integer toRet = null;
        String sql = "SELECT count(id_recepty) FROM recepty LEFT OUTER JOIN wizyty ON recepty.wizyta=wizyty.id_wizyty WHERE lekarz=" + doctorId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }


    @Override
    public Integer getVisitCount(LocalDate date1, LocalDate date2) {
        Integer toRet = null;
        String sql = "SELECT count(id_wizyty) FROM wizyty WHERE termin_wizyty>='" + Timestamp.valueOf(date1.atStartOfDay()) + "' AND termin_wizyty<='" + Timestamp.valueOf(date2.atStartOfDay()) + "'";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }


    @Override
    public Integer getExertionCount(LocalDate date1, LocalDate date2) {
        Integer toRet = null;
        String sql = "SELECT count(id_zabiegu) FROM zabiegi_pielegniarskie WHERE termin_zabiegu>='" + Timestamp.valueOf(date1.atStartOfDay()) + "' AND termin_zabiegu<='" + Timestamp.valueOf(date2.atStartOfDay()) + "'";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Integer getPrescriptionCount(LocalDate date1, LocalDate date2) {
        Integer toRet = null;
        String sql = "SELECT COUNT(id_recepty) FROM recepty LEFT OUTER JOIN wizyty ON recepty.wizyta=wizyty.id_wizyty WHERE termin_wizyty>='" + Timestamp.valueOf(date1.atStartOfDay()) + "'AND termin_wizyty<='" + Timestamp.valueOf(date2.atStartOfDay()) + "'";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Integer getPrescriptionCount(LocalDate date1, LocalDate date2, Integer doctorId) {
        Integer toRet = null;
        String sql = "SELECT COUNT(id_recepty) FROM recepty LEFT OUTER JOIN wizyty ON recepty.wizyta=wizyty.id_wizyty WHERE termin_wizyty>='" + Timestamp.valueOf(date1.atStartOfDay()) + "'AND termin_wizyty<='" + Timestamp.valueOf(date2.atStartOfDay()) + "' AND lekarz=" + doctorId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Integer getZwolnienieCount(LocalDate date1, LocalDate date2) {
        Integer toRet = null;
        String sql = "SELECT COUNT(id_zwolnienia) FROM zwolnienia LEFT OUTER JOIN wizyty ON zwolnienia.wizyta=wizyty.id_wizyty WHERE termin_wizyty>='" + Timestamp.valueOf(date1.atStartOfDay()) + "'AND termin_wizyty<='" + Timestamp.valueOf(date2.atStartOfDay()) + "'";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Integer getZwolnienieCount(LocalDate date1, LocalDate date2, Integer doctorId) {
        Integer toRet = null;
        String sql = "SELECT COUNT(id_zwolnienia) FROM zwolnienia LEFT OUTER JOIN wizyty ON zwolnienia.wizyta=wizyty.id_wizyty WHERE termin_wizyty>='" + Timestamp.valueOf(date1.atStartOfDay()) + "'AND termin_wizyty<='" + Timestamp.valueOf(date2.atStartOfDay()) + "' AND lekarz=" + doctorId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Integer getSkierowanieCount(LocalDate date1, LocalDate date2) {
        Integer toRet = null;
        String sql = "SELECT COUNT(id_skierowania) FROM skierowania LEFT OUTER JOIN wizyty ON skierowania.wizyta=wizyty.id_wizyty WHERE termin_wizyty>='" + Timestamp.valueOf(date1.atStartOfDay()) + "'AND termin_wizyty<='" + Timestamp.valueOf(date2.atStartOfDay()) + "'";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Integer getSkierowanieCount(LocalDate date1, LocalDate date2, Integer doctorId) {
        Integer toRet = null;
        String sql = "SELECT COUNT(id_skierowania) FROM skierowania LEFT OUTER JOIN wizyty ON skierowania.wizyta=wizyty.id_wizyty WHERE termin_wizyty>='" + Timestamp.valueOf(date1.atStartOfDay()) + "'AND termin_wizyty<='" + Timestamp.valueOf(date2.atStartOfDay()) + "' AND lekarz=" + doctorId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Integer getLongestZwolnienie(LocalDate date1, LocalDate date2) {
        Integer toRet = null;
        String sql = "SELECT id_zwolnienia, do_kiedy-od_kiedy FROm zwolnienia WHERE od_kiedy>='" + Timestamp.valueOf(date1.atStartOfDay()) + "' AND do_kiedy<='" + Timestamp.valueOf(date2.atStartOfDay()) + "' ORDER BY 2 DESC LIMIT 1";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Integer getLongestZwolnienie(LocalDate date1, LocalDate date2, Integer doctorId) {
        Integer toRet = null;
        String sql = "SELECT id_zwolnienia, do_kiedy-od_kiedy FROM zwolnienia LEFT OUTER JOIN wizyty ON wizyty.id_wizyty=zwolnienia.wizyta WHERE od_kiedy>='" + Timestamp.valueOf(date1.atStartOfDay()) + "' AND do_kiedy<='" + Timestamp.valueOf(date2.atStartOfDay()) + "' AND lekarz = " + doctorId + " ORDER BY 2 DESC LIMIT 1";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Integer getReceptaId(Integer visitId) {
        Integer toRet = null;
        String sql = "SELECT id_recepty FROM recepty WHERE wizyta = " + visitId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Integer getZwolnienieId(Integer visitId) {
        Integer toRet = null;
        String sql = "SELECT id_zwolnienia FROM zwolnienia WHERE wizyta = " + visitId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public String getSkierowanieIds(Integer visitId) {
        String toRet = "";
        String sql = "SELECT id_skierowania FROM skierowania WHERE wizyta = " + visitId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet += resultSet.getInt(1) + " ";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Office getMostUsedOffice(LocalDate date1, LocalDate date2) {
        //
        Office toRet = null;
        String sql = "SELECT gabinet, COUNT(id_wizyty) FROM recepty LEFT OUTER JOIN wizyty ON recepty.wizyta=wizyty.id_wizyty WHERE termin_wizyty>='" + Timestamp.valueOf(date1.atStartOfDay()) + "' AND termin_wizyty<='" + Timestamp.valueOf(date2.atStartOfDay()) + "' GROUP BY gabinet ORDER BY 2 DESC LIMIT 1";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = getOffice(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Office getMostUsedOffice(LocalDate date1, LocalDate date2, Integer doctorId) {
        Office toRet = null;
        String sql = "SELECT gabinet, COUNT(id_wizyty) FROM recepty LEFT OUTER JOIN wizyty ON recepty.wizyta=wizyty.id_wizyty WHERE termin_wizyty>'" + Timestamp.valueOf(date1.atStartOfDay()) + "' AND termin_wizyty<'" + Timestamp.valueOf(date2.atStartOfDay()) + "' AND lekarz = " + doctorId + " GROUP BY gabinet ORDER BY 2 DESC LIMIT 1";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = getOffice(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Medicine getMostCommonMedicine(LocalDate date1, LocalDate date2) {
        //
        Medicine toRet = null;
        String sql = "SELECT id_produktu, COUNT(id_wizyty) FROm recepty LEFT OUTER JOIN recepty_produkty_dawki ON recepty.id_recepty = recepty_produkty_dawki.id_recepty LEFT OUTER JOIN wizyty ON recepty.wizyta = wizyty.id_wizyty WHERE termin_wizyty>'" + Timestamp.valueOf(date1.atStartOfDay()) + "' AND termin_wizyty<'" + Timestamp.valueOf(date2.atStartOfDay()) + "' GROUP BY id_produktu ORDER BY 2 DESC LIMIT 1";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = getMedicine(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public Medicine getMostCommonMedicine(LocalDate date1, LocalDate date2, Integer doctorId) {
        Medicine toRet = null;
        String sql = "SELECT id_produktu, COUNT(id_wizyty) FROm recepty LEFT OUTER JOIN recepty_produkty_dawki ON recepty.id_recepty = recepty_produkty_dawki.id_recepty LEFT OUTER JOIN wizyty ON recepty.wizyta = wizyty.id_wizyty WHERE termin_wizyty>'" + Timestamp.valueOf(date1.atStartOfDay()) + "' AND termin_wizyty<'" + Timestamp.valueOf(date2.atStartOfDay()) + "' AND lekarz = " + doctorId + " GROUP BY id_produktu ORDER BY 2 DESC LIMIT 1";
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = getMedicine(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    @Override
    public void fireWorker(Integer workerId) {
        String sql = "UPDATE pracownicy SET status_zatrudnienia=false WHERE id_pracownika=" + workerId;
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelVisit(Integer visitId) {
        String sql = "DELETE FROM wizyty WHERE id_wizyty=" + visitId;
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void cancelExertion(Integer exertionId) {
        String sql = "DELETE FROM zabiegi_pielegniarskie WHERE id_zabiegu=" + exertionId;
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isNonFiredWorker(String pesel) {
        String sql = "SELECT EXISTS (SELECT * FROM dane_osob LEFT OUTER JOIN pracownicy ON pracownicy.id_pracownika=dane_osob.id WHERE status_zatrudnienia=true AND pesel = '" + pesel + "')";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isFiredWorker(String pesel) {
        String sql = "SELECT EXISTS (SELECT * FROM dane_osob LEFT OUTER JOIN pracownicy ON pracownicy.id_pracownika=dane_osob.id WHERE status_zatrudnienia=false AND pesel = '" + pesel + "')";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getPrettyNameByPesel(String pesel) {
        String sql = "SELECT imie, nazwisko FROM dane_osob WHERE pesel = '" + pesel + "'";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getString(1) + " " + resultSet.getString(2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void rehire(String pesel) {
        String sql = "UPDATE pracownicy SET status_zatrudnienia = true WHERE id_pracownika = (SELECT id FROM dane_osob WHERE pesel = '" + pesel + "')";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isInDb(String pesel) {
        String sql = "SELECT EXISTS (SELECT * FROM dane_osob WHERE pesel='" + pesel + "')";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void addRole(Integer id, Roles role) {
        String roleStr = role.toString();
        if (roleStr.equals("OBSŁUGA_TECHNICZNA")) {
            roleStr = "OBSŁUGA TECHNICZNA";
        }
        String sql = "INSERT INTO pracownicy (id_pracownika, etat, status_zatrudnienia) VALUES (" + id + ", '" + roleStr + "', true);\n";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateMedicine(Medicine medicine) {
        String sql = "UPDATE produkty SET nazwa='" + medicine.getName() + "' WHERE id_produktu=" + medicine.getId() + "";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newMedicine(String name) {
        String sql = "INSERT INTO produkty (nazwa) VALUES ('" + name + "')";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean hasMedicine(String name) {
        String sql = "SELECT EXISTS (SELECT * FROm produkty WHERE nazwa='" + name + "')";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isPatientFree(LocalDateTime from, LocalDateTime to, Integer patientId) {
        boolean one = false;
        boolean two = false;
        String sql = "SELECT EXISTS (SELECT * FROM wizyty WHERE(('" + from + "'<= termin_wizyty AND '" + to + "' > termin_wizyty) OR ('" + from + "' > termin_wizyty AND '" + to + "' < koniec_wizyty)) AND pacjent=" + patientId + ")";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            one = resultSet.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sql = "SELECT EXISTS (SELECT * FROM zabiegi_pielegniarskie WHERE termin_zabiegu>= '" + from + "' AND termin_zabiegu < '" + to + "' AND pacjent=" + patientId + ")";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            two = resultSet.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (two || one) {
            return false;
        }
        return true;
    }

    @Override
    public boolean hasZwolnienie(LocalDate from, LocalDate to, Integer patientId, Integer visitId) {
        boolean exists = false;
        String sql = "SELECT EXISTS ( SELECT * FROM (SELECT * FROM wizyty JOIN zwolnienia ON wizyta = id_wizyty) XX" +
                "  WHERE XX.pacjent = " + patientId +
                "  AND XX.id_wizyty!=" + visitId + " AND ( ( '" + from + "' <= od_kiedy AND '" + to + "' > od_kiedy ) OR" +
                "  ( '" + from + "' > od_kiedy AND '" + to + "' < do_kiedy )));";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            exists = resultSet.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    @Override
    public Double getCredibility(Integer patientId) {
        double toRet = 0.0;
        String sql = "SELECT (CASE WHEN XX.ile_wszystkich=0 THEN 100.00" +
                "ELSE ROUND(100*XX.ile_odbytych/XX.ile_wszystkich, 2) END) AS wiarygodnosc FROM (" +
                "SELECT ds.*, COALESCE(SUM(odbyla_sie::int),0) AS ile_odbytych, COUNT(odbyla_sie) AS ile_wszystkich  FROM dane_osob ds " +
                "LEFT JOIN wizyty w ON w.pacjent = ds.id WHERE termin_wizyty::date < current_date GROUP BY id) XX WHERE XX.id=" + patientId;
        try {
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                toRet = resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;
    }

}
