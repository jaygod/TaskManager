package models.datamodel;

import play.data.validation.Constraints;

import java.sql.Timestamp;

/**
 * Created by Kuba on 2014-12-09.
 */
public class TaskProperties {

    @Constraints.Required
    private int id;
    @Constraints.Required
    private int taskId;
    @Constraints.Required
    public Timestamp due_date;
    @Constraints.Required
    public String issueType;
    @Constraints.Required
    public String priority;
    @Constraints.Required
    public Timestamp createdDate;
    @Constraints.Required
    public Timestamp updatedDate;
    @Constraints.Required
    public Timestamp resolvedDate;
    @Constraints.Required
    public int reporterId;
}
