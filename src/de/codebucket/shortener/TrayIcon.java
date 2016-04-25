package de.codebucket.shortener;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import me.nrubin29.pastebinapi.CreatePaste;
import me.nrubin29.pastebinapi.ExpireDate;
import me.nrubin29.pastebinapi.Format;
import me.nrubin29.pastebinapi.PrivacyLevel;
import me.nrubin29.pastebinapi.User;

import com.google.gson.JsonSyntaxException;

import de.codebucket.shortener.api.CallbackResponse;
import de.codebucket.shortener.api.ClientHandler;
import de.codebucket.shortener.api.ImageUploader;
import de.codebucket.shortener.api.URLShortener;
import de.codebucket.shortener.capture.ScreenCapture;
import de.codebucket.shortener.utils.AudioPlayer;
import de.codebucket.shortener.utils.FrameRunner;
import de.codebucket.shortener.utils.ImageUtils;

public class TrayIcon extends java.awt.TrayIcon
{
	private CallbackResponse response;
	
	private JDialog hiddenDialog;
	private JMenu[] recentUploads;
	private Settings settings = Settings.getInstance();
	private MainWindow main = MainWindow.getInstance();
	
	public TrayIcon(Image image)
	{
		super(image);
		createTrayIcon();
	}
	
	public TrayIcon(Image image, String title)
	{
		super(image, title);
		createTrayIcon();
	}
	
	public void createTrayIcon()
	{
    	if (!SystemTray.isSupported())
    	{
    		return;
    	}
    	
    	final JPopupMenu popupMenu = new JPopupMenu();
    	popupMenu.setName("PopupMenu");
    	
    	JMenuItem title = new JMenuItem("Rly.li URL Shortener 2.4-release");
    	title.setFont(new Font(title.getFont().getName(), Font.BOLD, title.getFont().getSize()));
    	title.setEnabled(false);
    	popupMenu.add(title);
    	
    	JMenuItem link = new JMenuItem("Check for Updates");
    	link.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent evt) 
			{
				try 
				{
					FrameRunner.run(UpdaterWindow.class);
				} 
				catch (Exception ex)
				{
					MainWindow.showExceptionInfo(ex);
				}
			}
		});
    	popupMenu.add(link);    	
    	popupMenu.addSeparator();
    	
    	JMenuItem recents = new JMenuItem("Recent Uploads");
    	recents.setEnabled(false);
    	popupMenu.add(recents);
    	
    	try
    	{
    		recentUploads = new JMenu[5];
			CallbackResponse[] history = ClientHandler.getRecentUploads(settings.getClientId());
			
			for (int i = 0; i < Math.min(5, history.length); i++)
			{
				JMenu menu = new JMenu();
				menu.setVisible(false);
				
				if (i < history.length)
				{
					CallbackResponse entry = history[i];
					menu.setText(entry.getShortCode());
					
					JMenuItem preview = new JMenuItem("<html><img src='http://img.rly.li/l0br/1orbam.png'></html>");
					preview.setHorizontalTextPosition(SwingConstants.CENTER);
					menu.add(preview);
					
					JMenuItem uploaded = new JMenuItem("Uploaded: " + entry.getDateCreated().toString());
					uploaded.setEnabled(false);
					menu.add(uploaded);
					
					JMenuItem views = new JMenuItem("Views: " + entry.getClicks());
					views.setEnabled(false);
					menu.add(views);
					
					JMenuItem open = new JMenuItem("Open in browser");
					menu.add(open);
					
					JMenuItem copy = new JMenuItem("Copy link to clipboard");
					menu.add(copy);
					
					if (entry.getShortCode().contains("/"))
					{
						JMenuItem delete = new JMenuItem("Delete");
						menu.addSeparator();
						menu.add(delete);
					}
					
					menu.setVisible(true);
				}
				
		    	popupMenu.add(menu);
		    	recentUploads[i] = menu;
			}
		}
    	catch (IOException | JsonSyntaxException ex)
    	{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
    	
    	popupMenu.addSeparator();
    	
    	JMenuItem captureWindow = new JMenuItem("Capture Current Window");
    	captureWindow.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent evt) 
			{
				// TODO Auto-generated catch block
				// captureWindow();
			}
		});
    	popupMenu.add(captureWindow);
    	
    	JMenuItem captureDesktop = new JMenuItem("Capture Desktop");
    	captureDesktop.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent evt) 
			{
				captureDesktop();
			}
		});
    	popupMenu.add(captureDesktop);
    	
    	JMenuItem captureArea = new JMenuItem("Capture Area...");
    	captureArea.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent evt) 
			{
				captureArea();
			}
		});
    	popupMenu.add(captureArea);
    	
    	JMenuItem uploadClipboard = new JMenuItem("Upload Clipboard");
    	uploadClipboard.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent evt) 
			{
				uploadClipboard();
			}
		});
    	popupMenu.add(uploadClipboard);
    	
    	JMenuItem uploadFile = new JMenuItem("Upload File...");
    	uploadFile.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent evt) 
			{
				// TODO Auto-generated catch block
				// uploadFile();
			}
		});
    	popupMenu.add(uploadFile);
    	popupMenu.addSeparator();
    	
    	JMenuItem restore = new JMenuItem("Restore Window");
    	restore.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent evt) 
			{
				if (main.isVisible())
				{
					return;
				}
				
				main.setVisible(true);
			}
		});
    	popupMenu.add(restore);
    	
    	JMenuItem settings = new JMenuItem("Settings...");
    	settings.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent evt) 
			{
				if (main.getWindow(SettingsWindow.class) == null)
				{
					SettingsWindow window = (SettingsWindow) main.openWindow(SettingsWindow.class);
					FrameRunner.centerWindow(window);
				}
				else
				{
					final Window window = main.getWindow(SettingsWindow.class);
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
    	popupMenu.add(settings);
    	popupMenu.addSeparator();
    	
    	JMenuItem exit = new JMenuItem("Exit");
    	exit.addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent evt) 
			{
				SystemTray.getSystemTray().remove(TrayIcon.this);
				System.exit(0);
			}
    	});
    	popupMenu.add(exit);
    	
    	setImageAutoSize(true);
    	addMouseListener(new MouseAdapter()
    	{	    		
    		@Override
    		public void mouseClicked(MouseEvent evt) 
    		{
    			if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)
    			{
    				response = null;
    				
    	        	if (main.isVisible())
	    			{
	    				main.setVisible(false);
	    				return;
	    			}
	    			
    	        	Object content = readClipboard();
	    			if (content == null)
	    			{
	    				main.setVisible(true);
	    				return;
	    			}
	    			
	    			if (content instanceof String && isValidUrl((String) content))
	    			{
	    				String url = (String) content;
	    				shortenUrl(url);
	    			}
	    			else if (content instanceof BufferedImage)
	    			{
	    				BufferedImage img = (BufferedImage) content;
	    				uploadImage(img);
	    			}
	    			else
	    			{
	    				main.setVisible(true);
	    			}
    			}
    			else if (evt.getButton() == MouseEvent.BUTTON3 && evt.getClickCount() == 1)
    			{
	    			Rectangle bounds = getSafeScreenBounds(evt.getPoint());
                    Point point = evt.getPoint();
                    int x = point.x, y = point.y;
                    
                    if (y < bounds.y)
                    {
                        y = bounds.y;
                    }
                    else if (y > bounds.y + bounds.height)
                    {
                        y = bounds.y + bounds.height;
                    }
                    
                    if (x < bounds.x)
                    {
                        x = bounds.x;
                    }
                    else if (x > bounds.x + bounds.width)
                    {
                        x = bounds.x + bounds.width;
                    }
                    
                    if (x + popupMenu.getPreferredSize().width > bounds.x + bounds.width)
                    {
                        x = (bounds.x + bounds.width) - popupMenu.getPreferredSize().width;
                    }
                    
                    if (y + popupMenu.getPreferredSize().height > bounds.y + bounds.height)
                    {
                        y = (bounds.y + bounds.height) - popupMenu.getPreferredSize().height;
                    }
                    
                    popupMenu.setLocation(x, y);
                    popupMenu.setInvoker(popupMenu);
                    popupMenu.setVisible(true);
    			}
    	     }
		});
    	
    	addActionListener(new ActionListener()
    	{
			@Override
			public void actionPerformed(ActionEvent evt) 
			{
				new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{
						try
						{
							Thread.sleep(170L);
						}
						catch (InterruptedException ex) {}
						
						if (response != null)
	    				{
	    					try 
	    					{
								openWebsite(new URL(response.getLink()));
							} 
	    					catch (Exception ex) {}
	    				}
					}
				}).start();	
			}
		});

    	try 
    	{
    		SystemTray.getSystemTray().add(this);
    	}
    	catch(AWTException ex) {}
	}
	
	public Object readClipboard()
	{
		if (getClipboardData(DataFlavor.stringFlavor) != null)
		{
			return (String) getClipboardData(DataFlavor.stringFlavor);
		}
		else if (getClipboardData(DataFlavor.imageFlavor) != null)
		{
			return (BufferedImage) getClipboardData(DataFlavor.imageFlavor);
		}
		
		return null;
	}
	
	private Object getClipboardData(DataFlavor flavor)
	{
		try
		{
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			return clipboard.getData(flavor);
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	private boolean isValidUrl(String url)
	{
		try
		{
			new URL(url);
		}
		catch (Exception ex)
		{
			return false;
		}
		
		return true;
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
	
	public void captureDesktop()
	{
		new Thread(new Runnable()
		{
	        @Override
	        public void run()
	        {
	        	try
	        	{
					Thread.sleep(250L);
				}
	        	catch (InterruptedException ex) {}
	        	
	        	final Rectangle resolution = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	        	BufferedImage image = null;
	        	
	        	try
	        	{
		        	
	        		image = new Robot(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()).createScreenCapture(resolution);
	        	}
	        	catch (Exception ex)
	        	{
	        		MainWindow.showExceptionInfo(ex);
	        	}
	        	
	        	if (settings.isSaveImage())
	        	{
	        		String data = ImageUtils.encodeToString(image, "PNG");
	        		String extension = "png";
	        		
	        		if (settings.isSmartQuality())
	        		{
	        			String img = ImageUtils.encodeToString(image, "JPG");
	        			if (img.length() < data.length())
	        			{
	        				data = img;
	        				extension = "jpg";
	        			}
	        		}
	        		
	        		BufferedImage img = ImageUtils.decodeToImage(data);
	        		File file = new File(settings.getSavePath(), "ss (" + new SimpleDateFormat("yyyy-MM-dd 'at' HH.mm.ss").format(Calendar.getInstance().getTime()) + ")." + extension);
	        		
	        		File dir = file.getParentFile();
	        		if (!dir.exists())
	        		{
	        			dir.mkdirs();
	        		}
	        		
	        		try
	        		{
						ImageIO.write(img, extension.toUpperCase(), file);
					}
	        		catch (IOException ex)
	        		{
						MainWindow.showExceptionInfo(ex);
					}
	        	}
	        	
	        	uploadImage(image);
	        }
	   }).start();
	}
	
	public void captureArea()
	{
		new Thread(new Runnable()
		{
	        @Override
	        public void run()
	        {
	        	try
	        	{
					Thread.sleep(250L);
				}
	        	catch (InterruptedException ex) {}
	        	
	        	BufferedImage image = new ScreenCapture().call();
	        	
	        	if (settings.isSaveImage())
	        	{
	        		String data = ImageUtils.encodeToString(image, "PNG");
	        		String extension = "png";
	        		
	        		if (settings.isSmartQuality())
	        		{
	        			String img = ImageUtils.encodeToString(image, "JPG");
	        			if (img.length() < data.length())
	        			{
	        				data = img;
	        				extension = "jpg";
	        			}
	        		}
	        		
	        		BufferedImage img = ImageUtils.decodeToImage(data);
	        		File file = new File(settings.getSavePath(), "ss (" + new SimpleDateFormat("yyyy-MM-dd 'at' HH.mm.ss").format(Calendar.getInstance().getTime()) + ")." + extension);
	        		
	        		File dir = file.getParentFile();
	        		if (!dir.exists())
	        		{
	        			dir.mkdirs();
	        		}
	        		
	        		try
	        		{
						ImageIO.write(img, extension.toUpperCase(), file);
					}
	        		catch (IOException ex)
	        		{
						MainWindow.showExceptionInfo(ex);
					}
	        	}
	        	
	    		uploadImage(image);
	        }
	   }).start();
	}
	
	public void uploadClipboard()
	{
		Object content = readClipboard();	
		if (content == null)
		{
			return;
		}
		
		if (content instanceof String && isValidUrl((String) content))
		{
			String url = (String) content;
			shortenUrl(url);
		}
		else if (content instanceof BufferedImage)
		{
			BufferedImage image = (BufferedImage) content;
			
			if (settings.isSaveImage())
        	{
        		String data = ImageUtils.encodeToString(image, "PNG");
        		String extension = "png";
        		
        		if (settings.isSmartQuality())
        		{
        			String img = ImageUtils.encodeToString(image, "JPG");
        			if (img.length() < data.length())
        			{
        				data = img;
        				extension = "jpg";
        			}
        		}
        		
        		BufferedImage img = ImageUtils.decodeToImage(data);
        		File file = new File(settings.getSavePath(), "clip (" + new SimpleDateFormat("yyyy-MM-dd 'at' HH.mm.ss").format(Calendar.getInstance().getTime()) + ")." + extension);
        		
        		File dir = file.getParentFile();
        		if (!dir.exists())
        		{
        			dir.mkdirs();
        		}
        		
        		try
        		{
					ImageIO.write(img, extension.toUpperCase(), file);
				}
        		catch (IOException ex)
        		{
					MainWindow.showExceptionInfo(ex);
				}
        	}
			
			uploadImage(image);
		}
		else
		{
			if (main.getAccount() == null)
			{
				return;
			}
				 
			try
			{
				User account = main.getAccount();
				CreatePaste paste = account.createPaste()
								.withName("clip (" + new SimpleDateFormat("yyyy-MM-dd 'at' HH.mm.ss").format(Calendar.getInstance().getTime()) + ")")
								.withFormat(Format.None)
								.withPrivacyLevel(PrivacyLevel.PUBLIC)
								.withExpireDate(ExpireDate.NEVER)
								.withText((String) content);
				
				String pasteUrl = paste.post();
				shortenUrl(pasteUrl);
			}
			catch (Exception ex)
			{
				MainWindow.showExceptionInfo(ex);
			}
		}
	}
	
	public void uploadImage(BufferedImage image)
	{
		try
		{
			final CallbackResponse callback = ImageUploader.uploadImage(image);
			
			if (callback != null && callback.isSuccess())
			{
				if (settings.isCopyLink())
				{
					StringSelection stringSelection = new StringSelection((String) callback.getLink());
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, null);
				}
				
				displayMessage("Rly.li URL Shortener", "Your shortened link is: " + callback.getLink(), MessageType.INFO);
				
				if (settings.isPlaySound())
				{
					AudioPlayer.play("/notify.wav");
				}
				
				new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{
						try
						{
							Thread.sleep(250L);
						}
						catch (InterruptedException ex) {}
						
						response = callback;
					}
				}).start();
				
				if (settings.isOpenBrowser())
				{
					openWebsite(new URL(callback.getLink()));
				}
			}
			else if (callback.isError() && !callback.getError().equals("ServerRuntimeException"))
			{
				displayMessage("Rly.li URL Shortener", callback.getErrorMessage(), MessageType.WARNING);	
			}
			else
			{
				displayMessage("Rly.li URL Shortener", callback.getErrorMessage(), MessageType.ERROR);	
			}
		}
		catch (Exception ex)
		{
			MainWindow.showExceptionInfo(ex);
		}
	}
	
	public void shortenUrl(String url)
	{
		try
		{
			final CallbackResponse callback = URLShortener.shortenUrl(url);
			
			if (callback != null && callback.isSuccess())
			{
				if (settings.isCopyLink())
				{
					StringSelection stringSelection = new StringSelection((String) callback.getLink());
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, null);
				}
				
				displayMessage("Rly.li URL Shortener", "Your shortened link is: " + callback.getLink(), MessageType.INFO);
				
				if (settings.isPlaySound())
				{
					AudioPlayer.play("/notify.wav");
				}
				
				new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{
						try
						{
							Thread.sleep(250L);
						}
						catch (InterruptedException ex) {}
						
						response = callback;
					}
				}).start();
				
				if (settings.isOpenBrowser())
				{
					openWebsite(new URL(callback.getLink()));
				}
			}
			else if (callback.isError() && !callback.getError().equals("ServerRuntimeException"))
			{
				displayMessage("Rly.li URL Shortener", callback.getErrorMessage(), MessageType.WARNING);	
			}
			else
			{
				displayMessage("Rly.li URL Shortener", callback.getErrorMessage(), MessageType.ERROR);	
			}
		}
		catch (Exception ex)
		{
			MainWindow.showExceptionInfo(ex);
		}
	}
	
	public static Rectangle getSafeScreenBounds(Point pos) {

        Rectangle bounds = getScreenBoundsAt(pos);
        Insets insets = getScreenInsetsAt(pos);

        bounds.x += insets.left;
        bounds.y += insets.top;
        bounds.width -= (insets.left + insets.right);
        bounds.height -= (insets.top + insets.bottom);

        return bounds;

    }

    public static Insets getScreenInsetsAt(Point pos) {
        GraphicsDevice gd = getGraphicsDeviceAt(pos);
        Insets insets = null;
        if (gd != null) {
            insets = Toolkit.getDefaultToolkit().getScreenInsets(gd.getDefaultConfiguration());
        }
        return insets;
    }

    public static Rectangle getScreenBoundsAt(Point pos) {
        GraphicsDevice gd = getGraphicsDeviceAt(pos);
        Rectangle bounds = null;
        if (gd != null) {
            bounds = gd.getDefaultConfiguration().getBounds();
        }
        return bounds;
    }

    public static GraphicsDevice getGraphicsDeviceAt(Point pos) {

        GraphicsDevice device = null;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice lstGDs[] = ge.getScreenDevices();

        ArrayList<GraphicsDevice> lstDevices = new ArrayList<GraphicsDevice>(lstGDs.length);

        for (GraphicsDevice gd : lstGDs) {

            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            Rectangle screenBounds = gc.getBounds();

            if (screenBounds.contains(pos)) {

                lstDevices.add(gd);

            }

        }

        if (lstDevices.size() > 0) {
            device = lstDevices.get(0);
        } else {
            device = ge.getDefaultScreenDevice();
        }

        return device;

    }
}
