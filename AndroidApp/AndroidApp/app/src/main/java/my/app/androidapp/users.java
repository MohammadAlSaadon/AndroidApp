package my.app.androidapp;

public class users
{
    Integer userId;
    String firstName;
    String lastName;
    int phoneNumber;
    String emailAddress;


    public users() {
    }

    public users(Integer userId, String firstName, String lastName, int phoneNumber, String emailAddress) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }


    public Integer getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
