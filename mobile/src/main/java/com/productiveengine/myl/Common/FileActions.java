package com.productiveengine.myl.Common;

import android.util.Log;

import com.productiveengine.myl.UIL.Services.MediaPlayerService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileActions {

    private static final String TAG = MediaPlayerService.class.getName();

    public void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;

        String fullInputPath = inputPath + "/" + inputFile;
        String fullOutputPath = outputPath + "/" + inputFile;

        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);

            if (!dir.exists())
            {
                dir.mkdirs();
            }
            else{
                fullOutputPath = fixSameNameFiles(fullOutputPath);
            }

            in = new FileInputStream(fullInputPath);
            out = new FileOutputStream(fullOutputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            File checkNewFile = new File(fullOutputPath);

            if(checkNewFile.exists()){
                // delete the original file
                new File(fullInputPath).delete();
            }
            else{
                throw new FileNotFoundException();
            }
        }

        catch (FileNotFoundException fnfe1) {
            Log.e(TAG, fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    public void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + "/" + inputFile).delete();
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e(TAG, fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
    private String fixSameNameFiles(String path){
        String outputPath = "";

        File file = new File(path);

        if(file.exists()){
            //outputPath = fixSameNameFiles(path)
        }
        else{
            outputPath = path;
        }

        return outputPath;
    }
}
