package models.datamodel;

import com.avaje.ebean.Ebean;
import controllers.Utils;
import models.Employee;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Kuba on 2014-12-09.
 */
@Entity
@Table(name = "\"task\"")
public class Task extends Model {

    private TaskProperties taskProperties;
    private Attachment attachment;
    private Employee assigned;
    private List<Comment> commentsList;
    private TimeTracking timeTracking;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "task_gen", sequenceName = "task_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_gen")
    private int id;

    @Constraints.Required
    public int projectId;

    @Constraints.Required
    public String code;

    @Constraints.Required
    public String summary;

    @Constraints.Required
    public String description;

    @Constraints.Required
    public String labels;

    @Constraints.Required
    public String status;

    @Constraints.Required
    public Date created;

    @Constraints.Required
    public int assigne;

    public int getId() {
        return id;
    }

    public static Finder<Integer, Task> find = new Finder<Integer, Task>(Integer.class, Task.class);

    public static List<Task> all() {
        return find.all();
    }

    public static Task getTask(String code) {
        return Ebean.find(Task.class)
                .where()
                .eq("code", code).findUnique();
    }

    public void setTaskProperties(TaskProperties taskProperties) {
        this.taskProperties = taskProperties;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public void setTimeTracking(TimeTracking timeTracking) {
        this.timeTracking = timeTracking;
    }

    public TaskProperties getTaskProperties() {
        return taskProperties;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public Employee getAssigned() {
        if (assigned == null) {
            assigned = getAssignedFromDatabase();
        }
        return assigned;
    }

    public void setAssigned(Employee assigned) {
        this.assigned = assigned;
    }

    private Employee getAssignedFromDatabase() {
        return Ebean.find(Employee.class)
                .where()
                .eq("id", this.assigne).findUnique();
    }

    public List<Comment> getCommentsList() {
        if (commentsList == null) {
            commentsList = getCommentsFromDatabase();
        }
        return commentsList;
    }

    private List<Comment> getCommentsFromDatabase() {
        return Ebean.find(Comment.class)
                .where()
                .eq("task_id", this.id).orderBy("addeddate asc").findList();
    }

    public String getProjectName() {
        return Utils.getProjectName(this.projectId);
    }

    public TimeTracking getTimeTracking() {
        if (timeTracking == null) {
            timeTracking = getTimeTrackingFromDatabase();
        }
        return timeTracking;
    }

    public TimeTracking getTimeTrackingFromDatabase() {
        return Ebean.find(TimeTracking.class)
                .where()
                .eq("task_id", this.id).findUnique();
    }
}
