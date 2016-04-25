package de.codebucket.shortener.api;

public class ServerRuntimeException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	private String error, errorMessage;
	
	public ServerRuntimeException()
	{
		this("An internal error occurred while attempting to perform this request.");
	}
	
	public ServerRuntimeException(String errorMessage)
	{
		this("ServerRuntimeException", errorMessage);
	}
	
	public ServerRuntimeException(String error, String errorMessage)
	{
		super(errorMessage);
		this.error = error;
		this.errorMessage = errorMessage;
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
