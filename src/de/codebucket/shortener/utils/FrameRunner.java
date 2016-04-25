package de.codebucket.shortener.utils;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;

import de.codebucket.shortener.utils.WindowUtils.LookAndFeel;

public class FrameRunner 
{
	static
	{
		WindowUtils.setLookAndFeel(LookAndFeel.getSystemLookAndFeel());
	}

	public static void centerWindow(Window frame) 
	{
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2.0D);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2.0D);
		frame.setLocation(x, y);
	}

	public static void run(Class<? extends Window> clazz) 
	{
		try
		{
			executeInstance((Window) clazz.newInstance());
		} 
		catch (InstantiationException ex)
		{
			ex.printStackTrace();
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}

	public static void run(Object obj) 
	{
		try 
		{
			executeInstance((Window) obj.getClass().newInstance());
		} 
		catch (InstantiationException ex)
		{
			ex.printStackTrace();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public static void run(Class<? extends Window> clazz, Class<?>[] args, Object[] param)
	{
		try 
		{
			executeInstance((Window) clazz.getConstructor(args).newInstance(param));
		} 
		catch (InstantiationException ex) 
		{
			ex.printStackTrace();
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
	}

	public static void executeInstance(final Window window) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			@Override
			public void run() 
			{
				try
				{
					WindowUtils.updateWindow(window);
					window.setAutoRequestFocus(window.isAutoRequestFocus());
					window.setAlwaysOnTop(window.isAlwaysOnTop());
				} 
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
	}
}
