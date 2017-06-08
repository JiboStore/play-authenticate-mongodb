package models.sweetmaker;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.query.Query;

import com.mongodb.DBCollection;

import helper.datasources.MorphiaObject;
import play.Logger;
import play.data.format.Formats;

public class SweetMakerApnsUser {
	
	@Id
	public ObjectId id;

	@Indexed(unique = true)
	public String token;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date lastpush;
	
	public static List<String> getAllToken() {
		DBCollection collections = MorphiaObject.datastore.getCollection(SweetMakerApnsUser.class);
		List allToken = collections.distinct("token");
		List<String> allTokenString = (List<String>)allToken;
		for ( String szToken : allTokenString ) {
			Logger.error("SweetMakerApnsUser getAllToken: " + szToken);
		}
		return allTokenString;
	}
	
	public static List<SweetMakerApnsUser> getAllUsers() {
		Query<SweetMakerApnsUser> qUser = MorphiaObject.datastore.createQuery(SweetMakerApnsUser.class);
		List<SweetMakerApnsUser> users = qUser.asList();
		return users;
	}
	
	public static SweetMakerApnsUser find(final String token) {
		SweetMakerApnsUser user = MorphiaObject.datastore.createQuery(SweetMakerApnsUser.class)
				.filter("token", token)
				.get();
		if ( user == null ) {
			Logger.error("SweetMakerApnsUser cannot find: " + token);
		} else {
			Logger.error("SweetMakerApnsUser found: " + token);
		}
		return user;
	}
	
	public static void save(final SweetMakerApnsUser user) {
		MorphiaObject.datastore.save(user);
	}

	public static SweetMakerApnsUser create(final String token) {
		SweetMakerApnsUser user = new SweetMakerApnsUser();
		user.token = token;
		user.created = new Date();
		MorphiaObject.datastore.save(user);
		return user;
	}

}
