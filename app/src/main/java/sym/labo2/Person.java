package sym.labo2;

import java.io.Serializable;

/**
 * Created by lemdjo on 09.11.2017.
 */

public class Person implements Serializable {

    private String lastname;
    private String firstname;
    private String email;

    Person(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public void setLastname(String lastname) { this.lastname = lastname; }

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public void setEmail(String email) { this.email = email; }

    public String getLastname() { return lastname; }

    public String getFirstname() { return firstname;}

    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "{firstname :" + firstname + ", lastname : " + lastname + "email : " + email + "}";
    }

}