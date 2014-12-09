package models.datamodel;

import play.data.validation.Constraints;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Kuba on 2014-12-09.
 */
public class Task {

    @Constraints.Required
    private int id;
    @Constraints.Required
    private int Projectid;
    @Constraints.Required
    public String summary;
    @Constraints.Required
    public String description;
    @Constraints.Required
    public List<String> labels;
    @Constraints.Required
    public String status;
    @Constraints.Required
    public Timestamp created;
    @Constraints.Required
    public int assignedID;

    private TaskProperties taskProperties;
}
