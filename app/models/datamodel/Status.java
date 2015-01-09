package models.datamodel;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Kuba on 2014-12-09.
 */
@Entity
@Table(name="\"status\"")
public class Status extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Constraints.Required
    private String status;

    public static Finder<Integer, Status> find = new Finder<Integer, Status>(Integer.class, Status.class);

    public static List<Status> all() {
        return find.all();
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}
