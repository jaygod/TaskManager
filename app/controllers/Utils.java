package controllers;

import com.avaje.ebean.Ebean;
import models.Employee;
import models.datamodel.*;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba on 2015-01-03.
 */
public class Utils extends Controller {

    private static List<String> allTasks;

    public static Project getProjectById(Long projectId) {

        return Ebean.find(Project.class)
                .where()
                .eq("id", projectId).findUnique();
    }

    public static List<Task> getTasksWithinProject(Long projectId) {
        return Ebean.find(Task.class)
                .where()
                .eq("project_id", projectId).orderBy("code asc").findList();
    }

    public static List<String> getAllProjectsNames() {

        List<String> projectNamesList = new ArrayList<>();
        List<Project> projectsList = Project.find.all();
        for (Project project : projectsList) {
            projectNamesList.add(project.getName());
        }
        return projectNamesList;
    }

    public static List<String> getAllEmployeesNames() {

        List<String> employeeNamesList = new ArrayList<>();
        List<Employee> employeeList = Employee.find.all();
        for (Employee employee : employeeList) {
            employeeNamesList.add(employee.fullname);
        }
        return employeeNamesList;
    }

    public static List<String> getAllTaskTypeNames() {

        List<String> taskTypesNamesList = new ArrayList<>();
        List<TaskType> taskTypesList = TaskType.find.all();
        for (TaskType taskType : taskTypesList) {
            taskTypesNamesList.add(taskType.getType());
        }
        return taskTypesNamesList;
    }

    public static List<String> getAllTaskPrioritets() {

        List<String> taskPrioritets = new ArrayList<>();
        List<TaskPrioritets> taskPrioritetsList = TaskPrioritets.find.all();
        for (TaskPrioritets taskPriority : taskPrioritetsList) {
            taskPrioritets.add(taskPriority.getPriority());
        }
        return taskPrioritets;
    }

    public static int getTaskTypeId(String taskTypeName) {

        TaskType taskType = Ebean.find(TaskType.class)
                .where()
                .eq("type", taskTypeName).findUnique();
        return taskType.getId();
    }

    public static List<String> getAllTasksCodes() {
        List<String> taskCodesList = new ArrayList<>();
        List<Task> tasksList = Task.find.all();
        for (Task task : tasksList) {
            taskCodesList.add(task.code);
        }
        return taskCodesList;
    }

    public static List<Task> getAllAssignedTasks() {
        List<Task> taskList = Ebean.find(Task.class)
                .where()
                .eq("assigne", Employee.findByEmail(request().username()).id).orderBy("code asc").findList();
        for (Task task : taskList) {
            TaskProperties taskProperties = Ebean.find(TaskProperties.class)
                    .where()
                    .eq("task_id", task.getId()).findUnique();

            TaskType taskType = Ebean.find(TaskType.class)
                    .where()
                    .eq("id", taskProperties.type).findUnique();
            taskProperties.setTaskType(taskType);

            task.setTaskProperties(taskProperties);
        }

        return taskList;
    }

    public static Employee getEmployee(long id) {
        Employee employee = Ebean.find(Employee.class)
                .where()
                .eq("id", id).findUnique();
        return employee;
    }

    public static Employee getEmployee(String assignName) {
        Employee employee = Ebean.find(Employee.class)
                .where()
                .eq("fullname", assignName).findUnique();
        return employee;
    }

    public static List<Employee> getAllTaskWatchers(int taskId) {
        List<Watcher> watchersList = Ebean.find(Watcher.class)
                .where()
                .eq("task_id", taskId).findList();

        List<Employee> employeesList = new ArrayList<>();
        for (Watcher watcher : watchersList) {
            employeesList.add(getEmployee(watcher.getUserId()));
        }
        return employeesList;
    }

    public static String getProjectName(int projectId) {
        Project project = Ebean.find(Project.class)
                .where()
                .eq("id", projectId).findUnique();

        return project.getName();
    }

    public static Attachment getAttachment(long id) {
        return Ebean.find(Attachment.class)
                .where()
                .eq("id", id).findUnique();

    }

    public static void deleteWatcher(long watcherId) {
        Watcher watcher = Ebean.find(Watcher.class)
                .where()
                .eq("user_id", watcherId).findUnique();
        Ebean.delete(Watcher.class, watcher.getId());
    }

    public static List<Task> getRecentCommentedTasks(int maxRows) {
        List<Comment> commentsList = Ebean.find(Comment.class).
                setMaxRows(maxRows).orderBy("addeddate desc").findList();

        List<Task> taskList = new ArrayList<>();
        for (Comment comment : commentsList) {
            Task task = Ebean.find(Task.class).where().eq("id", comment.getTaskId()).findUnique();
            List<Comment> taskCommentsList = new ArrayList<Comment>();
            taskCommentsList.add(comment);
            task.setCommentsList(taskCommentsList);
            taskList.add(task);
        }

        return taskList;
    }

    public static List<Task> getRecentCommentedTasksWithinProject(Long projectId, int maxRows) {
        List<Comment> commentsList = Ebean.find(Comment.class).where().eq("project_id", projectId).
                setMaxRows(maxRows).orderBy("addeddate desc").findList();

        List<Task> taskList = new ArrayList<>();
        for (Comment comment : commentsList) {
            Task task = Ebean.find(Task.class).where().eq("id", comment.getTaskId()).where().findUnique();
            List<Comment> taskCommentsList = new ArrayList<Comment>();
            taskCommentsList.add(comment);
            task.setCommentsList(taskCommentsList);
            taskList.add(task);
        }

        return taskList;
    }

    public static models.datamodel.Status getStatus(String status) {
        return Ebean.find(models.datamodel.Status.class)
                .where()
                .eq("status", status).findUnique();
    }

    public static List<String> getAllStatusNamesList() {
        List<String> statusNamesList = new ArrayList<>();
        List<models.datamodel.Status> statusList = models.datamodel.Status.find.all();
        for (models.datamodel.Status status : statusList) {
            statusNamesList.add(status.getStatus());
        }
        return statusNamesList;
    }

    public static byte[] imageToByte(File file) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                bos.write(buf, 0, readNum);
            }
        } catch (IOException ex) {
            Logger.error("Utils.imageToByte error", ex);
            flash("error", Messages.get("error.technical"));
        }
        byte[] bytes = bos.toByteArray();

        return bytes;
    }

}
