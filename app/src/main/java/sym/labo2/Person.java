package sym.labo2;

import java.io.Serializable;

/**
 * Person class used to test serialization
 */
public class Person implements Serializable {
    private String name;
    private String firstname;
    private String middlename;
    private String gender;
    private String phone;

    Person(String firstname, String middlename, String name, String gender, String phone) {
        this.firstname = firstname;
        this.middlename = middlename;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
    }

    Person(String firstname, String name, String gender, String phone) {
        this.firstname = firstname;
        this.middlename = "";
        this.name = name;
        this.gender = gender;
        this.phone = phone;
    }

    public void setName(String lastname) { this.name = lastname; }

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public void setMiddlename(String firstname) { this.firstname = firstname; }

    public void setGender(String gender) { this.gender = gender; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getName() { return name; }

    public String getFirstname() { return firstname; }

    public String getMiddlename() { return middlename; }

    public String getGender() { return gender; }

    public String getPhone() { return phone; }
}
