package com.ihsan.notes;

import android.content.Context;
import android.graphics.*;
import java.io.*;
import android.os.*;
import android.util.Log;

public class ImageSaver {
	private String directoryName =
	"images";
	private String fileName = "image.png" ;
	private Context context;
	private boolean external;
	public ImageSaver (Context context) {
		this.context = context;
	}
	public ImageSaver setFileName( String fileName) {
		this.fileName = fileName;
		return this;
	}
	public ImageSaver setExternal
	(boolean external) {
		this.external = external;
		return this;
	}
	public ImageSaver setDirectoryName
	(String directoryName) {
		this.directoryName = directoryName;
		return this;
	}
	public void save(Bitmap bitmapImage)
	{
		FileOutputStream fileOutputStream
			= null;
		try {
			fileOutputStream = new
				FileOutputStream(createFile());
			bitmapImage.compress
			(Bitmap .CompressFormat .PNG, 100 , fileOutputStream);
        } catch ( Exception e) {
			e.printStackTrace();Log.e( "ImageSaver" , "Failed"+e.toString());
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch ( IOException e) {
				e.printStackTrace();
				Log.e( "ImageSaver" , "Failed"+e.toString());
			}
		}
	}
	private File createFile() {
		File directory;
		if(external){
			directory = getAlbumStorageDir
			(directoryName);
        }
		else {
			directory = context.getDir
			(directoryName, Context .MODE_PRIVATE);
        }
		return new File (directory, fileName);
	}
	private File getAlbumStorageDir( String albumName) {
		File file = new File
		(Environment .getExternalStoragePublicDirectory(
			 Environment .DIRECTORY_PICTURES), albumName);
		if (!file.mkdirs()) {
			Log.e( "ImageSaver" , "Directory not created" );
		}
		return file;
	}
	public static boolean isExternalStorageWritable() {
		String state =
			Environment .getExternalStorageState();
		return Environment .MEDIA_MOUNTED.equals(state);
    }
	public static boolean isExternalStorageReadable() {
		String state =
			Environment .getExternalStorageState();
		return Environment .MEDIA_MOUNTED.equals(state) ||
			Environment .MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
	public Bitmap load() {
		FileInputStream inputStream = null;
		try {
			inputStream = new
				FileInputStream(createFile());
			return
				BitmapFactory .decodeStream
			(inputStream);
        } catch ( Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch ( IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}