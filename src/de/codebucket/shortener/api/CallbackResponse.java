package de.codebucket.shortener.api;

import java.sql.Date;
import java.sql.Timestamp;

public final class CallbackResponse 
{
	public static final String CALLBACK_URL = "http://api.rly.li";
	
	private int clicks;
	private int complaints;
	private Date date_created;
	private String extension;
	private int id;
	private Timestamp last_visited;
	private String link;
	private String long_url;
	private String short_code;
	private Type type;
	
	private String error;
	private String errorMessage;
	
	public boolean isSuccess()
	{
		return short_code != null && long_url != null && type != Type.UNKNOWN;
	}
	
	public int getClicks()
	{
		return clicks;
	}
	
	public int getComplaints()
	{
		return complaints;
	}
	
	public Date getDateCreated()
	{
		return date_created;
	}
	
	public String getExtension()
	{
		return extension;
	}

	public int getId()
	{
		return id;
	}

	public Timestamp getLastVisited() 
	{
		return last_visited;
	}

	public String getLink()
	{
		return link;
	}

	public String getLongUrl()
	{
		return long_url;
	}

	public String getShortCode()
	{
		return short_code;
	}

	public Type getType()
	{
		return type;
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
	
	public static enum Type
	{
		LINK("link"), IMAGE("image"), DOWNLOAD("download"), UNKNOWN("unknown");
		
		private String name;
		
		Type(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
		
		@Override
		public String toString()
		{
			return this.getName();
		}
	}
}
