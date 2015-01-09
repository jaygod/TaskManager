package models;

import models.utils.AppException;
import models.utils.Hash;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Employee: yesnault
 * Date: 20/01/12
 */
@Entity
public class Employee extends Model {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="employee_gen", sequenceName="employee_id_seq",allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_gen")
    public Integer id;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String email;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String fullname;

    public String confirmationToken;

    @Constraints.Required
    @Formats.NonEmpty
    public boolean isAdmin;

    @Constraints.Required
    @Formats.NonEmpty
    public String passwordHash;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    @Formats.NonEmpty
    public Boolean validated = false;


    private byte[] icon;

    // -- Queries (long id, user.class)
    public static Model.Finder<Integer, Employee> find = new Model.Finder<Integer, Employee>(Integer.class, Employee.class);

    /**
     * Retrieve a user from an email.
     *
     * @param email email to search
     * @return a user
     */
    public synchronized static Employee findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    /**
     * Retrieve a user from a fullname.
     *
     * @param fullname Full name
     * @return a user
     */
    public synchronized static Employee findByFullname(String fullname) {
        return find.where().eq("fullname", fullname).findUnique();
    }

    /**
     * Retrieves a user from a confirmation token.
     *
     * @param token the confirmation token to use.
     * @return a user if the confirmation token is found, null otherwise.
     */
    public synchronized static Employee findByConfirmationToken(String token) {
        return find.where().eq("confirmationToken", token).findUnique();
    }

    /**
     * Authenticate a Employee, from a email and clear password.
     *
     * @param email         email
     * @param clearPassword clear password
     * @return Employee if authenticated, null otherwise
     * @throws AppException App Exception
     */
    public static Employee authenticate(String email, String clearPassword) throws AppException {

        // get the user with email only to keep the salt password
        Employee user = find.where().eq("email", email).findUnique();
        if (user != null) {
            // get the hash password from the salt + clear password
            if (Hash.checkPassword(clearPassword, user.passwordHash)) {
                return user;
            }
        }
        return null;
    }

    public void changePassword(String password) throws AppException {
        this.passwordHash = Hash.createPassword(password);
        this.save();
    }

    /**
     * Confirms an account.
     *
     * @return true if confirmed, false otherwise.
     * @throws AppException App Exception
     */
    public static boolean confirm(Employee user) throws AppException {
        if (user == null) {
            return false;
        }

        user.confirmationToken = null;
        user.validated = true;
        user.dateCreation = new Date();
        user.dateCreation.setTime(System.currentTimeMillis());
        user.save();
        return true;
    }

    public static List<Employee> all() {
        return find.all();
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public String getImageData() {
        if (icon != null) {
            sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            String encode = encoder.encode(icon);
            return encode;
        }
        return "";
    }
}
