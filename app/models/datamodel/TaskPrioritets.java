package models.datamodel;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Kuba on 2014-12-09.
 */
@Entity
@Table(name = "\"taskprioritets\"")
public class TaskPrioritets extends Model {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "taskprioritets_gen", sequenceName = "taskpriorites_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taskprioritets_gen")
    private int id;

    @Constraints.Required
    public String priority;

    public static Finder<Integer, TaskPrioritets> find = new Finder<Integer, TaskPrioritets>(Integer.class, TaskPrioritets.class);

    public static List<TaskPrioritets> all() {
        return find.all();
    }

    public String getPriority() {
        return priority;
    }

//    public static TaskPrioritets getTaskProperties(int taskId) {
//        TaskPrioritets attachment = Ebean.find(TaskPrioritets.class)
//                .where()
//                .eq("task_id", taskId).findUnique();
//
//        return attachment;
//    }
}
