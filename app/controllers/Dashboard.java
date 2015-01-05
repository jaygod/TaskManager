package controllers;

import com.google.common.io.Files;
import models.Employee;
import models.datamodel.Attachment;
import models.datamodel.Project;
import models.datamodel.Task;
import models.datamodel.TaskProperties;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.account.userPage;
import views.html.dashboard.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static play.data.Form.form;


@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {

    public static Result index() {
        return ok(index.render(Employee.findByEmail(request().username()), Utils.getAllProjectsNames(), Utils.getAllAssignedTasks()));
    }

    public static Result userPage(long id) {
        Employee employee = Utils.getEmployee(id);
        return ok(userPage.render(Employee.findByEmail(request().username()), employee));
    }


    public static Result createProject() {
        return ok(createProject.render(Employee.findByEmail(request().username()), form(CreateProject.class)));
    }

    public static Result createTask() {

        return ok(createTask.render(Employee.findByEmail(request().username()), form(CreateTask.class),
                Utils.getAllProjectsNames(), Utils.getAllTaskTypeNames(), Utils.getAllEmployeesNames(),
                Utils.getAllTaskPrioritets()));
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
            return badRequest(createTask.render(Employee.findByEmail(request().username()), createTaskForm,
                    Utils.getAllProjectsNames(), Utils.getAllTaskTypeNames(), Utils.getAllEmployeesNames(), Utils.getAllTaskPrioritets()));
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

            task.setTaskProperties(getProperties(createTaskFormFilled, task));
            task.setAttachment(getAttachment(task));

            return ok(taskCreated.render(Employee.findByEmail(request().username()), task.code));
        } catch (Exception e) {
            Logger.error("Task.save error", e);
            flash("error", Messages.get("error.technical"));
        }

        return badRequest();
    }

    private static TaskProperties getProperties(CreateTask createTaskFormFilled, Task task) {
        TaskProperties taskProperties = new TaskProperties();
        taskProperties.deadline = createTaskFormFilled.deadline;
        taskProperties.type = Utils.getTaskTypeId(createTaskFormFilled.issueType);
        taskProperties.priority = createTaskFormFilled.priority;
        taskProperties.reporterId = Employee.findByEmail(request().username()).id;
        taskProperties.taskId = task.getId();
        taskProperties.save();
        return taskProperties;
    }

    private static Attachment getAttachment(Task task) throws IOException {

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");
        if (file != null) {

            Attachment attachment = new Attachment();
            attachment.name = file.getFilename();
            attachment.content_type = file.getContentType();
            attachment.setData(Utils.imageToByte(file.getFile()));
            attachment.taskId = task.getId();
            attachment.save();
            return attachment;
        }
        return null;
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
