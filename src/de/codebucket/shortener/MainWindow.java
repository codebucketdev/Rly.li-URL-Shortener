package de.codebucket.shortener;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import me.nrubin29.pastebinapi.PastebinAPI;
import me.nrubin29.pastebinapi.PastebinException;
import me.nrubin29.pastebinapi.User;
import de.codebucket.shortener.api.CallbackResponse;
import de.codebucket.shortener.api.URLShortener;
import de.codebucket.shortener.session.Session;
import de.codebucket.shortener.session.SessionManager;
import de.codebucket.shortener.utils.FrameRunner;

public class MainWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private Settings settings;
	
	private JTextField txtUrl = new JTextField();
	private JButton btnSubmit = new JButton();
	private JLabel lblText = new JLabel(), lblMessage = new JLabel();
	private TrayIcon trayIcon = null;
	private List<Window> frames = new ArrayList<Window>();
	
	private Session session;
	private User account;
	private CallbackResponse response;
	
	private static MainWindow window;
	
	public MainWindow()
	{
		MainWindow.window = this;
		this.settings = new Settings(new File(getUserDirectory(), "settings.properties"));
		
		if (SystemTray.isSupported())
		{
			trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/favicon.png")), "Rly.li URL Shortener");
		}
		
	    // Frame-Initialisierung
	    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    getContentPane().setBackground(SystemColor.control);
	    
	    int frameWidth = 450; 
	    int frameHeight = 95;
	    setSize(frameWidth, frameHeight);
	    
	    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (d.width - getSize().width) / 2;
	    int y = (d.height - getSize().height) / 2;
	    
	    setLocation(x, y);
	    setResizable(false);
	    
	    Container contentPane = getContentPane();
	    contentPane.setLayout(null);
	    
	    setTitle("Rly.li URL Shortener");
	    setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/favicon.png")));
	    
	    JLabel lblSettings = new JLabel("<html><a href=\"\">Open Settings</a></html>");
	    lblSettings.addMouseListener(new MouseAdapter()
	    {
	    	public void mouseClicked(MouseEvent e)
	    	{
	    		if (getWindow(SettingsWindow.class) == null)
				{
					SettingsWindow window = (SettingsWindow) openWindow(SettingsWindow.class);
					FrameRunner.centerWindow(window);
				}
				else
				{
					final Window window = getWindow(SettingsWindow.class);
					EventQueue.invokeLater(new Runnable() 
					{
					    @Override
					    public void run() 
					    {
					    	window.setVisible(true);
					    	window.toFront();
					    	window.repaint();
					    }
					});
				}
	    	}
		});
	    lblSettings.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    lblSettings.setFont(new Font(lblSettings.getFont().getName(), Font.PLAIN, 11));
	    lblSettings.setHorizontalAlignment(SwingConstants.RIGHT);
	    lblSettings.setBounds(361, 4, 73, 18);
	    getContentPane().add(lblSettings);
	    
	    lblText.setBounds(8, 7, 343, 21);
	    lblText.setText("Paste your long URL here:");
	    lblText.setOpaque(true);
	    lblText.setFont(new Font(lblText.getFont().getName(), Font.PLAIN, 12));
	    contentPane.add(lblText);
	    
	    txtUrl.setText("http://");
	    txtUrl.setBackground(SystemColor.text);
	    txtUrl.setBounds(8, 31, 343, 25);
	    txtUrl.setFont(new Font(UIManager.getFont("TextArea.font").getName(), Font.PLAIN, 12));
	    contentPane.add(txtUrl);
	    
	    btnSubmit.setBounds(361, 31, 73, 25);
	    btnSubmit.setText("Shorten");
	    btnSubmit.setMargin(new Insets(2, 2, 2, 2));
	    btnSubmit.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent evt)
		    {
	    		String url = txtUrl.getText();
	    		shortenUrl(url);
		    }
	    });
	    btnSubmit.setFont(new Font(btnSubmit.getFont().getName(), Font.PLAIN, 12));
	    btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    contentPane.add(btnSubmit);
	    
	    lblMessage.setToolTipText("Click here to copy the URL to the clipboard");
	    lblMessage.addMouseListener(new MouseAdapter() 
	    {
	    	@Override
	    	public void mouseClicked(MouseEvent arg0)
	    	{
	    		if (response == null || !response.isSuccess())
	    		{
	    			Toolkit.getDefaultToolkit().beep();
	    			return;
	    		}
	    		
	    		StringSelection stringSelection = new StringSelection(response.getLink());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
				
				if (settings.isOpenBrowser())
				{
					try
					{
						openWebsite(new URL(response.getLink()));
					}
					catch (Exception ex) {}
				}
	    	}
	    });
	    lblMessage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    lblMessage.setBounds(8, 65, 426, 25);
	    lblMessage.setText("<html><b>Done!</b> Your shortened link is: <a href=\"null\">null</a></html>");
	    lblMessage.setOpaque(true);
	    lblMessage.setFont(new Font(lblMessage.getFont().getName(), Font.PLAIN, 13));
	    lblMessage.setHorizontalAlignment(SwingConstants.LEFT);
	    contentPane.add(lblMessage);
	    
	    int errorCount = 0;
	    
	    while (session == null)
		{
			try
			{
				session = SessionManager.getSession(settings.getClientId());
				
				if (session.isSuccess())
				{
					settings.setClientId(session.getClientId());
					settings.saveSettings();
				}
				else
				{
					if (getInstance().trayIcon == null || getInstance().isVisible())
					{
						JOptionPane.showMessageDialog(getInstance(), session.getErrorMessage(), "Rly.li URL Shortener", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						getInstance().trayIcon.displayMessage("Rly.li URL Shortener", session.getErrorMessage(), MessageType.ERROR);
					}
					
					System.exit(0);
				}
				
				if (settings.getPastebinKey().length() == 32 && settings.getPastebinUsername().length() > 0 && settings.getPastebinPassword().length() > 0)
				{
					PastebinAPI api = new PastebinAPI(settings.getPastebinKey());
					account = api.getUser(settings.getPastebinUsername(), settings.getPastebinPassword());
				}
			}
			catch (PastebinException ex)
			{
				MainWindow.showExceptionInfo(ex);
			}
			catch (Exception ex)
			{
				errorCount++;
				
				if (errorCount == 12)
				{
					MainWindow.showExceptionInfo(ex);
				}
				
				try
				{
					Thread.sleep(5000L);
				}
				catch (InterruptedException e) {}
			}
		}
	    
	    if (trayIcon == null)
	    {
	    	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    	setVisible(true);
	    }
	}
  
	public void shortenUrl(final String url)
	{
		btnSubmit.setEnabled(false);
		
		if(isExpanded())
		{
			while(getHeight() > 95)
			{
				setBounds(getX(), getY(), getWidth(), getHeight() - 5);
				
				try
				{
					Thread.sleep(50l);
				} 
				catch (InterruptedException e) {}
			}
			
			lblMessage.setVisible(false);
		}
		
		this.response = null;
		
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				try
				{
					CallbackResponse callback = URLShortener.shortenUrl(url);
					
					if (callback != null && callback.isSuccess())
					{
						lblMessage.setText("<html><b>Done!</b> Your shortened link is: <a href=\"" + callback.getLink() + "\">" + callback.getLink() + "</a><html>");
						response = callback;
						
						if(!isExpanded())
						{
							MainWindow.this.expandWindow();
						}
					}
					else if (callback.isError() && !callback.getError().equals("ServerRuntimeException"))
					{
						JOptionPane.showMessageDialog(MainWindow.this, callback.getErrorMessage(), "Rly.li URL Shortener", JOptionPane.WARNING_MESSAGE);
					}
					else
					{
						JOptionPane.showMessageDialog(MainWindow.this, callback.getErrorMessage(), "Rly.li URL Shortener", JOptionPane.ERROR_MESSAGE);
					}
				}
				catch (Exception ex)
				{
					MainWindow.showExceptionInfo(ex);
				}
				
				btnSubmit.setEnabled(true);
			}
		}).start();
	}
	
	public void openWebsite(URL url) throws URISyntaxException 
	{
		openWebsite(url.toURI());
	}
	
	private void openWebsite(URI uri)
	{
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) 
	    {
	        try 
	        {
	            desktop.browse(uri);
	        } 
	        catch (Exception ex)
	        {
	            ex.printStackTrace();
	        }
	    }
	}
	
	private void expandWindow()
	{
		lblMessage.setVisible(true);
		
		while(getHeight() < 125)
		{
			setBounds(getX(), getY(), getWidth(), getHeight() + 5);
			
			try
			{
				Thread.sleep(50l);
			} 
			catch (InterruptedException e) {}
		}
	}
	
	private boolean isExpanded()
	{
		return !(getBounds().getWidth() == 450.0 && getBounds().getHeight() == 95.0);
	}
	
	public Window openWindow(Class<? extends Window> window, Object... args)
	{
		try
		{
			List<Class<?>> classes = new ArrayList<Class<?>>();
			for(Object arg : args)
			{
				classes.add(arg.getClass());
			}
			
			Class<?>[] parameterTypes = classes.toArray(new Class<?>[classes.size()]);
			Window frame = (Window) window.getConstructor(parameterTypes).newInstance(args);
			frame.setVisible(true);
			
			frames.add(frame);
			return frame;
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	public void closeWindow(Window window)
	{
		if(isWindowOpen(window))
		{
			window.setVisible(false);
			window.dispose();
			frames.remove(window);
		}
	}
	
	public void closeWindows()
	{
		for(Window w : getWindowsList())
		{
			w.setAlwaysOnTop(false);
			w.setVisible(false);
			w.dispose();
		}
		frames.clear();
	}
	
	public boolean isWindowOpen(Window window)
	{
		return this.frames.contains(window);
	}
	
	public Window getWindow(Class<? extends Window> window)
	{
		for(Window w : getWindowsList())
		{
			if(w.getClass().equals(window))
			{
				return w;
			}
		}
		return null;
	}
	
	public List<Window> getWindowsList()
	{
		return frames;
	}
	
	public Session getSession()
	{
		return session;
	}
	
	public User getAccount()
	{
		return account;
	}
	
	public File getUserDirectory()
	{
		File dir = new File(System.getProperty("user.home") + File.separator + ".shortener" + File.separator);
		if(!dir.exists())
		{
			dir.mkdir();
		}
		
		return dir;
	}
	
	public static void showExceptionInfo(Exception ex)
	{
		if (getInstance().trayIcon == null || getInstance().isVisible())
		{
			JOptionPane.showMessageDialog(getInstance(), ex.toString(), "Rly.li URL Shortener", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			getInstance().trayIcon.displayMessage("Rly.li URL Shortener", ex.toString(), MessageType.ERROR);
		}
		
		throw new RuntimeException(ex);
	}
	
	public static MainWindow getInstance()
	{
		return window;
	}
	
	public static void main(String[] args)
	{
		FrameRunner.run(MainWindow.class);
	}
}
