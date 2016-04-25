package de.codebucket.shortener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Properties;

import de.codebucket.shortener.utils.PasswordUtils;

public class Settings
{
	private File file;
	private String clientId = "";
	private String secretKey = new BigInteger(130, new SecureRandom()).toString(32);
	private String userAgent = "de.codebucket.shortener/2.4-release HttpClient/4.5.1";
	private int configVersion = 72;
	private boolean privateMode = false;
	private boolean saveImage = false;
	private String savePath = System.getProperty("user.home") + File.separator + "Desktop";
	private boolean keepActive = true;
	private boolean enablePrtsc = true;
	private boolean autoUpload = false;
	private boolean playSound = true;
	private boolean copyLink = true;
	private boolean openBrowser = false;
	private String pastebinKey = "";
	private String pastebinUsername = "";
	private String pastebinPassword = "";
	private boolean drawRectangle = true;
	private boolean smartQuality = true;
	private int captureMode = 0;
	
	private static Settings settings;
	
	public Settings(File file)
	{
		settings = this;
		this.file = file;
		loadSettings();
	}
	
	public File getFile()
	{
		return file;
	}
	
	public void loadSettings()
	{
		try
		{
			Properties p = new Properties();
			if (!file.exists()) 
			{
				File dir = file.getParentFile(); 
				if (!dir.exists())
				{
					dir.mkdirs();
				}
				
				file.createNewFile();
				
				p.setProperty("client-id", clientId);
				p.setProperty("secret-key", secretKey);
				p.setProperty("user-agent", userAgent);
				p.setProperty("config-version", String.valueOf(configVersion));
				p.setProperty("private-mode", String.valueOf(privateMode));
				p.setProperty("save-image", String.valueOf(saveImage));
				p.setProperty("save-path", savePath);
				p.setProperty("keep-active", String.valueOf(keepActive));
				p.setProperty("enable-prtsc", String.valueOf(enablePrtsc));
				p.setProperty("auto-upload", String.valueOf(autoUpload));
				p.setProperty("play-sound", String.valueOf(playSound));
				p.setProperty("copy-link", String.valueOf(copyLink));
				p.setProperty("open-browser", String.valueOf(openBrowser));
				p.setProperty("pastebin-key", pastebinKey);
				p.setProperty("pastebin-username", pastebinUsername);
				p.setProperty("pastebin-password", PasswordUtils.encodeString(pastebinPassword));
				p.setProperty("draw-rectangle", String.valueOf(drawRectangle));
				p.setProperty("smart-quality", String.valueOf(smartQuality));
				p.setProperty("capture-mode", String.valueOf(captureMode));
				
				FileOutputStream out = new FileOutputStream(file);
				p.store(out, "Rly.li URL Shortener client properties");
				out.close();
			} 
			else 
			{
				FileInputStream in = new FileInputStream(file);
				p.load(in);
				in.close();
			}
			
			this.clientId = p.getProperty("client-id");
			this.secretKey = p.getProperty("secret-key");
			this.userAgent = p.getProperty("user-agent");
			this.configVersion = Integer.parseInt(p.getProperty("config-version"));
			this.privateMode = Boolean.parseBoolean(p.getProperty("private-mode"));
			this.saveImage = Boolean.parseBoolean(p.getProperty("save-image"));
			this.savePath = p.getProperty("save-path");
			this.keepActive = Boolean.parseBoolean(p.getProperty("keep-active"));
			this.enablePrtsc = Boolean.parseBoolean(p.getProperty("enable-prtsc"));
			this.autoUpload = Boolean.parseBoolean(p.getProperty("auto-upload"));
			this.playSound = Boolean.parseBoolean(p.getProperty("play-sound"));
			this.copyLink = Boolean.parseBoolean(p.getProperty("copy-link"));
			this.openBrowser = Boolean.parseBoolean(p.getProperty("open-browser"));
			this.pastebinKey = p.getProperty("pastebin-key");
			this.pastebinUsername = p.getProperty("pastebin-username");
			this.pastebinPassword = PasswordUtils.decodeString(p.getProperty("pastebin-password"));
			this.drawRectangle = Boolean.parseBoolean(p.getProperty("draw-rectangle"));
			this.smartQuality = Boolean.parseBoolean(p.getProperty("smart-quality"));
			this.captureMode = Integer.parseInt(p.getProperty("capture-mode"));
		}
		catch(Exception ex) 
		{
			MainWindow.showExceptionInfo(ex);
		}
	}
	
	public boolean saveSettings()
	{
		try
		{
			Properties p = new Properties();	
			
			p.setProperty("client-id", clientId);
			p.setProperty("secret-key", secretKey);
			p.setProperty("user-agent", userAgent);
			p.setProperty("config-version", String.valueOf(configVersion));
			p.setProperty("private-mode", String.valueOf(privateMode));
			p.setProperty("save-image", String.valueOf(saveImage));
			p.setProperty("save-path", savePath);
			p.setProperty("keep-active", String.valueOf(keepActive));
			p.setProperty("enable-prtsc", String.valueOf(enablePrtsc));
			p.setProperty("auto-upload", String.valueOf(autoUpload));
			p.setProperty("play-sound", String.valueOf(playSound));
			p.setProperty("copy-link", String.valueOf(copyLink));
			p.setProperty("open-browser", String.valueOf(openBrowser));
			p.setProperty("pastebin-key", pastebinKey);
			p.setProperty("pastebin-username", pastebinUsername);
			p.setProperty("pastebin-password", PasswordUtils.encodeString(pastebinPassword));
			p.setProperty("draw-rectangle", String.valueOf(drawRectangle));
			p.setProperty("smart-quality", String.valueOf(smartQuality));
			p.setProperty("capture-mode", String.valueOf(captureMode));
			
			FileOutputStream out = new FileOutputStream(file);
			p.store(out, "Rly.li URL Shortener client properties");
			out.close();
			
			return true;
		}
		catch(Exception ex) 
		{
			MainWindow.showExceptionInfo(ex);
			return false;
		}
	}
	
	/*
	public boolean updateSettings()
	{
		try
		{
			Properties p = new Properties();	
			
			p.setProperty("client-id", clientId);
			p.setProperty("user-agent", userAgent);
			p.setProperty("config-version", String.valueOf(configVersion));
			p.setProperty("private-mode", String.valueOf(privateMode));
			p.setProperty("save-image", String.valueOf(saveImage));
			p.setProperty("save-path", savePath);
			p.setProperty("keep-active", String.valueOf(keepActive));
			p.setProperty("enable-prtsc", String.valueOf(enablePrtsc));
			p.setProperty("auto-upload", String.valueOf(autoUpload));
			p.setProperty("play-sound", String.valueOf(playSound));
			p.setProperty("copy-link", String.valueOf(copyLink));
			p.setProperty("open-browser", String.valueOf(openBrowser));
			p.setProperty("pastebin-username", pastebinUsername);
			p.setProperty("pastebin-password", PasswordUtils.encodeString(pastebinPassword));
			p.setProperty("draw-rectangle", String.valueOf(drawRectangle));
			p.setProperty("smart-quality", String.valueOf(smartQuality));
			p.setProperty("capture-mode", String.valueOf(captureMode));
			p.setProperty("secret-key", secretKey);
			
			FileOutputStream out = new FileOutputStream(file);
			p.store(out, "Rly.li URL Shortener client properties");
			out.close();
			
			return true;
		}
		catch(Exception ex) 
		{
			MainWindow.showExceptionInfo(ex);
			return false;
		}
	}
	*/

	public String getClientId()
	{
		return clientId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}
	
	public String getSecretKey()
	{
		return secretKey;
	}

	public void setSecretKey(String secretKey)
	{
		this.secretKey = secretKey;
	}

	public String getUserAgent()
	{
		return userAgent;
	}

	public void setUserAgent(String userAgent)
	{
		this.userAgent = userAgent;
	}

	public int getConfigVersion()
	{
		return configVersion;
	}

	public void setConfigVersion(int configVersion)
	{
		this.configVersion = configVersion;
	}

	public boolean isPrivateMode()
	{
		return privateMode;
	}

	public void setPrivateMode(boolean privateMode)
	{
		this.privateMode = privateMode;
	}

	public boolean isSaveImage()
	{
		return saveImage;
	}

	public void setSaveImage(boolean saveImage)
	{
		this.saveImage = saveImage;
	}

	public String getSavePath()
	{
		return savePath;
	}

	public void setSavePath(String savePath)
	{
		this.savePath = savePath;
	}
	
	public boolean isKeepActive()
	{
		return keepActive;
	}

	public void setKeepActive(boolean keepActive)
	{
		this.keepActive = keepActive;
	}

	public boolean isEnablePrtsc()
	{
		return enablePrtsc;
	}

	public void setEnablePrtsc(boolean enablePrtsc)
	{
		this.enablePrtsc = enablePrtsc;
	}

	public boolean isAutoUpload()
	{
		return autoUpload;
	}

	public void setAutoUpload(boolean autoUpload)
	{
		this.autoUpload = autoUpload;
	}

	public boolean isPlaySound()
	{
		return playSound;
	}

	public void setPlaySound(boolean playSound)
	{
		this.playSound = playSound;
	}

	public boolean isCopyLink()
	{
		return copyLink;
	}

	public void setCopyLink(boolean copyLink)
	{
		this.copyLink = copyLink;
	}

	public boolean isOpenBrowser()
	{
		return openBrowser;
	}

	public void setOpenBrowser(boolean openBrowser)
	{
		this.openBrowser = openBrowser;
	}
	
	public String getPastebinKey()
	{
		return pastebinKey;
	}

	public void setPastebinKey(String pastebinKey)
	{
		this.pastebinKey = pastebinKey;
	}
	
	public String getPastebinUsername()
	{
		return pastebinUsername;
	}

	public void setPastebinUsername(String pastebinUsername)
	{
		this.pastebinUsername = pastebinUsername;
	}

	public String getPastebinPassword()
	{
		return pastebinPassword;
	}

	public void setPastebinPassword(String pastebinPassword)
	{
		this.pastebinPassword = pastebinPassword;
	}
	
	public boolean isDrawRectangle()
	{
		return drawRectangle;
	}
	
	public void setDrawRectangle(boolean drawRectangle)
	{
		this.drawRectangle = drawRectangle;
	}

	public boolean isSmartQuality()
	{
		return smartQuality;
	}

	public void setSmartQuality(boolean smartQuality)
	{
		this.smartQuality = smartQuality;
	}

	public int getCaptureMode()
	{
		return captureMode;
	}

	public void setCaptureMode(int captureMode)
	{
		this.captureMode = captureMode;
	}
	
	public static Settings getInstance()
	{
		return settings;
	}
}
