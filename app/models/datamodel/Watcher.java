package models.datamodel;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Kuba on 2014-12-14.
 */
@Entity
@Table(name = "\"watcher\"")
public class Watcher extends Model {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "watcher_gen", sequenceName = "watcher_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "watcher_gen")
    private int id;

    @Constraints.Required
    private int taskId;

    @Constraints.Required
    private int userId;

    public static Finder<Integer, Watcher> find = new Finder<Integer, Watcher>(Integer.class, Watcher.class);

    public static List<Watcher> all() {
        return find.all();
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
