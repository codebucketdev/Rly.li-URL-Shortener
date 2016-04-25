package de.codebucket.shortener.session;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.codebucket.shortener.Settings;
import de.codebucket.shortener.api.CallbackResponse;

public class SessionManager
{
	private static Settings settings = Settings.getInstance();
	
	public static Session getSession(String clientId) throws ClientProtocolException, IOException
	{
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(CallbackResponse.CALLBACK_URL + "/session?client_id=" + clientId);

		// add request header
		request.addHeader("User-Agent", settings.getUserAgent());

		HttpResponse response = client.execute(request);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		
		Session session = gson.fromJson(new InputStreamReader(response.getEntity().getContent()), Session.class);
		return session;
	}
}
