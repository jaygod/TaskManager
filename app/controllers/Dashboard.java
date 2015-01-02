package controllers;

import models.Employee;
import models.datamodel.Project;
import models.datamodel.Task;
import models.datamodel.TaskType;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.createProject;
import views.html.dashboard.createTask;
import views.html.dashboard.index;
import views.html.dashboard.projectCreated;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static play.data.Form.form;

/**
 * Employee: yesnault
 * Date: 22/01/12
 */
@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {

    public static Result index() {
        return ok(index.render(Employee.findByEmail(request().username()),new CreateTask().getAllProjectsNames()));
    }

    public static Result createProject() {
        return ok(createProject.render(Employee.findByEmail(request().username()), form(CreateProject.class)));
    }

    public static Result createTask() {

        CreateTask task = new CreateTask();

        return ok(createTask.render(Employee.findByEmail(request().username()), form(CreateTask.class),
                task.getAllProjectsNames(), task.getAllTaskTypes(), task.getAllEmployeesNames()));
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
            //return badRequest(singUp.render(createTask));
        }

        Dashboard.CreateTask createTaskFormFilled = createTaskForm.get();
//        Result resultError = checkBeforeSave(createTaskForm, createTask.email);
//
//        if (resultError != null) {
//            return resultError;
//        }

        try {
            Task task = new Task();
            task.summary = createTaskFormFilled.summary;
            task.description = createTaskFormFilled.description;
            task.labels = createTaskFormFilled.labels;
            task.created = new Date(System.currentTimeMillis());
            task.assigne = Employee.findByFullname(createTaskFormFilled.assigneFullName).id;
            task.projectId = Project.findByProjectName(createTaskFormFilled.projectName).getId();
            task.status = "Created";

            Task.Attachment attachment = new Task.Attachment();
            attachment.data = Files.readAllBytes(createTaskFormFilled.file.getAbsoluteFile().toPath());
            attachment.name = createTaskFormFilled.file.getName();
            attachment.size = createTaskFormFilled.file.length();
//            attachment.taskId = task.getId();
            attachment.save();

            task.attachments = attachment.getId();
            task.save();

            return redirect(
                    controllers.routes.Dashboard.index());
        } catch (Exception e) {
            Logger.error("Task.save error", e);
            flash("error", Messages.get("error.technical"));
        }

        return badRequest();
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

        //        @Constraints.Required
        public String projectName;
        //        @Constraints.Required
        public String issueType;
        //        @Constraints.Required
        public String summary;
        //        @Constraints.Required
        public String priority;
        //        @Constraints.Required
        public String labels;
        //        @Constraints.Required
        public File file;
        //        @Constraints.Required
        public String description;

        public Date deadline;

        public String assigneFullName;

        List<String> projectNamesList = new ArrayList<>();
        List<String> typesList = new ArrayList<>();
        List<String> employeeNamesList = new ArrayList<>();

        public List<String> getAllProjectsNames() {

            List<Project> projectsList = Project.find.all();
            for (Project project : projectsList) {
                projectNamesList.add(project.getName());
            }
            return projectNamesList;
        }

        public List<String> getAllEmployeesNames() {

            List<Employee> employeeList = Employee.find.all();
            for (Employee employee : employeeList) {
                employeeNamesList.add(employee.fullname);
            }
            return employeeNamesList;
        }

        public List<String> getAllTaskTypes() {

            List<TaskType> taskTypesList = TaskType.find.all();
            for (TaskType taskType : taskTypesList) {
                typesList.add(taskType.getType());
            }
            return typesList;
        }

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {

            return null;
        }
    }
}
