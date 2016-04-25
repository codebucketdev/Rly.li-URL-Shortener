package de.codebucket.shortener.utils;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;
import java.util.UUID;

import com.google.gson.Gson;

public abstract class UpdateTask 
{
	private UUID key;
	private String url;
	private Update current;
	
	public static final Update CURRENT_VERSION = new Update("Rly.li URL Shortener", "2.4-release", "https://github.com/codebucketdev/Rly.li-URL-Shortener/releases/download/2.4-release/shortener-2.4-release.jar");
	
	public UpdateTask(UUID key, String url, Update current)
	{
		this.key = key;
		this.url = url;
		this.current = current;
	}
	
	public abstract void updateSucess(Update update);
	
	public void check()
	{
		new Thread(new Runnable()
		{	
			public void run() 
			{
				try 
				{
					URL url = new URL(UpdateTask.this.url);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					Update update = new Gson().fromJson(new InputStreamReader(connection.getInputStream()), Update.class);
					updateSucess(update);
				}
				catch(Exception ex)
				{
					updateSucess(null);
				}
			}
		}).start();		
	}
	
	public UUID getKey()
	{
		return key;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public Update getCurrent()
	{
		return current;
	}
	
	public static class Update
	{
		private String name;
		private String version;
		private String update;
		
		public Update(String name, String version, String update)
		{
			this.name = name;
			this.version = version;
			this.update = update;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getVersion()
		{
			return version;
		}
		
		public String getUpdate()
		{
			return update;
		}
		
		public static boolean compareVersions(String version, String update)
		{
			int[] x = getVersionNumbers(version), y = getVersionNumbers(update);
			for (int i = 0; i < (x.length > y.length ? x.length : y.length); i++)
			{
				if (!(i >= x.length || i >= y.length))
				{
					if (x[i] < y[i])
					{
						return true;
					}
				}
			}
			
			return (x.length < y.length);
		}
		
		private static int[] getVersionNumbers(String version)
		{
			char[] allowed = "0123456789.".toCharArray();
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < version.length(); i++)
			{
				char c = version.charAt(i);
				for (char ch : allowed) 
				{
					if(ch == c) 
					{
						builder.append(c);
					}
				}
			}
			
			String[] split = builder.toString().split("\\.");
			
			int[] numbers = new int[split.length];
			for (int i = 0; i < split.length; i++)
			{
				numbers[i] = Integer.parseInt(split[i]);
			}
			
			return numbers;
		}
	}
	
	public static class Download extends Observable implements Runnable
	{
		// Max size of download buffer.
		private static final int MAX_BUFFER_SIZE = 4096;

		// These are the status names.
		public static final String STATUSES[] = {"Downloading", "Paused", "Complete", "Cancelled", "Error"};

		// These are the status codes.
		public static final int DOWNLOADING = 0;
		public static final int PAUSED = 1;
		public static final int COMPLETE = 2;
		public static final int CANCELLED = 3;
		public static final int ERROR = 4;
		
		public static final double NANOS_PER_SECOND = 1000000000.0;
		public static final double BYTES_PER_MIB = 1024 * 1024;

		private URL url; // download URL
		private int size; // size of download in bytes
		private int downloaded; // number of bytes downloaded
		private int status; // current status of download
		
		private File target;

		public Download(URL url)
		{
			this(url, null);
		}
		
		// Constructor for Download.
		public Download(URL url, File target)
		{
		    this.url = url;
		    this.size = -1;		    
		    this.target = target;

		    // Begin the download.
		    status = DOWNLOADING;
		    downloaded = 0;
		    download();
		}

		// Get this download's URL.
		public String getUrl()
		{
		    return url.toString();
		}

		// Get this download's size.
		public int getSize()
		{
		    return size;
		}
		
		public int getDownloaded()
		{
			return downloaded;
		}

		// Get this download's progress.
		public float getProgress() 
		{
		    return ((float) downloaded / size) * 100;
		}

		// Get this download's status.
		public int getStatus()
		{
		    return status;
		}

		// Pause this download.
		public void pause() 
		{
		    status = PAUSED;
		    stateChanged();
		}

		// Resume this download.
		public void resume() 
		{
		    status = DOWNLOADING;
		    stateChanged();
		    download();
		}

		// Cancel this download.
		public void cancel()
		{
		    status = CANCELLED;
		    stateChanged();
		}

		// Mark this download as having an error.
		private void error() 
		{
		    status = ERROR;
		    stateChanged();
		}

		// Start or resume downloading.
		private void download() 
		{
			new Thread(this).start();
		}

		// Get file name portion of URL or absolute path of target.
		public File getTarget() 
		{
		    return (target != null ? new File(target.getAbsolutePath()) : new File(url.getFile().substring(url.getFile().lastIndexOf('/') + 1)));
		}
		
		// Get file name portion of Download.
		public String getFileName() 
		{
		    return getTarget().getName();
		}

		// Download file.
		public void run() 
		{
		    RandomAccessFile file = null;
		    InputStream stream = null;

		    try 
		    {
		        // Open connection to URL.
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		        // Specify what portion of file to download.
		        connection.setRequestProperty("Range", "bytes=" + downloaded + "-");

		        // Connect to server.
		        connection.connect();

		        // Make sure response code is in the 200 range.
		        if (connection.getResponseCode() / 100 != 2) 
		        {
		            error();
		        }

		        // Check for valid content length.
		        int contentLength = connection.getContentLength();
		        if (contentLength < 1) 
		        {
		            error();
		        }

		        /* Set the size for this download if it
		     	   hasn't been already set. */
		        if (size == -1) 
		        {
		            size = contentLength;
		            stateChanged();
		        }

		        // Open file and seek to the end of it.
		        file = new RandomAccessFile(getTarget().getAbsolutePath(), "rw");
		        file.seek(downloaded);

		        stream = connection.getInputStream();
		        while (status == DOWNLOADING) 
		        {
		        /* Size buffer according to how much of the
		           file is left to download. */
		            byte buffer[];
		            if (size - downloaded > MAX_BUFFER_SIZE) 
		            {
		                buffer = new byte[MAX_BUFFER_SIZE];
		            } 
		            else
		            {
		                buffer = new byte[size - downloaded];
		            }

		            // Read from server into buffer.
		            int read = stream.read(buffer);
		            if (read == -1)
		                break;

		            // Write buffer to file.
		            file.write(buffer, 0, read);
		            downloaded += read;
		            stateChanged();
		        }

		        /* Change status to complete if this point was
		    	   reached because downloading has finished. */
		        if (status == DOWNLOADING) 
		        {
		            status = COMPLETE;
		            stateChanged();
		        }
		    } 
		    catch (Exception e) 
		    {
		        error();
		    } 
		    finally 
		    {
		        // Close file.
		        if (file != null) 
		        {
		            try 
		            {
		                file.close();
		            } 
		            catch (Exception e) {}
		        }

		        // Close connection to server.
		        if (stream != null) 
		        {
		            try 
		            {
		                stream.close();
		            }
		            catch (Exception e) {}
		        }
		    }
		}

		// Notify observers that this download's status has changed.
		private void stateChanged()
		{
		    setChanged();
		    notifyObservers();
		}
	}
}
