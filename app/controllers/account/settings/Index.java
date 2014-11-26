package controllers.account.settings;

import controllers.Secured;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.account.settings.index;

/**
 * Index Settings page.
 *
 * User: yesnault
 * Date: 15/05/12
 */
@Security.Authenticated(Secured.class)
public class Index extends Controller {

    /**
     * Main page settings
     *
     * @return index settings
     */
    public static Result index() {
        return ok(index.render(User.findByEmail(request().username()), Html.apply("")));
    }
}
