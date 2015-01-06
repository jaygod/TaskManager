package models.datamodel;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Kuba on 2014-12-14.
 */
@Entity
@Table(name = "\"timetracking\"")
public class TimeTracking extends Model {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "timetracking_gen", sequenceName = "timetracking_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "timetracking_gen")
    private int id;

    @Constraints.Required
    private int taskId;

    @Constraints.Required
    private int estimatedTime;

    @Constraints.Required
    private int remainingTime;

    public static Finder<Integer, TimeTracking> find = new Finder<Integer, TimeTracking>(Integer.class, TimeTracking.class);

    public static List<TimeTracking> all() {
        return find.all();
    }

    public int getId() {
        return id;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public float getRemainingTimePercent() {
        return Math.round((float) remainingTime / estimatedTime * 100);
    }

    public float getOverTimePercent() {
        int whole = 10;
        if (-remainingTime < 10) {
            whole = 10;
        } else if (-remainingTime < 20) {
            whole = 20;
        } else if (-remainingTime < 30) {
            whole = 30;
        } else if (-remainingTime < 50) {
            whole = 50;
        } else {
            whole = 100;
        }
        return Math.round((float) -remainingTime/whole * 100);
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
}
