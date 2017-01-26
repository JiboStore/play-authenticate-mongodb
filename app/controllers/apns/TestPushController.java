package controllers.apns;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

import play.mvc.Controller;
import play.mvc.Result;

public class TestPushController extends Controller {
	
	private static final String certPath = 
			"conf/certificates/hakim_maji_apns/170126b_hakim_apns_dev_cert_and_privatekey.p12";
	private static final String deviceToken = 
			"5e2d89c5276de7bd9205a3926991733e12f1482185be4849752a5c6d3b78a788";
	
	public static Result testPushAction() {
		ApnsService service =
			    APNS.newService()
			    .withCert(certPath, "123456")
			    .withSandboxDestination()
			    .build();
		String payload = APNS.newPayload().alertBody("sent using notnoop play entry point!").build();
		service.push(deviceToken, payload);
		try {
			Thread.sleep(10000);
		} catch ( InterruptedException ie ) {
			
		}
		return ok();
	}

}