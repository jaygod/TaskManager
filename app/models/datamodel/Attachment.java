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
    private byte[] data;

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

    public static List<Attachment> getAttachment(int taskId) {
        return Ebean.find(Attachment.class)
                .where()
                .eq("task_id", taskId).findList();
    }

    public String getImageData() {
        sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
        String encode = encoder.encode(data);
        return encode;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}