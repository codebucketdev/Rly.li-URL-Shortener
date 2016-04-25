package de.codebucket.shortener.capture;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

/**Adapter class for ScreenCapture without any fields*/
public abstract class ScreenCaptureAdapter extends JPanel implements KeyListener, MouseInputListener, Callable<BufferedImage>
{
	private static final long serialVersionUID = 1L;

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

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
