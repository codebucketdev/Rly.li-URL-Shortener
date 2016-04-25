package de.codebucket.shortener.capture;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;

import de.codebucket.shortener.MainWindow;
import de.codebucket.shortener.Settings;

public class ScreenCapture extends ScreenCaptureAdapter
{
	private static final long serialVersionUID = 1L;
	
	private Settings settings;

	private JDialog frame;
	private BufferedImage image;
	private final Color selection = new Color(140, 140, 140, 100);

	private int startY, startX;
	private int x, y;
	private int width, height;
	private int endX, endY;

	private boolean finished;

	public ScreenCapture()
	{
		final Rectangle res = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		this.settings = Settings.getInstance();
		
		try
		{
			image = new Robot(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()).createScreenCapture(res);
		}
		catch (AWTException ex)
		{
			MainWindow.showExceptionInfo(ex);
		}
		
		createAndShowGui(res);
	}

	private void createAndShowGui(Rectangle resolution)
	{
		frame = new JDialog();
		frame.setBounds(resolution);
		frame.setUndecorated(true);
		frame.add(this);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		frame.setAlwaysOnTop(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		frame.addKeyListener(this);
		setIgnoreRepaint(true);
		frame.repaint();
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		
		final Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(image, null, 0, 0);
		
		if (!settings.isDrawRectangle())
		{
			return;
		}
		
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(selection);
		g2.fillRect(x, y, width, height);
		g2.setColor(Color.WHITE);
		g2.drawRect(x, y, width, height);
		g2.setColor(Color.BLACK);
		g2.drawRect(x + 1, y + 1, width - 2, height - 2);
		g2.setColor(Color.GRAY);
		
		g2.drawString(String.valueOf(width), x + width - 29, y + height - 9);
		g2.drawString(String.valueOf(height), x + width - 29, y + height - 24);
		g2.setColor(Color.WHITE);
		
		//Those are just offset values
		g2.drawString(String.valueOf(width), x + width - 30, y + height - 10);
		g2.drawString(String.valueOf(height), x + width - 30, y + height - 25);

		g2.dispose();
		Toolkit.getDefaultToolkit().sync();
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException ex)
			{
				MainWindow.showExceptionInfo(ex);
			}
			
			frame.dispose();
		}
		
		startX = e.getX();
		startY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		image = image.getSubimage(x, y, width, height);
		finished = true;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		endX = e.getX();
		endY = e.getY();
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
			finished = true;
		}
		else
		{
			frame.dispose();
		}
	}

	@Override
	public BufferedImage call()
	{
		while (!finished)
		{
			repaint();
			
			try
			{
				Thread.sleep(15); // 1000 / 15 = about 60 Frames per second
			}
			catch (InterruptedException ex) {}
		}
		
		frame.dispose();
		return image;
	}
}
