package de.codebucket.shortener.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import de.codebucket.shortener.Settings;

public class PasswordUtils 
{
	private static SecretKey key = generateKey();
    
    public static SecretKey generateKey() 
    {
        try 
        {
            DESKeySpec keySpec = new DESKeySpec(Settings.getInstance().getSecretKey().getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            return keyFactory.generateSecret(keySpec);    
        }
        catch (Exception ex) 
        {
            return null;
        }               
    }

    public static String encodeString(String plainTextPassword) 
    {
        BASE64Encoder encoder = new BASE64Encoder();
        try 
        {
            byte[] cleartext = plainTextPassword.getBytes("UTF8");      
            Cipher cipher = Cipher.getInstance("DES"); 
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return encoder.encode(cipher.doFinal(cleartext));
        }
        catch (Exception ex) 
        {
            return plainTextPassword;
        }        
    }

    public static String decodeString(String encryptedPassword) 
    {
        BASE64Decoder decoder = new BASE64Decoder();
        try 
        {        
            byte[] passwordBytes = decoder.decodeBuffer(encryptedPassword);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            passwordBytes = cipher.doFinal(passwordBytes);
            return new String (passwordBytes, "UTF8");
        }
        catch (Exception ex)
        {
            return encryptedPassword;
        }               
    }
}
