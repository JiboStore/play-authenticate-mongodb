package controllers.apns;

import com.notnoop.apns.*;

import com.relayrides.pushy.apns.*;
import com.relayrides.pushy.apns.proxy.*;
import com.relayrides.pushy.apns.util.*;
import io.netty.util.concurrent.*;

import controllers.Application;

import java.io.File;
import java.io.InputStream;
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

import javax.net.ssl.*;
import javax.security.cert.*;

import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import views.html.*;
//import be.objectify.deadbolt.java.actions.Group;
//import be.objectify.deadbolt.java.actions.Restrict;
//
//import com.feth.play.module.pa.PlayAuthenticate;
//import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
//import com.feth.play.module.pa.user.AuthUser;

//import controllers.routes;

public class RegistrationController extends Controller {
	
	public static Result index() throws Exception {
		File f = Play.application().path();
		Logger.error("index root: " + f.getAbsolutePath());
		testSendPushFromNotNoop();
//		testSendPushFromPushy();
//		ApnsService service =
//			    APNS.newService()
//			    .withCert("conf/certificates/casinobzbeepush.p12", "123456")
//			    .withSandboxDestination()
//			    .build();
//		String payload = APNS.newPayload().alertBody("Can't be simpler than this!").build();
//		String token = "682617713d505bd2d65bde87fe6356786e4a4063f5ad8e5ef9db0ab2814f4321";
//		service.push(token, payload);
		return ok(index.render());
	}
	
	private static void testSendPushFromNotNoop() throws Exception{
		ApnsService service = null;
        try {
        	doTrustToCertificates();
//        	String certPath = "conf/certificates/apns_dev_cert.p12";
//        	String certPath = "conf/certificates/maji/aps_development.cer";
//        	String certPath = "conf/certificates/hakim_maji_apns/hakim_aps_export_development.p12";
//        	String certPath = "conf/certificates/hakim_maji_apns/export_keyonly_dev.p12";
        	String certPath = "conf/certificates/hakim_maji_apns/export_keyonly_dev.p12"; // ok on urban airship
//        	String certPath = "conf/certificates/apns_dev_cert.p12"; // ok on urban airship
        	File certFile = new File(certPath);
        	Logger.error("cert file exist: " + certFile.exists());
        	Logger.error("cert file size: " + certFile.length());
            // get the certificate
            InputStream certStream = Application.class.getClassLoader().getResourceAsStream(certPath);
            service = APNS.newService().withCert(certStream, "123456").withSandboxDestination().build();
            // or
            // service = APNS.newService().withCert(certStream,
            // "your_cert_password").withProductionDestination().build();
//            service.start();
//            service.testConnection();

            // we had a daily update here, so we need to know how many 
            //days the user hasn't started the app
            // so that we get the number of updates to display it as the badge.
        	try {
        		PayloadBuilder payloadBuilder = APNS.newPayload();
        		payloadBuilder = payloadBuilder.badge(1).alertBody("some message you want to send here");
        		String payload = payloadBuilder.build();
//        		String token = "682617713d505bd2d65bde87fe6356786e4a4063f5ad8e5ef9db0ab2814f4321";
//        		String token = "97e083f46013d61be5c9b1990b1b9ec51f2f797b05f86e186a0a4113e4e9407c";
        		String token = "5e2d89c5276de7bd9205a3926991733e12f1482185be4849752a5c6d3b78a788";
        		service.push(token, payload);
            } catch (Exception ex) {
                // some logging stuff
            	Logger.error("payload building exception: " + ex.getMessage());
            	throw new Exception(ex);
            }
        } catch (Exception ex) {
            // more logging
        	Logger.error("APNS init error! " + ex.getMessage());
        	throw new Exception(ex);
        } finally {
            // check if the service was successfull initialized and stop it here, if it was
            if (service != null) {
                service.stop();
            }
 
        }
	}
	
	public static void doTrustToCertificates() throws Exception {
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        return;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        return;
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                    System.out.println("Warning: URL host '" + urlHostName + "' is different to SSLSession host '" + session.getPeerHost() + "'.");
                }
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
	
	public static void testSendPushFromPushy() {
    	String certPath = "conf/certificates/hakim_maji_apns/export_keyonly_dev.p12";
//    	String certPath = "conf/certificates/hakim_maji_apns/export_keyonly_uni.p12";
    	String token = "97e083f46013d61be5c9b1990b1b9ec51f2f797b05f86e186a0a4113e4e9407c";
    	String appId = "com.crazybach.ios.casinodeluxe";
    	
    	File certFile = new File(certPath);
    	Logger.error("pushy cert file exist: " + certFile.exists());
    	Logger.error("pushy cert file size: " + certFile.length());
    	
    	try {
    		final ApnsClient apnsClient = new ApnsClientBuilder().setClientCredentials(new File(certPath), "123456").build();
    		final Future<Void> connectFuture = apnsClient.connect(ApnsClient.DEVELOPMENT_APNS_HOST);
    		connectFuture.await();
    	
    		final SimpleApnsPushNotification pushNotification;

    		{
    	    	final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
    	    	payloadBuilder.setAlertBody("Example!");

    	    	final String payload = payloadBuilder.buildWithDefaultMaximumLength();
//    	    	final String token = TokenUtil.sanitizeTokenString(token);

    	    	pushNotification = new SimpleApnsPushNotification(token, appId, payload);
    	    	
    	    	final Future<PushNotificationResponse<SimpleApnsPushNotification>> sendNotificationFuture =
    	    	        apnsClient.sendNotification(pushNotification);
    	    	
    	    	try {
    	    	    final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse =
    	    	            sendNotificationFuture.get();

    	    	    if (pushNotificationResponse.isAccepted()) {
    	    	        Logger.error("Push notification accepted by APNs gateway.");
    	    	    } else {
    	    	        Logger.error("Notification rejected by the APNs gateway: " +
    	    	                pushNotificationResponse.getRejectionReason());

    	    	        if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
    	    	            Logger.error("\t…and the token is invalid as of " +
    	    	                pushNotificationResponse.getTokenInvalidationTimestamp());
    	    	        }
    	    	    }
    	    	} catch (final Exception e) {
    	    	    Logger.error("Failed to send push notification. " + e.getMessage());
    	    	    e.printStackTrace();

    	    	    if (e.getCause() instanceof ClientNotConnectedException) {
    	    	        Logger.error("Waiting for client to reconnect…");
    	    	        apnsClient.getReconnectionFuture().await();
    	    	        Logger.error("Reconnected.");
    	    	    }
    	    	}
    		}
    		
    		final Future<Void> disconnectFuture = apnsClient.disconnect();
    		disconnectFuture.await();
    		Logger.error("operation completed successfully");
    	}
    	catch ( Exception e ) {
    		Logger.error("testSendPushFromPushy exception: " + e.getMessage());
    	}
	}

}
