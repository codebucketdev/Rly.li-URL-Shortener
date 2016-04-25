package de.codebucket.shortener.session;

import java.sql.Date;
import java.sql.Timestamp;

public final class Session
{	
	private String client_id;
	private Date date_created;
	private String user_agent;
	private String timezone;
	private Timestamp last_online;
	
	private String error;
	private String errorMessage;
	
	public boolean isSuccess()
	{
		return client_id != null;
	}
	
	public String getClientId()
	{
		return client_id;
	}
	
	public Date getDateCreated()
	{
		return date_created;
	}
	
	public String getUserAgent()
	{
		return user_agent;
	}
	
	public String getTimezone()
	{
		return timezone;
	}
	
	public Timestamp getLastOnline()
	{
		return last_online;
	}
	
	public boolean isError()
	{
		return error != null && errorMessage != null;
	}
	
	public String getError()
	{
		return error;
	}
	
	public String getErrorMessage()
	{
		return errorMessage;
	}
}
