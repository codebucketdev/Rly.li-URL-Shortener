package de.codebucket.shortener.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileManager 
{
	public static String[] readFile(File file)
	{
		List<String> list = new ArrayList<String>();
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
		    for (String line; (line = br.readLine()) != null; ) 
		    {
		        list.add(line);
		    }

		    br.close();
		} 
		catch (FileNotFoundException ignored) {}
		catch (IOException ignored) {}
		return list.toArray(new String[list.size()]);
	}
	
	public static byte[] readBytes(File file)
	{
		byte[] buffer = new byte[(int)file.length()];
		try
		{
			FileInputStream input = new FileInputStream(file);
			input.read(buffer);
			input.close();
		}
		catch (IOException ignored) {}
		return buffer;
	}
	
	public static String[] readResource(String resource)
	{
		List<String> list = new ArrayList<String>();
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(FileManager.class.getResourceAsStream(resource)));
		    for (String line; (line = br.readLine()) != null; ) 
		    {
		        list.add(line);
		    }
		} 
		catch (FileNotFoundException ignored) {}
		catch (IOException ignored) {}
		return list.toArray(new String[list.size()]);
	}
	
	public static byte[] getResource(String resource)
	{
		try
		{
			InputStream input = FileManager.class.getResourceAsStream(resource);
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			int reads = input.read(); 
			
			while(reads != -1)
			{ 
				baos.write(reads); 
				reads = input.read(); 
			}

			return baos.toByteArray();
		}
		catch (IOException ignored) {}
		return null;
	}
	
	public static void saveResource(File folder, String resource)
	{
		File file = new File(folder, resource);
		if (file.exists())
		{
			writeBytes(file, getResource(resource));
			return;
		}
		
		File dir = file.getParentFile();
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		
		try 
		{
			file.createNewFile();
		} 
		catch (IOException ex) 
		{
			ex.printStackTrace();
			return;
		}

		writeBytes(file, getResource(resource));
	}
	
	public static void clearFile(File file)
	{
		try 
		{
			PrintWriter writer = new PrintWriter(file);
			writer.print("");
			writer.close();
		} 
		catch (FileNotFoundException ignored) {}
	}
	
	public static boolean createFile(File file)
	{
		try 
		{
			return file.createNewFile();
		} 
		catch (IOException ignored) {}
		return false;
	}
	
	public static void writeFile(File file, String text)
	{
	    writeFile(file, new String[]{ text });
	}
	
	public static void writeFile(File file, String[] i)
	{
	    BufferedWriter buffwriter;
	    FileWriter filewriter;
	    
	    try 
	    {
	    	filewriter = new FileWriter(file, true);
	    	buffwriter = new BufferedWriter(filewriter);

	    	for (String s : i)
	    	{
	    		buffwriter.write(s);
	    		buffwriter.newLine();
	    	}

	    	buffwriter.flush();
	    	filewriter.close();
	    	buffwriter.close();
	    }
	    catch (IOException ignored) {}
	}
	
	public static void writeBytes(File file, byte[] data)
	{
	    try 
	    {
	    	FileOutputStream output = new FileOutputStream(file);
	    	output.write(data);
	    	output.close();
	    }
	    catch (IOException ignored) {}
	}
}
