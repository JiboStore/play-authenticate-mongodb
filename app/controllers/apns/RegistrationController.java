package controllers.apns;

import com.notnoop.apns.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.User;
import play.Play;
import play.Routes;
import play.Logger;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Response;
import play.mvc.Http.Session;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;

import views.html.*;
//import be.objectify.deadbolt.java.actions.Group;
//import be.objectify.deadbolt.java.actions.Restrict;
//
//import com.feth.play.module.pa.PlayAuthenticate;
//import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
//import com.feth.play.module.pa.user.AuthUser;

//import controllers.routes;

public class RegistrationController extends Controller {
	
	public static Result index() {
		File f = Play.application().path();
		Logger.error("index root: " + f.getAbsolutePath());
		ApnsService service =
			    APNS.newService()
			    .withCert("conf/certificates/dev.p12", "123456")
			    .withSandboxDestination()
			    .build();
		String payload = APNS.newPayload().alertBody("Can't be simpler than this!").build();
		String token = "fedfbcfb....";
		service.push(token, payload);
		return ok(index.render());
	}

}
