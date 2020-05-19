package Model;

import java.sql.Date;

public class Person {
    private String name;
    private String lastName;
    private String pesel;
    private Date dateOfBirth;
    private String phoneNumber;
    private String email;
    private Integer id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Imię: "+name+"\n"+"Nazwisko: "+lastName+"\n"+"Pesel: "+pesel+"\n"+"Data urodzenia: "+dateOfBirth+"\n"+"Numer telefonu: "+phoneNumber+"\n"+"Email: "+email+"\n";
    }
}
