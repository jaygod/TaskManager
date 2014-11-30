package controllers;

import models.Employee;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.index;

/**
 * Employee: yesnault
 * Date: 22/01/12
 */
@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {

    public static Result index() {
        return ok(index.render(Employee.findByEmail(request().username())));
    }
}
