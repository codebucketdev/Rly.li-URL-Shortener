package de.codebucket.shortener.utils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioPlayer
{
	public static synchronized void play(final String resourcePath)
    {        
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(AudioPlayer.class.getResource(resourcePath));
                    clip.open(inputStream);
                    clip.start();
                }
                catch (Exception ex)
                {
                	ex.printStackTrace();
                }
            }
        }).start();
    }
}
