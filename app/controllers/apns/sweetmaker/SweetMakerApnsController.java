package controllers.apns.sweetmaker;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

import models.sweetmaker.SweetMakerApnsUser;
import play.Logger;
import play.api.mvc.MultipartFormData;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.RawBuffer;
import play.mvc.Result;

import views.html.*;

public class SweetMakerApnsController extends Controller {	
	
	private static final String certPath = 
			"conf/certificates/sweetmaker/aps_development_sweetmaker.p12";
	private static final String deviceToken = 
			"2404d486d44d6718407ef5db94639b9a99942de0bdbc879ea4c5602625e65e1b";
	
	public static Result testpostAction() {
		Logger.error("SweetMakerApnsController.testpostAction");
		try {
			Map<String, String[]> postData = request().body().asFormUrlEncoded();
			if ( postData == null ) {
				Logger.error("postData is null!");
				String body = request().body().asText();
				Logger.error("postData as text: " + body);
				RawBuffer raw = request().body().asRaw();
				Logger.error("postData as rawbuffer: " + raw.toString());
				Logger.error("requestBody: " + request().body().toString());
			}
			for ( Map.Entry<String, String[]> entry : postData.entrySet() ) {
				Logger.error("post param pair: " + entry.getKey() + " : " + entry.getValue().toString());
				String[] values = entry.getValue();
				String szToken = null;
				for ( String szValue : values ) {
					Logger.error(entry.getKey() + " => " + szValue );
					szToken = szValue;
				}
			}
		} catch ( Exception e ) {
			Logger.error("exception in testpostaction: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		return ok(index.render());
	}
	
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
//		List<String> token = SweetMakerApnsUser.getAllToken();
		List<SweetMakerApnsUser> users = SweetMakerApnsUser.getAllUsers();
		ApnsService service =
			    APNS.newService()
			    .withCert(certPath, "123456")
			    .withSandboxDestination()
			    .build();
		String payload = APNS.newPayload().badge(1).alertBody("Just to bug you!").build();
		for ( SweetMakerApnsUser user : users ) {
			service.push(user.token, payload);
			user.lastpush = new Date();
			SweetMakerApnsUser.save(user);
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
				SweetMakerApnsUser user = SweetMakerApnsUser.find(szToken);
				if ( user == null ) {
					SweetMakerApnsUser.create(szToken);
				}
			}
		}
		return ok(index.render());
	}

}