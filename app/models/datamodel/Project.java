package models.datamodel;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Kuba on 2014-12-09.
 */
@Entity
@Table(name="\"project\"")
public class Project extends Model {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name="project_gen", sequenceName="project_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_gen")
    protected int id;

    @Constraints.Required
        private String name;

    @Constraints.Required
    private String summary;

    @Constraints.Required
    private String version;

    @Constraints.Required
    private Date deadline;

    private byte[] icon;

    public static Finder<Integer, Project> find = new Finder<Integer, Project>(Integer.class, Project.class);

    public static List<Project> all() {
        return find.all();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    /**
     * Retrieve a project from a projectName.
     *
     * @param projectName Full name
     * @return a user
     */
    public static Project findByProjectName(String projectName) {
        return find.where().eq("name", projectName).findUnique();
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public byte[] getIcon() {
        return icon;
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
