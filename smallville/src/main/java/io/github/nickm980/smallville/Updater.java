package io.github.nickm980.smallville;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class Updater {

    private final static String VERSION = "v1.3.0";
    private final static Logger LOG = LoggerFactory.getLogger(Updater.class);
    private final static String REPO_URL = "https://api.github.com/repos/nickm980/smallville/releases/latest";

    private Updater() {

    }

    public static void checkLatestVersion() {
	OkHttpClient client = new OkHttpClient();
	Request request = new Request.Builder().url(REPO_URL).build();

	try {
	    Response response = client.newCall(request).execute();
	    if (response.isSuccessful()) {
		String body = response.body().string();

		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(body, JsonObject.class);
		String tag = jsonObject.get("tag_name").getAsString();

		if (VERSION.compareTo(tag) < 0) {
		    LOG.warn("Your version of smallville " + VERSION + " is outdated. Latest version: " + tag);
		}
	    }
	} catch (Exception e) {
	    LOG.debug(e.getStackTrace().toString());
	}
    }
}
