package models.datamodel;

import com.avaje.ebean.Ebean;
import models.Employee;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by Kuba on 2014-12-14.
 */
@Entity
@Table(name = "\"comment\"")
public class Comment extends Model {

    private Employee commentAuthor;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "comment_gen", sequenceName = "comment_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_gen")
    private int id;

    @Constraints.Required
    private int taskId;

    @Constraints.Required
    private int userId;

    @Constraints.Required
    private String comment;

    @Constraints.Required
    private Timestamp addeddate;

    public static Finder<Integer, Comment> find = new Finder<Integer, Comment>(Integer.class, Comment.class);

    public static List<Comment> all() {
        return find.all();
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getComment() {
        return comment;
    }

    public Employee getCommentAuthor() {
        if (commentAuthor == null) {
            commentAuthor = getCommentAuthorFromDatabase();
        }
        return commentAuthor;
    }

    private Employee getCommentAuthorFromDatabase() {
        return Ebean.find(Employee.class)
                .where()
                .eq("id", this.userId).findUnique();
    }

    public Timestamp getAddeddate() {
        return addeddate;
    }

    public void setAddeddate(Timestamp addeddate) {
        this.addeddate = addeddate;
    }

    public int getTaskId() {
        return taskId;
    }
}