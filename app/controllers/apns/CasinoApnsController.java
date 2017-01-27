package controllers.apns;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

import models.CasinoApnsUser;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.*;

public class CasinoApnsController extends Controller {	
	
	private static final String certPath = 
			"conf/certificates/apns_dev_export_single_selection_root_from_keychain.p12";
	private static final String deviceToken = 
			"2404d486d44d6718407ef5db94639b9a99942de0bdbc879ea4c5602625e65e1b";
	
	public static Result testPushAction() {
		ApnsService service =
			    APNS.newService()
			    .withCert(certPath, "123456")
			    .withSandboxDestination()
			    .build();
		String payload = APNS.newPayload().badge(1).alertBody("sent using notnoop play entry point from testpush!").build();
		service.push(deviceToken, payload);
		return ok(index.render());
	}
	
	public static Result sendallAction() {
//		List<String> token = CasinoApnsUser.getAllToken();
		List<CasinoApnsUser> users = CasinoApnsUser.getAllUsers();
		ApnsService service =
			    APNS.newService()
			    .withCert(certPath, "123456")
			    .withSandboxDestination()
			    .build();
		String payload = APNS.newPayload().badge(1).alertBody("Just to bug you!").build();
		for ( CasinoApnsUser user : users ) {
			service.push(user.token, payload);
			user.lastpush = new Date();
			CasinoApnsUser.save(user);
		}
//		for ( String szToken : token ) {
//			service.push(szToken, payload);
//		}
		return ok(index.render());
	}
	
	public static Result registerAction() {
		Map<String, String[]> postData = request().body().asFormUrlEncoded();
		for ( Map.Entry<String, String[]> entry : postData.entrySet() ) {
			Logger.error("registration pair: " + entry.getKey() + " : " + entry.getValue().toString());
			String[] values = entry.getValue();
			String szToken = null;
			for ( String szValue : values ) {
				Logger.error(entry.getKey() + " => " + szValue );
				szToken = szValue;
			}
			if ( szToken != null ) {
				CasinoApnsUser user = CasinoApnsUser.find(szToken);
				if ( user == null ) {
					CasinoApnsUser.create(szToken);
				}
			}
		}
		return ok(index.render());
	}

}