package com.iago.undiaunapalabra.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

import android.content.Context;
import android.util.Log;

public class FtpDownloader {
	
	public static void getHistorico(Context ctx) {
		FTPClient client = new FTPClient();
		BufferedOutputStream fos = null;
		try {
			client.connect(Data.ftpIp);
			client.login(Data.ftpUser, Data.ftpPassword);

			client.enterLocalPassiveMode(); // important!
			client.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
			fos = new BufferedOutputStream(
					new FileOutputStream(ctx.getFilesDir().toString()+"/Historico.db"));
			Log.i(Utils.tag, ctx.getFilesDir().toString());
			client.retrieveFile("tests/Historico.db", fos);
		} catch (IOException e) {
			Log.e(Utils.tag, "Error Getting File");
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) fos.close();
				client.disconnect();
			} catch (IOException e) {
				Log.e(Utils.tag, "Disconnect Error");
				e.printStackTrace();
			}
		}
		Log.v(Utils.tag, "Ftp done");    
	}

	public static Boolean deleteFile(Context ctx) {
		File file = new File(ctx.getFilesDir().toString()+"/Historico.db");
        return file.delete();
	}

}
