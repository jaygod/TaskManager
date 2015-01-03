package models.datamodel;

import com.avaje.ebean.Ebean;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "\"attachment\"")
public class Attachment extends Model {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "attachment_gen", sequenceName = "attatchment_id_seq", allocationSize = 1)
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