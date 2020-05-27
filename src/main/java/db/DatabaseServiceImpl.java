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
                visit.setSpecialization(getSpecialization(resultSet.getInt(3)));
                visit.setDoctor(getPerson(resultSet.getInt(4)));
                visit.setStart(resultSet.getTimestamp(5));
                visit.setEnd(resultSet.getTimestamp(6));
                visit.setOffice(getOffice(resultSet.getInt(7)));
                visit.setTakenPlace(resultSet.getBoolean(8));
                visit.setNote(resultSet.getString(9));
                Array array = resultSet.getArray(10);
                if(array!=null){
                    List<String> diseasesCode = Arrays.asList((String[]) array.getArray());
                    visit.setDiseases(diseasesCode);
                }
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                array = resultSet.getArray(12);
                Array array2 = resultSet.getArray(13);
                if(array!=null){
                    List<Referral> list = new ArrayList<>();
                    List<Integer> ids = Arrays.asList((Integer[]) array.getArray());
                    List<String> desc = Arrays.asList((String[]) array2.getArray());
                    for(int i = 0; i<ids.size(); i++){
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
                if(array!=null){
                    medicines = Arrays.asList((Integer[]) array.getArray());
                }
                array = resultSet.getArray(19);
                if(array!=null){
                    instructions = Arrays.asList((String[]) array.getArray());
                }
                if(medicines!=null){
                    for(int i = 0; i<medicines.size(); i++){
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
                visit.setSpecialization(getSpecialization(resultSet.getInt(3)));
                visit.setDoctor(getPerson(resultSet.getInt(4)));
                visit.setStart(resultSet.getTimestamp(5));
                visit.setEnd(resultSet.getTimestamp(6));
                visit.setOffice(getOffice(resultSet.getInt(7)));
                visit.setTakenPlace(resultSet.getBoolean(8));
                visit.setNote(resultSet.getString(9));
                Array array = resultSet.getArray(10);
                if(array!=null){
                    List<String> diseasesCode = Arrays.asList((String[]) array.getArray());
                    visit.setDiseases(diseasesCode);
                }
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                array = resultSet.getArray(12);
                Array array2 = resultSet.getArray(13);
                if(array!=null){
                    List<Referral> list = new ArrayList<>();
                    List<Integer> ids = Arrays.asList((Integer[]) array.getArray());
                    List<String> desc = Arrays.asList((String[]) array2.getArray());
                    for(int i = 0; i<ids.size(); i++){
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
                if(array!=null){
                    medicines = Arrays.asList((Integer[]) array.getArray());
                }
                array = resultSet.getArray(19);
                if(array!=null){
                    instructions = Arrays.asList((String[]) array.getArray());
                }
                if(medicines!=null){
                    for(int i = 0; i<medicines.size(); i++){
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
                "id NOT IN (SELECT lekarz FROM wizyty WHERE ("+from +"<= termin_wizyty AND "+to+" > termin.wizyty) OR "+from+" > termin_wizyty AND "+from+" < koniec_wizyty)" + "');";
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
                specializationId + ") AND id_pracownika NOT IN (SELECT lekarz FROM wizyty WHERE ('"+from +"'<= termin_wizyty AND '"+to+"' > termin_wizyty) OR ('"+from+"' > termin_wizyty AND '"+from+"' < koniec_wizyty))" + " ORDER BY 2 DESC;";
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
        String sql = "SELECT * FROM gabinety_typy WHERE typ='LEKARSKI' AND gabinet NOT IN (SELECT gabinet FROM wizyty WHERE('"+from +"'<= termin_wizyty AND '"+to+"' > termin_wizyty) OR ('"+from+"' > termin_wizyty AND '"+from+"' < koniec_wizyty));";
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
        String sql = "SELECT gabinety_typy.gabinet, COALESCE(s.count, 0), gabinety_typy.typ FROM gabinety_typy LEFT OUTER JOIN (SELECt gabinet, COUNT(id_wizyty) AS count FROM wizyty WHERE lekarz="+doctorId+" GROUP BY gabinet) AS s ON s.gabinet=gabinety_typy.gabinet WHERE gabinety_typy.gabinet IN"+
                "(SELECT gabinet FROM gabinety_typy WHERE typ='LEKARSKI' AND gabinet NOT IN (SELECT gabinet FROM wizyty WHERE('"+from +"'<= termin_wizyty AND '"+to+"' > termin_wizyty) OR ('"+from+"' > termin_wizyty AND '"+from+"' < koniec_wizyty))) ORDER BY 2 DESC;";
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
        Visit  visit = new Visit();
        String sql = "SELECT * FROM wizyty_info WHERE termin_wizyty > CURRENT_DATE AND lekarz="+doctorId+" ORDER BY 5 LIMIT 1;\n";
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
                if(array!=null){
                    List<String> diseasesCode = Arrays.asList((String[]) array.getArray());
                    visit.setDiseases(diseasesCode);
                }
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                visit.setHasSkierowanie(resultSet.getBoolean(11));
                array = resultSet.getArray(12);
                Array array2 = resultSet.getArray(13);
                if(array!=null){
                    List<Referral> list = new ArrayList<>();
                    List<Integer> ids = Arrays.asList((Integer[]) array.getArray());
                    List<String> desc = Arrays.asList((String[]) array2.getArray());
                    for(int i = 0; i<ids.size(); i++){
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
                if(array!=null){
                    medicines = Arrays.asList((Integer[]) array.getArray());
                }
                array = resultSet.getArray(19);
                if(array!=null){
                    instructions = Arrays.asList((String[]) array.getArray());
                }
                if(medicines!=null){
                    for(int i = 0; i<medicines.size(); i++){
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
        if(visit.getDiseases()!=null && visit.getDiseases().size()!=0){
            choroby = "array[";
            for(String s: visit.getDiseases()){
                choroby += "'"+s+"'"+", ";
            }
            choroby = choroby.substring(0, choroby.length()-2);
            choroby += "]";
        }
        if(visit.hasRecepta() && visit.getMedicines().size()!=0){
            lekiId = "array[";
            for(Medicine s: visit.getMedicines()){
                lekiId += s.getId()+", ";
            }
            lekiId = lekiId.substring(0, lekiId.length()-2);
            lekiId += "]";
        }
        if(visit.hasRecepta() && visit.getMedicines().size()!=0){
            instructions = "array[";
            for(Medicine s: visit.getMedicines()){
                instructions += "'"+s.getInstruction()+"'"+", ";
            }
            instructions = instructions.substring(0, instructions.length()-2);
            instructions += "]";
        }
        if(visit.hasSkierowanie() && visit.getReferrals().size()>0){
            specId = "array[";
            desc = "array[";
            for(Referral ref: visit.getReferrals()){
                specId += ref.getSpecialization().getId()+", ";
                if(ref.getNote()==null){
                    ref.setNote("");
                }
                desc += "'"+ref.getNote()+"'"+", ";
            }
            specId = specId.substring(0, specId.length()-2);
            desc = desc.substring(0, desc.length()-2);
            specId += "]";
            desc += "]";
        }
        if(visit.getZwolnienieStart()==null){
            visit.setZwolnienieStart(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
        }
        if(visit.getZwolnienieEnd()==null){
            visit.setZwolnienieEnd(Timestamp.valueOf(LocalDate.now().atStartOfDay()));
        }
        if(visit.getNote()==null){
            visit.setNote("");
        }
        String sql = "SELECT insert_wizyta("+visit.getId()+", "+visit.hasTakenPlace()+", '"+visit.getNote()+"', "+choroby+", "+visit.hasSkierowanie()+", "+specId+", "+desc+", "+visit.hasZwolnienie()+", '"+visit.getZwolnienieStart()+"', '"+visit.getZwolnienieEnd()+"', "+visit.hasRecepta()+", "+lekiId+", "+instructions+");";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newVisit(Visit visit) {
        String sql = "SELECT new_wizyta("+visit.getPatient().getId()+", "+visit.getDoctor().getId()+", '"+visit.getStart()+"', '"+visit.getEnd()+"', "+visit.getOffice().getId()+", "+visit.getSpecialization().getId()+");";
        System.out.println(sql);
        try {
            statement = c.createStatement();
            statement.executeQuery(sql);
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
                switch (role){
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
                toRet.add(worker);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toRet;

    }
}
