package models.datamodel;

import com.avaje.ebean.Ebean;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by Kuba on 2014-12-09.
 */
@Entity
@Table(name="\"taskproperties\"")
public class TaskProperties extends Model {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="taskproperties_gen", sequenceName="taskproperties_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taskproperties_gen")
    private int id;

    @Constraints.Required
    public int taskId;

    @Constraints.Required
    public int type;

    @Constraints.Required
    public int reporter;

    @Constraints.Required
    public String priority;

    @Constraints.Required
    public Date deadline;

    @Constraints.Required
    public Date created;

    @Constraints.Required
    public Date updated;

    @Constraints.Required
    public Date resolved;


    public static Finder<Integer, TaskProperties> find = new Finder<Integer, TaskProperties>(Integer.class, TaskProperties.class);

    public static List<TaskProperties> all() {
        return find.all();
    }

    public static TaskProperties getTaskProperties(int taskId) {
        TaskProperties properties = Ebean.find(TaskProperties.class)
                .where()
                .eq("task_id", taskId).findUnique();

        return properties;
    }
}
