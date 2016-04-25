package de.codebucket.shortener.capture;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import javax.swing.JDialog;

/**Adapter class for ScreenCapture without any fields*/
public abstract class ScreenCaptureAdapterUnix extends JDialog implements KeyListener, MouseMotionListener, Callable<BufferedImage>
{
	private static final long serialVersionUID = 1L;

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public abstract BufferedImage call() throws Exception;
}
