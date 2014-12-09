package controllers;

import models.Employee;
import models.datamodel.Project;
import models.datamodel.Status;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.createProject;
import views.html.dashboard.createTask;
import views.html.dashboard.index;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

/**
 * Employee: yesnault
 * Date: 22/01/12
 */
@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {

    public static Result index() {
        return ok(index.render(Employee.findByEmail(request().username())));
    }

    public static Result createProject() {
        return ok(createProject.render(Employee.findByEmail(request().username()), form(CreateProject.class)));
    }

    public static Result createTask() {
        return ok(createTask.render(Employee.findByEmail(request().username()), form(CreateTask.class)));
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
        public Timestamp deadline;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {

            return null;
        }
    }
    /**
     * CreateTask class used by CreateTask Form.
     */
    public static class CreateTask {

        @Constraints.Required
        private int projectId;
        @Constraints.Required
        public String issueType;
        @Constraints.Required
        public String summary;
        @Constraints.Required
        public String priority;
        @Constraints.Required
        public String labels;
        @Constraints.Required
        public int attatchments;
        @Constraints.Required
        public String description;
        @Constraints.Required
        public Timestamp dueDate;
        @Constraints.Required
        List<String> projectNamesList = new ArrayList<>();

        public List<String> getAllProjectsNames() {

            List<Project> projectsList = Project.find.all();
            for (Project project : projectsList) {
                projectNamesList.add(project.getName());
            }
            return projectNamesList;
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
