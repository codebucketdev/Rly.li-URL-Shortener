package de.codebucket.shortener;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import de.codebucket.shortener.utils.PasswordUtils;

public class SettingsWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private JCheckBox chckbxStartup, chckbxPlaySound, chckbxCopyLink, chckbxOpenLink, chckbxSaveImage, chckbxShowExplorer, chckbxUnguessable, chckbxDontDraw;
	private JTextField txtSavePath, txtUsername, txtPassword;
	private JRadioButton rdbtnNoCompression, rdbtnSmartQuality, rdbtnCaptureAll, rdbtnCaptureCurrent, rdbtnCapturePrimary;
	private JButton btnOpen;
	
	private Settings settings = Settings.getInstance();
	private MainWindow main = MainWindow.getInstance();
	
	public SettingsWindow()
	{
		// Frame-Initialisierung
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    getContentPane().setBackground(SystemColor.control);
	    
	    int frameWidth = 501;
	    int frameHeight = 388;
	    setSize(frameWidth, frameHeight);
		
	    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (d.width - getSize().width) / 2;
	    int y = (d.height - getSize().height) / 2;
	    
	    setLocation(x, y);
	    setResizable(false);
	    
	    Container contentPane = getContentPane();
	    contentPane.setLayout(null);
	    
	    setTitle("Settings");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/favicon.png")));
		
		JTabbedPane options = new JTabbedPane(JTabbedPane.TOP);
		options.setFont(new Font("Tahoma", Font.PLAIN, 11));
		options.setBounds(10, 11, 475, 337);
		contentPane.add(options);
		
		JPanel generalTab = new JPanel();
		generalTab.setBackground(SystemColor.window);
		options.addTab("General", null, generalTab, null);
		generalTab.setLayout(null);
		
		JPanel general = new JPanel();
		general.setBackground(SystemColor.window);
		general.setBorder(new TitledBorder(null, "General Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		general.setBounds(10, 7, 450, 50);
		generalTab.add(general);
		general.setLayout(null);
		
		this.chckbxStartup = new JCheckBox(" Start Rly.li URL Shortener on startup");
		chckbxStartup.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				settings.setKeepActive(chckbxStartup.isSelected());
			}
		});
		chckbxStartup.setSelected(settings.isKeepActive());
		chckbxStartup.setBackground(SystemColor.window);
		chckbxStartup.setBounds(30, 17, 275, 23);
		general.add(chckbxStartup);
		
		JPanel upload = new JPanel();
		upload.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "On successful upload", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		upload.setBackground(SystemColor.window);
		upload.setBounds(10, 64, 450, 100);
		generalTab.add(upload);
		upload.setLayout(null);
		
		this.chckbxPlaySound = new JCheckBox(" Play a notification sound");
		chckbxPlaySound.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				settings.setPlaySound(chckbxPlaySound.isSelected());
			}
		});
		chckbxPlaySound.setSelected(settings.isPlaySound());
		chckbxPlaySound.setBackground(SystemColor.window);
		chckbxPlaySound.setBounds(30, 17, 177, 23);
		upload.add(chckbxPlaySound);
		
		this.chckbxCopyLink = new JCheckBox(" Copy link to clipboard");
		chckbxCopyLink.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				settings.setCopyLink(chckbxCopyLink.isSelected());
			}
		});
		chckbxCopyLink.setSelected(settings.isCopyLink());
		chckbxCopyLink.setBackground(SystemColor.window);
		chckbxCopyLink.setBounds(30, 40, 177, 23);
		upload.add(chckbxCopyLink);
		
		this.chckbxOpenLink = new JCheckBox(" Open link in browser");
		chckbxOpenLink.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				settings.setOpenBrowser(chckbxOpenLink.isSelected());
			}
		});
		chckbxOpenLink.setSelected(settings.isOpenBrowser());
		chckbxOpenLink.setBackground(SystemColor.window);
		chckbxOpenLink.setBounds(30, 64, 177, 23);
		upload.add(chckbxOpenLink);
		
		this.txtSavePath = new JTextField();
		txtSavePath.setEnabled(settings.isSaveImage());
		txtSavePath.setText(settings.getSavePath());
		txtSavePath.getDocument().addDocumentListener(new DocumentListener()
		{	
			public void removeUpdate(DocumentEvent e)
			{
				callUpdate(e);
			}
			
			public void insertUpdate(DocumentEvent e)
			{
				callUpdate(e);
			}
			
			public void changedUpdate(DocumentEvent e)
			{
				callUpdate(e);
			}
			
			public void callUpdate(DocumentEvent e)
			{
				try
				{
					settings.setSavePath(e.getDocument().getText(0, e.getDocument().getLength()));
				}
				catch (BadLocationException ex)
				{
					ex.printStackTrace();
				}
			}
		});
		txtSavePath.setBounds(246, 42, 160, 23);
		txtSavePath.setColumns(10);
		upload.add(txtSavePath);
		
		this.btnOpen = new JButton("..");
		btnOpen.setEnabled(settings.isSaveImage());
		btnOpen.addActionListener(new ActionListener()
		{	
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Browse for Folder");
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
			    int result = fileChooser.showOpenDialog(SettingsWindow.this);
			    if (result == JFileChooser.APPROVE_OPTION)
			    {
			    	settings.setSavePath(fileChooser.getSelectedFile().getAbsolutePath());
			        txtSavePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
			    }
			}
		});
		btnOpen.setBounds(412, 42, 21, 23);
		upload.add(btnOpen);
		
		this.chckbxSaveImage = new JCheckBox(" Save a local copy of image");
		chckbxSaveImage.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				settings.setSaveImage(chckbxSaveImage.isSelected());
				txtSavePath.setEnabled(settings.isSaveImage());
				btnOpen.setEnabled(settings.isSaveImage());
			}
		});
		chckbxSaveImage.setSelected(settings.isSaveImage());
		chckbxSaveImage.setBackground(SystemColor.window);
		chckbxSaveImage.setBounds(222, 17, 177, 23);
		upload.add(chckbxSaveImage);
		
		JPanel behaviour = new JPanel();
		behaviour.setBorder(new TitledBorder(null, "Tray Icon Behaviour", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		behaviour.setBackground(SystemColor.window);
		behaviour.setBounds(10, 171, 450, 120);
		behaviour.setEnabled(false);
		generalTab.add(behaviour);
		behaviour.setLayout(null);
		
		JLabel lblOnDoubleclick = new JLabel("On double-click...");
		lblOnDoubleclick.setBounds(21, 19, 125, 14);
		lblOnDoubleclick.setEnabled(false);
		behaviour.add(lblOnDoubleclick);
		
		ButtonGroup groupDoubleclick = new ButtonGroup();
		groupDoubleclick.clearSelection();
		
		JRadioButton rdbtnShowSettings = new JRadioButton(" Show settings dialog");
		rdbtnShowSettings.setSelected(true);
		rdbtnShowSettings.setBackground(SystemColor.window);
		rdbtnShowSettings.setBounds(31, 36, 217, 23);
		rdbtnShowSettings.setEnabled(false);
		groupDoubleclick.add(rdbtnShowSettings);
		behaviour.add(rdbtnShowSettings);
		
		JRadioButton rdbtnBeginCapture = new JRadioButton(" Begin screen capture mode");
		rdbtnBeginCapture.setBackground(SystemColor.window);
		rdbtnBeginCapture.setBounds(31, 60, 217, 23);
		rdbtnBeginCapture.setEnabled(false);
		groupDoubleclick.add(rdbtnBeginCapture);
		behaviour.add(rdbtnBeginCapture);
		
		JRadioButton rdbtnOpenFile = new JRadioButton(" Open upload file dialog");
		rdbtnOpenFile.setBackground(SystemColor.window);
		rdbtnOpenFile.setBounds(31, 84, 217, 23);
		rdbtnOpenFile.setEnabled(false);
		groupDoubleclick.add(rdbtnOpenFile);
		behaviour.add(rdbtnOpenFile);
		
		JPanel accountTab = new JPanel();
		accountTab.setBackground(SystemColor.window);
		options.addTab("Account", null, accountTab, null);
		accountTab.setLayout(null);
		
		JPanel setup = new JPanel();
		setup.setBorder(new TitledBorder(null, "Pastebin Setup", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setup.setBackground(SystemColor.window);
		setup.setBounds(10, 7, 450, 180);
		accountTab.add(setup);
		setup.setLayout(null);
		
		JTextPane txtpnInformation = new JTextPane();
		txtpnInformation.setEditable(false);
		txtpnInformation.setText("You need to login before you can make full use of Pastebin. If you don't already have an account, you can register for free via the link below.");
		txtpnInformation.setBounds(10, 17, 405, 36);
		setup.add(txtpnInformation);
		
		this.txtUsername = new JTextField();
		txtUsername.setText(settings.getPastebinUsername());
		txtUsername.getDocument().addDocumentListener(new DocumentListener()
		{	
			public void removeUpdate(DocumentEvent e)
			{
				callUpdate(e);
			}
			
			public void insertUpdate(DocumentEvent e)
			{
				callUpdate(e);
			}
			
			public void changedUpdate(DocumentEvent e)
			{
				callUpdate(e);
			}
			
			public void callUpdate(DocumentEvent e)
			{
				try
				{
					settings.setPastebinUsername(e.getDocument().getText(0, e.getDocument().getLength()));
				}
				catch (BadLocationException ex)
				{
					ex.printStackTrace();
				}
			}
		});
		txtUsername.setBounds(135, 64, 149, 23);
		txtUsername.setColumns(10);
		setup.add(txtUsername);
		
		this.txtPassword = new JPasswordField();
		txtPassword.setText(PasswordUtils.decodeString(settings.getPastebinPassword()));
		txtPassword.getDocument().addDocumentListener(new DocumentListener()
		{	
			public void removeUpdate(DocumentEvent e)
			{
				callUpdate(e);
			}
			
			public void insertUpdate(DocumentEvent e)
			{
				callUpdate(e);
			}
			
			public void changedUpdate(DocumentEvent e)
			{
				callUpdate(e);
			}
			
			public void callUpdate(DocumentEvent e)
			{
				try
				{
					settings.setPastebinPassword(e.getDocument().getText(0, e.getDocument().getLength()));
				}
				catch (BadLocationException ex)
				{
					ex.printStackTrace();
				}
			}
		});
		txtPassword.setBounds(135, 92, 149, 23);
		txtPassword.setColumns(10);
		setup.add(txtPassword);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(305, 64, 125, 51);
		setup.add(btnLogin);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUsername.setBounds(35, 68, 93, 14);
		setup.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPassword.setBounds(35, 96, 93, 14);
		setup.add(lblPassword);
		
		JLabel lblForgotPassword = new JLabel("<html><a href=\"\">Forgotten Password?</a></html>");
		lblForgotPassword.addMouseListener(new MouseAdapter() 
	    {
	    	public void mouseClicked(MouseEvent e)
	    	{
	    		try
	    		{
	    			openWebsite(new URL("http://pastebin.com/passmailer"));
	    		}
	    		catch (Exception ex)
	    		{
	    			ex.printStackTrace();
	    		}
	    	}
	    });
		lblForgotPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblForgotPassword.setBounds(135, 121, 149, 14);
		setup.add(lblForgotPassword);
		
		JLabel lblSignup = new JLabel("<html><a href=\"\">Sign up for free account...</a></html>");
		lblSignup.addMouseListener(new MouseAdapter() 
	    {
	    	public void mouseClicked(MouseEvent e)
	    	{
	    		try
	    		{
	    			openWebsite(new URL("http://pastebin.com/signup"));
	    		}
	    		catch (Exception ex)
	    		{
	    			ex.printStackTrace();
	    		}
	    	}
	    });
		lblSignup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblSignup.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSignup.setBounds(250, 150, 180, 14);
		setup.add(lblSignup);
		
		JPanel updatesTab = new JPanel();
		updatesTab.setBackground(SystemColor.window);
		options.addTab("Updates", null, updatesTab, null);
		updatesTab.setLayout(null);
		
		JPanel management = new JPanel();
		management.setBorder(new TitledBorder(null, "Update Management", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		management.setBackground(SystemColor.window);
		management.setBounds(10, 7, 450, 90);
		updatesTab.add(management);
		management.setLayout(null);
		
		JLabel lblLastCheck = new JLabel("Last Checked:    " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime()));
		lblLastCheck.setBounds(33, 20, 242, 52);
		management.add(lblLastCheck);
		
		JButton btnCheck = new JButton("Check for Updates");
		btnCheck.setBounds(285, 20, 150, 52);
		management.add(btnCheck);
		
		JPanel advancedTab = new JPanel();
		advancedTab.setBackground(SystemColor.window);
		options.addTab("Advanced", null, advancedTab, null);
		advancedTab.setLayout(null);
		
		JPanel quality = new JPanel();
		quality.setBorder(new TitledBorder(null, "Screen Capture Quality", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		quality.setBackground(SystemColor.window);
		quality.setBounds(10, 7, 450, 68);
		advancedTab.add(quality);
		quality.setLayout(null);
		
		ButtonGroup groupCompression = new ButtonGroup();
		groupCompression.clearSelection();
		
		this.rdbtnNoCompression = new JRadioButton(" No Compression (always PNG)");
		rdbtnNoCompression.setSelected(!settings.isSmartQuality());
		rdbtnNoCompression.addChangeListener(new ChangeListener()
		{	
			public void stateChanged(ChangeEvent e)
			{
				settings.setSmartQuality(!rdbtnNoCompression.isSelected());
			}
		});
		rdbtnNoCompression.setBackground(SystemColor.window);
		rdbtnNoCompression.setBounds(12, 16, 325, 23);
		groupCompression.add(rdbtnNoCompression);
		quality.add(rdbtnNoCompression);
		
		this.rdbtnSmartQuality = new JRadioButton(" Smart (use JPG unless PNG is smaller in filesize)");
		rdbtnSmartQuality.setSelected(settings.isSmartQuality());
		rdbtnSmartQuality.addChangeListener(new ChangeListener()
		{	
			public void stateChanged(ChangeEvent e)
			{
				settings.setSmartQuality(rdbtnSmartQuality.isSelected());
			}
		});
		rdbtnSmartQuality.setBackground(SystemColor.window);
		rdbtnSmartQuality.setBounds(12, 38, 325, 23);
		groupCompression.add(rdbtnSmartQuality);
		quality.add(rdbtnSmartQuality);
		
		JPanel context = new JPanel();
		context.setBorder(new TitledBorder(null, "Context Menu", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		context.setBackground(SystemColor.window);
		context.setBounds(10, 81, 450, 46);
		advancedTab.add(context);
		context.setLayout(null);
		
		this.chckbxShowExplorer = new JCheckBox(" Show explorer context menu item");
		chckbxShowExplorer.setSelected(true);
		chckbxShowExplorer.setBackground(SystemColor.window);
		chckbxShowExplorer.setBounds(12, 16, 271, 23);
		context.add(chckbxShowExplorer);
		
		JPanel capture = new JPanel();
		capture.setBorder(new TitledBorder(null, "Fullscreen Capture", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		capture.setBackground(SystemColor.window);
		capture.setBounds(10, 133, 450, 91);
		advancedTab.add(capture);
		capture.setLayout(null);
		
		ButtonGroup groupCapture = new ButtonGroup();
		groupCapture.clearSelection();
		
		this.rdbtnCaptureAll = new JRadioButton(" Capture all screens");
		rdbtnCaptureAll.setSelected(settings.getCaptureMode() == 0);
		rdbtnCaptureAll.addChangeListener(new ChangeListener()
		{	
			public void stateChanged(ChangeEvent e)
			{
				if (rdbtnCaptureAll.isSelected())
				{
					settings.setCaptureMode(0);
				}
			}
		});
		rdbtnCaptureAll.setBackground(SystemColor.window);
		rdbtnCaptureAll.setBounds(12, 16, 300, 23);
		groupCapture.add(rdbtnCaptureAll);
		capture.add(rdbtnCaptureAll);
		
		this.rdbtnCaptureCurrent = new JRadioButton(" Capture screen containing mouse cursor");
		rdbtnCaptureCurrent.setSelected(settings.getCaptureMode() == 1);
		rdbtnCaptureCurrent.addChangeListener(new ChangeListener()
		{	
			public void stateChanged(ChangeEvent e)
			{
				if (rdbtnCaptureCurrent.isSelected())
				{
					settings.setCaptureMode(1);
				}
			}
		});
		rdbtnCaptureCurrent.setBackground(SystemColor.window);
		rdbtnCaptureCurrent.setBounds(12, 38, 300, 23);
		groupCapture.add(rdbtnCaptureCurrent);
		capture.add(rdbtnCaptureCurrent);
		
		this.rdbtnCapturePrimary = new JRadioButton(" Always capture primary screen");
		rdbtnCapturePrimary.setSelected(settings.getCaptureMode() == 2);
		rdbtnCapturePrimary.addChangeListener(new ChangeListener()
		{	
			public void stateChanged(ChangeEvent e)
			{
				if (rdbtnCapturePrimary.isSelected())
				{
					settings.setCaptureMode(2);
				}
			}
		});
		rdbtnCapturePrimary.setBackground(SystemColor.window);
		rdbtnCapturePrimary.setBounds(12, 60, 300, 23);
		groupCapture.add(rdbtnCapturePrimary);
		capture.add(rdbtnCapturePrimary);
		
		JPanel dangerous = new JPanel();
		dangerous.setBorder(new TitledBorder(null, "Dangerous Stuff", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		dangerous.setBackground(SystemColor.window);
		dangerous.setBounds(10, 231, 450, 67);
		advancedTab.add(dangerous);
		dangerous.setLayout(null);
		
		this.chckbxUnguessable = new JCheckBox(" Make links unguessable");
		chckbxUnguessable.setSelected(settings.isPrivateMode());
		chckbxUnguessable.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				settings.setPrivateMode(chckbxUnguessable.isSelected());
			}
		});
		chckbxUnguessable.setBackground(SystemColor.window);
		chckbxUnguessable.setBounds(12, 16, 256, 23);
		dangerous.add(chckbxUnguessable);
		
		this.chckbxDontDraw = new JCheckBox(" Don't draw selection rectangle");
		chckbxDontDraw.setSelected(!settings.isDrawRectangle());
		chckbxDontDraw.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				settings.setDrawRectangle(!chckbxDontDraw.isSelected());
			}
		});
		chckbxDontDraw.setBackground(SystemColor.window);
		chckbxDontDraw.setBounds(12, 38, 256, 23);
		dangerous.add(chckbxDontDraw);
		
		JPanel aboutTab = new JPanel();
		aboutTab.setBackground(SystemColor.window);
		options.addTab("About", null, aboutTab, null);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				settings.saveSettings();
				main.closeWindow(SettingsWindow.this);
			}
		});
		
		setVisible(true);
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
}
