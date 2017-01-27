package models;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import com.mongodb.DBCollection;

import helper.datasources.MorphiaObject;
import play.Logger;
import play.data.format.Formats;

public class CasinoApnsUser {
	
	@Id
	public ObjectId id;

	@Indexed(unique = true)
	public String token;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	
	public static List<String> getAllToken() {
		DBCollection collections = MorphiaObject.datastore.getCollection(CasinoApnsUser.class);
		List allToken = collections.distinct("token");
		List<String> allTokenString = (List<String>)allToken;
		for ( String szToken : allTokenString ) {
			Logger.error("CasinoApnsUser getAllToken: " + szToken);
		}
		return allTokenString;
	}
	
	public static CasinoApnsUser find(final String token) {
		CasinoApnsUser user = MorphiaObject.datastore.createQuery(CasinoApnsUser.class)
				.filter("token", token)
				.get();
		if ( user == null ) {
			Logger.error("CasinoApnsUser cannot find: " + token);
		} else {
			Logger.error("CasinoApnsUser found: " + token);
		}
		return user;
	}

	public static CasinoApnsUser create(final String token) {
		CasinoApnsUser user = new CasinoApnsUser();
		user.token = token;
		user.created = new Date();
		MorphiaObject.datastore.save(user);
		return user;
	}

}
