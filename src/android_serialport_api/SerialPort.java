package de.appplant.cordova.plugin.c_printer.android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort
{

	private static final String TAG = "SerialPort";

	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate, int flags, boolean flowCon) throws SecurityException, IOException
	{

		/* Check access permission */
		if (!device.canRead() || !device.canWrite())
		{
			try
			{
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite())
				{
					throw new SecurityException();
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				throw new SecurityException();
			}
		}

		mFd = open(device.getAbsolutePath(), baudrate, flags, flowCon);
		if (mFd == null)
		{
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	// Getters and setters
	public InputStream getInputStream()
	{
		return mFileInputStream;
	}

	public OutputStream getOutputStream()
	{
		return mFileOutputStream;
	}

	// JNI
	private native static FileDescriptor open(String path, int baudrate, int flags, boolean flowCon);

	public native void close();

	static
	{
		System.loadLibrary("serial_port");
	}
}
