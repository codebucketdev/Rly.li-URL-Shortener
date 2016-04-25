package de.codebucket.shortener.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResourceLoader
{
    public static String[] readResource(String resource) throws IOException
    {
        List<String> list = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(getResourceAsStream(resource)));

        for (String line; (line = br.readLine()) != null; )
        {
            list.add(line);
        }

        return list.toArray(new String[list.size()]);
    }

    public static List<String[]> readResources(String... resources) throws IOException
    {
        List<String[]> list = new ArrayList<String[]>();
        for (String resource : resources)
        {
            list.add(readResource(resource));
        }

        return list;
    }

    public static void saveResource(File folder, String resource) throws IOException
    {
        File file = new File(folder, resource);
        if (file.exists())
        {
            writeBytes(file, getResource(resource));
            return;
        }

        File dir = file.getParentFile();
        if (!dir.exists())
        {
            dir.mkdirs();
        }

        file.createNewFile();
        writeBytes(file, getResource(resource));
    }

    public static void saveResources(File folder, String... resources) throws IOException
    {
        for (String resource : resources)
        {
            saveResource(folder, resource);
        }
    }

    public static byte[] getResource(String resource) throws IOException
    {
        InputStream input = getResourceAsStream(resource);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int reads = input.read();
        while(reads != -1)
        {
            baos.write(reads);
            reads = input.read();
        }

        return baos.toByteArray();
    }

    public static InputStream getResourceAsStream(String resource)
    {
        return ResourceLoader.class.getResourceAsStream(resource);
    }

    public static void writeBytes(File file, byte[] data) throws IOException
    {
        FileOutputStream output = new FileOutputStream(file);
        output.write(data);
        output.close();
    }
}
