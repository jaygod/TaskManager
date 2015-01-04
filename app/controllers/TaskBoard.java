package controllers;

import models.Employee;
import models.datamodel.Attachment;
import models.datamodel.Task;
import models.datamodel.TaskProperties;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Content;
import views.html.taskboard.assignTask;
import views.html.taskboard.index;

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

        if (assignTaskForm.hasErrors()) {
            Task task = Task.getTask(code);
            List<String> employeesNamesList = Utils.getAllEmployeesNames();
            return badRequest(assignTask.render(Employee.findByEmail(request().username()), form(AssignTask.class), task, employeesNamesList));
        }

        TaskBoard.AssignTask assignTaskFormFilled = assignTaskForm.get();

        Task task = Task.getTask(code);
        task.assigne = Utils.getEmployee(assignTaskFormFilled.assignName).id;
        task.save();
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