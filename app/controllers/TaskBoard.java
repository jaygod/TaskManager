package controllers;

import models.Employee;
import models.datamodel.Attachment;
import models.datamodel.Comment;
import models.datamodel.Task;
import models.datamodel.TaskProperties;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.taskboard.assignTask;
import views.html.taskboard.index;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

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
        task.getAssigned();
        task.getCommentsList();
        task.setProjectName(Utils.getProjectName(task.projectId));

        List<Employee> watchersList = Utils.getAllTaskWatchers(task.getId());

        return ok(index.render(Employee.findByEmail(request().username()), task, watchersList));
    }

    public static Result assignTask(String code) {
        Task task = Task.getTask(code);
        List<String> employeesNamesList = Utils.getAllEmployeesNames();
        return ok(assignTask.render(Employee.findByEmail(request().username()), form(AssignTask.class), task, employeesNamesList));
    }

    public static Result assignTaskSave(String code) {

        Form<TaskBoard.AssignTask> assignTaskForm = form(TaskBoard.AssignTask.class).bindFromRequest();
        Task task = Task.getTask(code);
        if (assignTaskForm.hasErrors()) {
            List<String> employeesNamesList = Utils.getAllEmployeesNames();
            return badRequest(assignTask.render(Employee.findByEmail(request().username()), form(AssignTask.class), task, employeesNamesList));
        }

        TaskBoard.AssignTask assignTaskFormFilled = assignTaskForm.get();
        if (assignTaskFormFilled.comment != null) {
            Comment comment = new Comment();
            comment.setUserId(Employee.findByEmail(request().username()).id);
            comment.setTaskId(task.getId());

            comment.setComment(assignTaskFormFilled.comment);
            comment.setAdded(new Date(System.currentTimeMillis())); //TODO remove that pice of shit
            comment.setAddeddate(new Timestamp(System.currentTimeMillis()));
            comment.save();
            task.getCommentsList().add(comment);
        }

        task.assigne = Utils.getEmployee(assignTaskFormFilled.assignName).id;
        task.save();
        flash("success", Messages.get("task.has.been.reasigned"));
        return index(code);
    }

    /**
     * AssignTask class used by AssignTask Form.
     */
    public static class AssignTask {

        public String comment;
        public String assignName;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            if (isBlank(assignName)) {
                return "You must select a person to assign a task";
            }

            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }
    }


}