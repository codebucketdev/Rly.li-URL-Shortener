package de.codebucket.shortener.utils;

import java.awt.Window;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class WindowUtils 
{
	public static String getCurrentLook() 
	{
        return UIManager.getLookAndFeel().getName();
    }

    public static void setLookAndFeel(LookAndFeel look) 
    {
        try
        {
        	UIManager.setLookAndFeel(look.getClassName());
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
    }

    public static void updateWindow(Window window) 
    {
    	SwingUtilities.updateComponentTreeUI(window);
    }
    
    public enum LookAndFeel
    {
    	DEFAULT(UIManager.getCrossPlatformLookAndFeelClassName()),
    	SYSTEM(UIManager.getSystemLookAndFeelClassName()),
    	GTK("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"),
    	METAL("javax.swing.plaf.metal.MetalLookAndFeel"),
    	NIMBUS("javax.swing.plaf.nimbus.NimbusLookAndFeel"),
    	MOTIF("com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
    	WINDOWS("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"),
    	WINDOWS_CLASSIC("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"),
    	MACOSX("apple.laf.AquaLookAndFeel");
    	
    	private String className;
    	
    	LookAndFeel(String className)
    	{
    		this.className = className;
    	}
    	
    	public String getClassName()
    	{
    		return className;
    	}
    	
    	public static LookAndFeel getSystemLookAndFeel()
    	{
    		try
            {
    			return LookAndFeel.SYSTEM;
            }
    		catch (Exception ex)
            {
            	return LookAndFeel.DEFAULT;
            }
    	}
    }
}
