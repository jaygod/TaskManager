package controllers;

import models.Employee;
import models.datamodel.Attachment;
import models.datamodel.Project;
import models.datamodel.Task;
import models.datamodel.TaskProperties;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.projectboard.index;

import java.util.List;

/**
 * Created by Kuba on 2015-01-09.
 */
@Security.Authenticated(Secured.class)
public class ProjectBoard extends Controller {

    private static final int MAX_ROWS = 5;

    public static Result index(Long projectId) {
//        Task task = Task.getTask(code);
//
//        List<Attachment> attachmentList = Attachment.getAttachment(task.getId());
//        task.setAttachmentList(attachmentList);
//
//        TaskProperties taskProperties = TaskProperties.getTaskProperties(task.getId());
//        task.setTaskProperties(taskProperties);
//        task.getAssigned();
//        task.getCommentsList();
//        task.getTimeTracking();
//
//        List<Employee> watchersList = Utils.getAllTaskWatchers(task.getId());

        Project project = Utils.getProjectById(projectId);
        List<Task> tasksWithinProject = Utils.getTasksWithinProject(projectId);
        for (Task task : tasksWithinProject) {
            List<Attachment> attachmentList = Attachment.getAttachment(task.getId());
            task.setAttachmentList(attachmentList);

            TaskProperties taskProperties = TaskProperties.getTaskProperties(task.getId());
            task.setTaskProperties(taskProperties);
            task.getAssigned();
            task.getTimeTracking();
        }

        return ok(index.render(Employee.findByEmail(request().username()), project, tasksWithinProject,
                Utils.getRecentCommentedTasksWithinProject(projectId, MAX_ROWS)));
    }
}