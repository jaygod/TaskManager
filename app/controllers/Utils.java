package controllers;

import com.avaje.ebean.Ebean;
import models.Employee;
import models.datamodel.*;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba on 2015-01-03.
 */
public class Utils extends Controller {

    private static List<String> allTasks;

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
                .eq("assigne", Employee.findByEmail(request().username()).id).findList();
        for(Task task : taskList) {
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
}
