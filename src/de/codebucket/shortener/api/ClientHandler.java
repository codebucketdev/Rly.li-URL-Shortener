package de.codebucket.shortener.api;

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

public class ClientHandler
{
	private static Settings settings = Settings.getInstance();
	
	public static CallbackResponse[] getRecentUploads(String clientId) throws ClientProtocolException, IOException
	{
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(CallbackResponse.CALLBACK_URL + "/history?client_id=" + clientId);

		// add request header
		request.addHeader("User-Agent", settings.getUserAgent());

		HttpResponse response = client.execute(request);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		
		CallbackResponse[] callback = gson.fromJson(new InputStreamReader(response.getEntity().getContent()), CallbackResponse[].class);
		return callback;
	}
}
