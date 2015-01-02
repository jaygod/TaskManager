package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.io.Files;
import models.Employee;
import models.datamodel.*;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;


@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {

    public static Result index() {
        return ok(index.render(Employee.findByEmail(request().username()), new CreateTask().getAllProjectsNames()));
    }

    public static Result createProject() {
        return ok(createProject.render(Employee.findByEmail(request().username()), form(CreateProject.class)));
    }

    public static Result createTask() {

        return ok(createTask.render(Employee.findByEmail(request().username()), form(CreateTask.class),
                CreateTask.getAllProjectsNames(), CreateTask.getAllTaskTypeNames(), CreateTask.getAllEmployeesNames(),
                CreateTask.getAllTaskPrioritets()));
    }

    public static Result saveProject() {
        try {
            Form<Dashboard.CreateProject> createProjectForm = form(Dashboard.CreateProject.class).bindFromRequest();

            if (createProjectForm.hasErrors()) {
                System.out.println("błąd");
//                return badRequest(singUp.render(createProjectForm));
            }

            Dashboard.CreateProject createProjectFormFilled = createProjectForm.get();
//        Result resultError = checkBeforeSave(createTaskForm, createTask.email);
//
//        if (resultError != null) {
//            return resultError;
//        }

            Project project = new Project();
            project.setName(createProjectFormFilled.projectName);
            project.setSummary(createProjectFormFilled.description);
            project.setVersion(createProjectFormFilled.version);
            project.setDeadline(createProjectFormFilled.deadline);

            project.save();

            return ok(projectCreated.render(Employee.findByEmail(request().username())));
        } catch (Exception e) {
            Logger.error("Project save error", e);
            flash("error", Messages.get("error.technical"));
        }

        return badRequest();
    }

    public static Result saveTask() {

        Form<Dashboard.CreateTask> createTaskForm = form(Dashboard.CreateTask.class).bindFromRequest();

        if (createTaskForm.hasErrors()) {
            CreateTask task = new CreateTask();
            return badRequest(createTask.render(Employee.findByEmail(request().username()), createTaskForm,
                    task.getAllProjectsNames(), task.getAllTaskTypeNames(), task.getAllEmployeesNames(), task.getAllTaskPrioritets()));
        }

        Dashboard.CreateTask createTaskFormFilled = createTaskForm.get();

        try {
            Task task = new Task();
            task.summary = createTaskFormFilled.summary;
            task.description = createTaskFormFilled.description;
            task.labels = createTaskFormFilled.labels;
            task.created = new Date(System.currentTimeMillis());
            task.assigne = Employee.findByFullname(createTaskFormFilled.assigneFullName).id;
            task.projectId = Project.findByProjectName(createTaskFormFilled.projectName).getId();
            task.status = "Created";

            task.save();
            task.code = createTaskFormFilled.projectName + "-" + task.getId();
            task.save();

            TaskProperties taskProperties = new TaskProperties();
            taskProperties.deadline = createTaskFormFilled.deadline;
            taskProperties.type = CreateTask.getTaskTypeId(createTaskFormFilled.issueType);
            taskProperties.priority = createTaskFormFilled.priority;
            taskProperties.reporter = Employee.findByEmail(request().username()).id;
            taskProperties.taskId = task.getId();
            taskProperties.save();

            getAttachment(task);

            return ok(taskCreated.render(Employee.findByEmail(request().username()), task.code));
        } catch (Exception e) {
            Logger.error("Task.save error", e);
            flash("error", Messages.get("error.technical"));
        }

        return badRequest();
    }

    private static void getAttachment(Task task) throws IOException {

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");
        if (file != null) {

            Task.Attachment attachment = new Task.Attachment();
            attachment.name = file.getFilename();
            attachment.content_type = file.getContentType();
            attachment.data = Files.toByteArray(file.getFile());
            attachment.taskId = task.getId();
            attachment.save();
        }
    }

    /**
     * CreateProject class used by CreateProject Form.
     */
    public static class CreateProject {

        @Constraints.Required
        public String projectName;

        @Constraints.Required
        public String description;

        @Constraints.Required
        public Date deadline;

        @Constraints.Required
        public String version;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            if (isBlank(projectName)) {
                return "Project name is required";
            }
            if (isBlank(description)) {
                return "Description is required";
            }

            if (isBlank(version)) {
                return "Version is required";
            }

            if (deadline == null) {
                return "Deadline is required";
            }

            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }
    }


    /**
     * CreateTask class used by CreateTask Form.
     */
    public static class CreateTask {

        public String projectName;
        public String issueType;
        public String summary;
        public String priority;
        public String labels;
        public File file;
        public String description;
        public Date deadline;
        public String assigneFullName;


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

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            if (isBlank(summary)) {
                return "Project summary is required";
            }
            if (isBlank(description)) {
                return "Description is required";
            }

            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }
    }
}
