package models.datamodel;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

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

    public int attachments;

    public int getId() {
        return id;
    }


    @Entity
    @Table(name="\"attatchment\"")
    public static class Attachment extends Model {

        @Id
        @Column(name = "id")
        @SequenceGenerator(name="attatchment_gen", sequenceName="attatchment_id_seq", allocationSize=1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attatchment_gen")
        private int id;

        @Constraints.Required
        public int taskId;

        @Constraints.Required
        public byte[] data;

        @Constraints.Required
        public String name;

        @Constraints.Required
        public long size;

        public int getId() {
            return id;
        }
    }
}
