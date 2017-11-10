package sym.labo2;

/**
 * Created by lemdjo on 10.11.2017.
 */

public class Person {
    private String lastname;
    private String firstname;
    private String email;

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getEmail() {
        return email;
    }
}
