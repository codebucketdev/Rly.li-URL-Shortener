package de.codebucket.shortener.capture;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import de.codebucket.shortener.MainWindow;

public class ScreenCaptureUnix extends ScreenCaptureAdapterUnix
{
	private static final long serialVersionUID = 1L;

	private BufferedImage image;

	private int startY, startX;
	private int x, y;
	private int width, height;
	private int endX, endY;

	private boolean finished;

	public ScreenCaptureUnix()
	{
		System.setProperty("sun.java2d.noddraw", Boolean.TRUE.toString());
		createAndShowGui();

		try
		{
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex)
		{
			MainWindow.showExceptionInfo(ex);
		}

		GlobalScreen.addNativeMouseListener(new Listener());
	}

	private void createAndShowGui()
	{
		setBounds(0, 0, 10, 10);
		setUndecorated(true);
		setResizable(false);
		setVisible(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		setAlwaysOnTop(true);

		addMouseMotionListener(this);
		addKeyListener(this);

		setOpacity(0.4f);
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		endX = e.getXOnScreen();
		endY = e.getYOnScreen();
		x = Math.min(startX, endX);
		y = Math.min(startY, endY);
		width = Math.abs(startX - endX);
		height = Math.abs(startY - endY);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			x = startX = endX;
			y = startY = endY;
			width = 0;
			height = 0;
		}
		else if (e.getKeyCode() == KeyEvent.VK_CONTROL || e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_F)
		{
			final Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
			x = 0;
			y = 0;
			width = resolution.width;
			height = resolution.height;
		}
		else
		{
			dispose();
		}
	}

	@Override
	public BufferedImage call()
	{
		while (!finished)
		{
			setBounds(x, y, width, height);
			Toolkit.getDefaultToolkit().sync();
			
			try
			{
				Thread.sleep(15); // 1000 / 15 = about 60 Frames per second
			}
			catch (InterruptedException ex) {}
		}
		
		dispose();
		
		try
		{
			GlobalScreen.unregisterNativeHook();
		}
		catch (NativeHookException ex) {}
		return image;
	}

	private class Listener implements NativeMouseListener
	{
		@Override
		public void nativeMousePressed(NativeMouseEvent e)
		{
			if (e.getButton() == NativeMouseEvent.BUTTON2)
			{
				dispose();
			}

			startX = e.getX();
			startY = e.getY();
			setBounds(startX, startY, 50, 50);
		}

		@Override
		public void nativeMouseReleased(NativeMouseEvent e)
		{
			setVisible(false);
			final Rectangle res = new Rectangle(x, y, width, height);
			
			try
			{
				image = new Robot().createScreenCapture(res);
			}
			catch (AWTException ex)
			{
				MainWindow.showExceptionInfo(ex);
			}
			
			finished = true;
		}

		@Override
		public void nativeMouseClicked(NativeMouseEvent e) {}
	}
}
