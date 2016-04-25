package de.codebucket.shortener.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

public class ImageUtils
{
	public static BufferedImage decodeToImage(String imageString)
	{
        BufferedImage image = null;
        byte[] imageByte;
        
        try
        {
            imageByte = DatatypeConverter.parseBase64Binary(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        return image;
    }
	
    public static String encodeToString(BufferedImage image, String type)
    {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try
        {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
            imageString = DatatypeConverter.printBase64Binary(imageBytes);
            bos.close();
        } 
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        return imageString;
    }
}
