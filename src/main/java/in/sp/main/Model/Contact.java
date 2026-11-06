package in.sp.main.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int contactId;

    @NotBlank(message = "Name should be required")
    @Column
    private String firstName;

    @NotBlank(message = "Name should be required")
    @Column
    private String lastName;

    @NotBlank(message = "Work should be required")
    @Column
    private String work;

    @NotBlank(message = "Email Should be required")
    @Email(message = "Please provide a valid email address")
    @Column
    private String contactEmail;

    @NotBlank(message = "Phone number should be required")
    @Column
    private String contactPhone;

    @Column
    private String contactImageUrl;

    @Column(length = 1000)
    private String contactDescription;

    @ManyToOne
    private User user;

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactImageUrl() {
        return contactImageUrl;
    }

    public void setContactImageUrl(String contactImageUrl) {
        this.contactImageUrl = contactImageUrl;
    }

    public String getContactDescription() {
        return contactDescription;
    }

    public void setContactDescription(String contactDescription) {
        this.contactDescription = contactDescription;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactId=" + contactId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", work='" + work + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactImageUrl='" + contactImageUrl + '\'' +
                ", contactDescription='" + contactDescription + '\'' +
                ", user_Id=" + (user != null ? user.getUserId() : null) + '\'' +
                '}';
    }
}

