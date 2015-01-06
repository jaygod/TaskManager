package controllers;

import models.Employee;
import models.datamodel.*;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.taskboard.*;

import java.sql.Timestamp;
import java.util.ArrayList;
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
        task.getTimeTracking();

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
            addNewComment(task, assignTaskFormFilled.comment);
        }

        task.assigne = Utils.getEmployee(assignTaskFormFilled.assignName).id;
        task.save();
        flash("success", Messages.get("task.has.been.reasigned"));
        return index(code);
    }

    public static Result addWatcher(String code) {
        Task task = Task.getTask(code);
        List<String> watchersNamesList = getWatcherNamesList(task);
        return ok(addWatcher.render(Employee.findByEmail(request().username()), form(AddWatcher.class), task, watchersNamesList));
    }

    public static Result addWatcherSave(String code) {

        Form<TaskBoard.AddWatcher> addWatcherForm = form(TaskBoard.AddWatcher.class).bindFromRequest();
        Task task = Task.getTask(code);

        if (addWatcherForm.hasErrors()) {
            List<String> watchersNamesList = getWatcherNamesList(task);
            return badRequest(addWatcher.render(Employee.findByEmail(request().username()), form(AddWatcher.class), task, watchersNamesList));
        }

        TaskBoard.AddWatcher addWatcherFormFilled = addWatcherForm.get();
        Employee watcher = Utils.getEmployee(addWatcherFormFilled.watcherName);

        Watcher taskWatcher = new Watcher();
        taskWatcher.setTaskId(task.getId());
        taskWatcher.setUserId(watcher.id);
        taskWatcher.save();

        flash("success", Messages.get("watcher.has.been.added"));
        return index(code);
    }

    private static List<String> getWatcherNamesList(Task task) {
        List<Employee> employeesList = Employee.all();
        List<Employee> watchersList = Utils.getAllTaskWatchers(task.getId());
        employeesList.removeAll(watchersList);
        List<String> watchersNamesList = new ArrayList<>();
        for (Employee employee : employeesList) {
            watchersNamesList.add(employee.fullname);
        }
        return watchersNamesList;
    }

    public static Result getImage(String code, long id) {
        Attachment attachment = Utils.getAttachment(id);

        return ok(showImage.render(Employee.findByEmail(request().username()), code, attachment.getImageData()));
    }

    public static Result deleteWatcher(String taskCode, long watcherId) {
        Utils.deleteWatcher(watcherId);
        flash("success", Messages.get("watcher.has.been.removed"));
        return index(taskCode);
    }

    public static Result addComment(String taskCode) {
        Task task = Task.getTask(taskCode);
        return ok(newComment.render(Employee.findByEmail(request().username()), form(NewComment.class), task));
    }

    public static Result newCommentSave(String taskCode) {
        Form<TaskBoard.NewComment> newCommentForm = form(TaskBoard.NewComment.class).bindFromRequest();
        Task task = Task.getTask(taskCode);

        if (newCommentForm.hasErrors()) {
            return badRequest(newComment.render(Employee.findByEmail(request().username()), form(NewComment.class), task));
        }

        TaskBoard.NewComment newCommentFormFilled = newCommentForm.get();

        addNewComment(task, newCommentFormFilled.comment);

        flash("success", Messages.get("comment.has.been.added"));
        return index(taskCode);
    }

    private static void addNewComment(Task task, String commentBody) {
        Comment comment = new Comment();
        comment.setUserId(Employee.findByEmail(request().username()).id);
        comment.setTaskId(task.getId());

        comment.setComment(commentBody);
        comment.setAddeddate(new Timestamp(System.currentTimeMillis()));
        comment.save();
        task.getCommentsList().add(comment);
    }

    public static Result logWork(String taskCode) {
        Task task = Task.getTask(taskCode);
        List<String> availableLogDaysList = new ArrayList<>();
        for (int i = 0; i < 30; ++i) {
            availableLogDaysList.add(i + "");
        }
        return ok(logWork.render(Employee.findByEmail(request().username()), form(LogWork.class), task, availableLogDaysList));
    }

    public static Result logWorkSave(String taskCode) {
        Form<TaskBoard.LogWork> logWorktForm = form(TaskBoard.LogWork.class).bindFromRequest();
        Task task = Task.getTask(taskCode);

        if (logWorktForm.hasErrors()) {
            List<String> availableLogDaysList = new ArrayList<>();
            for (int i = 0; i < 30; ++i) {
                availableLogDaysList.add(i + "");
            }
            return badRequest(logWork.render(Employee.findByEmail(request().username()), form(LogWork.class), task, availableLogDaysList));
        }

        TaskBoard.LogWork logWorkFormFilled = logWorktForm.get();

        TimeTracking timeTracking = task.getTimeTracking();
        timeTracking.setRemainingTime(timeTracking.getRemainingTime() - Integer.parseInt(logWorkFormFilled.timeSpent));
        timeTracking.save();

        flash("success", Messages.get("work.log.adjusted"));
        return index(taskCode);
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

    /**
     * AddWatcher class used by AddWatcher Form.
     */
    public static class AddWatcher {

        public String watcherName;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            if (isBlank(watcherName)) {
                return "You must select a person to add new watcher";
            }

            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }
    }

    /**
     * NewComment class used by NewComment Form.
     */
    public static class NewComment {

        public String comment;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            if (isBlank(comment)) {
                return "You must enter a comment";
            }

            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }
    }

    /**
     * LogWork class used by LogWork Form.
     */
    public static class LogWork {

        public String timeSpent;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            if (isBlank(timeSpent)) {
                return "You must enter a time spent on task";
            }

            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }
    }

}