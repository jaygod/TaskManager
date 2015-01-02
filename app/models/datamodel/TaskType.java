package models.datamodel;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Kuba on 2014-12-14.
 */
@Entity
@Table(name="\"tasktype\"")
public class TaskType  extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Constraints.Required
    private String type;

    public static Finder<Integer, TaskType> find = new Finder<Integer, TaskType>(Integer.class, TaskType.class);

    public static List<TaskType> all() {
        return find.all();
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
