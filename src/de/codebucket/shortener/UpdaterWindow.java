package de.codebucket.shortener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import de.codebucket.shortener.utils.FileManager;
import de.codebucket.shortener.utils.UpdateTask;
import de.codebucket.shortener.utils.UpdateTask.Download;

public class UpdaterWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private Download download;
	private boolean cancelled;
	
	public UpdaterWindow()
	{
		// Frame-Initialisierung
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    getContentPane().setBackground(SystemColor.control);
	    
	    int frameWidth = 450;
	    int frameHeight = 200;
	    setSize(frameWidth, frameHeight);
		
	    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (d.width - getSize().width) / 2;
	    int y = (d.height - getSize().height) / 2;
	    
	    setLocation(x, y);
	    setResizable(false);
	    
	    Container contentPane = getContentPane();
	    contentPane.setLayout(null);
	    
	    setTitle("Updater");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/favicon.png")));
		
		final JLabel lblStatus = new JLabel();
		lblStatus.setFont(new Font(UIManager.getFont("ToolBar.font").getName(), UIManager.getFont("ToolBar.font").getStyle(), 16));
		lblStatus.setIcon(new ImageIcon(UpdaterWindow.class.getResource("/gif_load.gif")));
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setBounds(12, 12, 420, 80);
		contentPane.add(lblStatus);
		
		final JLabel lblDetails = new JLabel();
		lblDetails.setHorizontalAlignment(SwingConstants.CENTER);
		lblDetails.setFont(new Font(UIManager.getFont("TextField.font").getName(), UIManager.getFont("TextField.font").getStyle(), 12));
		lblDetails.setBounds(12, 107, 420, 17);
		contentPane.add(lblDetails);
		setVisible(true);
		
		final JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBounds(12, 133, 328, 25);
		contentPane.add(progressBar);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0)
			{
				cancelled = true;
				if (download != null && download.getStatus() == Download.DOWNLOADING)
				{
					download.cancel();
					return;
				}
				
				if (getDefaultCloseOperation() == DO_NOTHING_ON_CLOSE)
				{
					return;
				}
				
				new Thread(new Runnable()
				{
					public void run() 
					{
						try 
						{
							Thread.sleep(500L);
						} 
						catch (InterruptedException e) {}
						
						if (getDefaultCloseOperation() == DISPOSE_ON_CLOSE)
						{
							dispose();
							return;
						}
						
						System.exit(0);
					}
				}).start();
			}
		});
		btnCancel.setBounds(352, 133, 80, 25);
		contentPane.add(btnCancel);
		
		lblStatus.setText(" Checking for new updates...");
		lblDetails.setText("Trying to retrieve update informations from the repository.");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					Thread.sleep(300L);
				}
				catch (InterruptedException ex) {}
				
				new UpdateTask(UUID.randomUUID(), "https://raw.githubusercontent.com/codebucketdev/Rly.li-URL-Shortener/master/res/version.json", UpdateTask.CURRENT_VERSION) 
				{
					@Override
					public void updateSucess(Update update) 
					{
						if (cancelled == true)
						{
							return;
						}
						
						if (update == null)
						{
							lblStatus.setText(" An error occurred while checking for new updates!");
							lblStatus.setIcon(new ImageIcon(UpdaterWindow.class.getResource("/icon_delete.png")));
							lblDetails.setText("Nothing has been changed. You can close this window.");
							setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							
							progressBar.setIndeterminate(false);
							JOptionPane.showMessageDialog(UpdaterWindow.this, "An error occurred while checking for new updates.", "Rly.li URL Shortener", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						try
						{
							Thread.sleep(500L);
						}
						catch (InterruptedException ex) {}
						
						if (update.getName().equals(getCurrent().getName()))
						{
							if (!Update.compareVersions(getCurrent().getVersion(), update.getVersion()))
							{
								lblStatus.setText(" No update found! You have already the newest version.");
								lblStatus.setIcon(new ImageIcon(UpdaterWindow.class.getResource("/icon_yes.png")));
								lblDetails.setText("Nothing has been changed. You can close this window.");
								setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
								
								progressBar.setIndeterminate(false);
								JOptionPane.showMessageDialog(UpdaterWindow.this, "No update found! You have already the newest version.", "Rly.li URL Shortener", JOptionPane.INFORMATION_MESSAGE);
								return;
							}
							
							progressBar.setIndeterminate(false);
							lblStatus.setText(" Waiting for user response...");
							lblDetails.setText("New update found! Update version: " + update.getVersion());
							int result = JOptionPane.showConfirmDialog(UpdaterWindow.this, "<html><b>New update found!</b></html>\nUpdate version: " + update.getVersion() + "\nYour version: " + getCurrent().getVersion() + "\n\nDo you want to download the new update?", "Rly.li URL Shortener", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
							if (result == JOptionPane.YES_OPTION)
							{
								try 
								{
									File target = File.createTempFile("download_", "");
									if (target.exists())
									{
										target.deleteOnExit();
									}
									
									download = new Download(new URL(update.getUpdate()), target);
									new Thread(new Runnable() 
									{
										long start = System.nanoTime();
										
										public void run() 
										{
											lblStatus.setText(" Downloading update... Please wait.");
											lblStatus.setIcon(new ImageIcon(UpdaterWindow.class.getResource("/icon_download.png")));
											while (download.getStatus() == Download.DOWNLOADING)
											{
												if (download.getDownloaded() <= 0)
												{
													start = System.nanoTime();
												}
												
												progressBar.setValue((int) download.getProgress());
												String downloaded = humanReadableByteCount(download.getDownloaded(), true);
												String size = humanReadableByteCount(download.getSize(), true);
												String speed = humanReadableByteCount(getDownloadSpeed(), false);
												lblDetails.setText("Downloading file: " + downloaded + "/" + size + " (" + speed + "/s)");
											}
											
											switch (download.getStatus())
											{
												case Download.COMPLETE:
													lblStatus.setText(" Finished! Update sucessfully downloaded.");
													lblStatus.setIcon(new ImageIcon(UpdaterWindow.class.getResource("/icon_yes.png")));
													break;
												
												case Download.ERROR:
													lblStatus.setText(" An error occurred while trying to download a new update!");
													lblStatus.setIcon(new ImageIcon(UpdaterWindow.class.getResource("/icon_delete.png")));
													break;
												
												case Download.PAUSED:
													lblStatus.setText(" The update has been paused, no network available.");
													lblStatus.setIcon(new ImageIcon(UpdaterWindow.class.getResource("/icon_warning.png")));
													break;
													
												default:
													lblStatus.setText("Update has been cancelled by user.");
													lblStatus.setIcon(null);
													break;
											}
											
											if (download.getStatus() != Download.COMPLETE)
											{
												lblDetails.setText("Nothing has been changed. You can close this window.");
												setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
												
												File file = new File(download.getFileName());
												if (file.exists())
												{
													file.deleteOnExit();
												}
												
												if (download.getStatus() == Download.ERROR)
												{
													JOptionPane.showMessageDialog(UpdaterWindow.this, "An error occurred while trying to download a new update.", "Rly.li URL Shortener", JOptionPane.ERROR_MESSAGE);
												}
												
												return;
											}
											
											//Release unused files
											System.gc();
											
											File jar = getJarFile();
											if (jar != null && jar.exists())
											{
												synchronized(this) 
												{
													FileManager.writeBytes(jar, FileManager.readBytes(download.getTarget()));
												}
											}
											
											lblDetails.setText("File sucessfully downloaded! (" + humanReadableByteCount(getDownloadSpeed(), true) + "/s)");
											setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
											progressBar.setValue(100);
											
											int result = JOptionPane.showConfirmDialog(UpdaterWindow.this, "<html><b>Update sucessfully downloaded!</b></html>\nTo apply the changes, this program needs to be restarted.\nDo you like to restart this application?", "Rly.li URL Shortener", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
											if (result == JOptionPane.YES_OPTION)
											{
												try
												{
													restartApplication(new File(download.getFileName()));
												}
												catch (Exception ex) 
												{
													System.exit(0);
												}
											}
										}
										
										public long getDownloadSpeed()
										{
											return (long) ((Download.NANOS_PER_SECOND / Download.BYTES_PER_MIB * download.getDownloaded() / (System.nanoTime() - start + 1)) * 1024 * 1024);
										}
										
									}).start();
								}
								catch (Exception ex) {}
							}
							else
							{
								lblStatus.setIcon(null);
								lblStatus.setText("Update has been cancelled by user.");
								lblDetails.setText("Nothing has been changed. You can close this window.");
								setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							}
						}
					}
				}.check();
			}
		}, "update").start();		
		
		Window frames[] = Window.getWindows(); 
		for (int i = 0; i < frames.length; i++)
		{ 
			if (frames[i] != this)
			{
				frames[i].dispose();
			}
		} 
		
		addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e) 
			{
				cancelled = true;
				if (download != null && download.getStatus() == Download.DOWNLOADING)
				{
					download.cancel();
					return;
				}
			}
		});
		
		setVisible(true);
	}
	
	private String humanReadableByteCount(long bytes, boolean si) 
	{
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	public void restartApplication() throws IOException, URISyntaxException 
	{
		final File currentJar = new File(MainWindow.class.getProtectionDomain().getCodeSource().getLocation().toURI());

		/* is it a jar file? */
		if (!currentJar.getName().endsWith(".jar"))
			return;
		
		restartApplication(currentJar);
	}
	
	public void restartApplication(File jar) throws IOException
	{
		final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		
		/* Build command: java -jar application.jar */
		final ArrayList<String> command = new ArrayList<String>();
		command.add(javaBin);
		command.add("-jar");
		command.add(jar.getPath());

		final ProcessBuilder builder = new ProcessBuilder(command);
		builder.start();
		System.exit(0);
	}
	
	public File getJarFile()
	{
		try
		{
			final File currentJar = new File(MainWindow.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (!currentJar.getName().endsWith(".jar"))
				return null;
			
			return currentJar;
		}
		catch (Exception ex) {}
		return null;
	}
	
	public enum UpdateStatus
	{
		CHECK_UPDATE, CONFIRM_UPDATE, DOWNLOAD_UPDATE;
	}
}
