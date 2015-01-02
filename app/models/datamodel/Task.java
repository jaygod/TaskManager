package models.datamodel;

import com.avaje.ebean.Ebean;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Kuba on 2014-12-09.
 */
@Entity
@Table(name="\"task\"")
public class Task extends Model {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="task_gen", sequenceName="task_id_seq", allocationSize=1)
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

    public int attachment;

    public int getId() {
        return id;
    }

    public static Finder<Integer, Task> find = new Finder<Integer, Task>(Integer.class, Task.class);

    public static List<Task> all() {
        return find.all();
    }

    public static Task getTask(String code) {
       Task task = Ebean.find(Task.class)
                .where()
                .eq("code", code).findUnique();

        return task;
    }

    @Entity
    @Table(name="\"attachment\"")
    public static class Attachment extends Model {

        @Id
        @Column(name = "id")
        @SequenceGenerator(name="attachment_gen", sequenceName="attatchment_id_seq", allocationSize=1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_gen")
        private int id;

        @Constraints.Required
        public int taskId;

        @Constraints.Required
        public byte[] data;

        @Constraints.Required
        public String name;

        @Constraints.Required
        public long size;

        @Constraints.Required
        public String content_type;

        public int getId() {
            return id;
        }

        public static Finder<Integer, Attachment> find = new Finder<Integer, Attachment>(Integer.class, Attachment.class);

        public static List<Attachment> all() {
            return find.all();
        }

        public static Attachment getAttachment(int taskId) {
            Attachment attachment = Ebean.find(Attachment.class)
                    .where()
                    .eq("task_id", taskId).findUnique();

            return attachment;
        }
    }
}
