package controllers;

import models.Employee;
import models.datamodel.Attachment;
import models.datamodel.Task;
import models.datamodel.TaskProperties;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.taskboard.index;

/**
 * Created by Kuba on 2015-01-02.
 */
@Security.Authenticated(Secured.class)
public class TaskBoard extends Controller {

    public static Result index(String code) {
        Task task = Task.getTask(code);

        Attachment attachment = Attachment.getAttachment(task.getId());
        task.setAttachment(attachment);

        TaskProperties taskProperties = TaskProperties.getTaskProperties(task.getId());
        task.setTaskProperties(taskProperties);

        return ok(index.render(Employee.findByEmail(request().username()), task));
    }

}