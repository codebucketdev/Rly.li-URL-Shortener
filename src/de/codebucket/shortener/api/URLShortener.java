package de.codebucket.shortener.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.codebucket.shortener.Settings;

public class URLShortener
{
	private static Settings settings = Settings.getInstance();
	
	public static CallbackResponse shortenUrl(String url) throws ClientProtocolException, IOException
	{
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost(CallbackResponse.CALLBACK_URL + "/shorten?client_id=" + settings.getClientId());

		// add header
		request.setHeader("User-Agent", settings.getUserAgent());

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("url", url));
		parameters.add(new BasicNameValuePair("private", String.valueOf(settings.isPrivateMode())));
		request.setEntity(new UrlEncodedFormEntity(parameters));
		
		HttpResponse response = client.execute(request);
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		
		CallbackResponse callback = gson.fromJson(new InputStreamReader(response.getEntity().getContent()), CallbackResponse.class);
		return callback;
	}
}
