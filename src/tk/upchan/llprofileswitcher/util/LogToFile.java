package tk.upchan.llprofileswitcher.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

public class LogToFile {
	public static void log(Context context, String content){
		try {
			File fileDir = context.getExternalFilesDir("log");
			if(fileDir.exists()==false){
				fileDir.mkdirs();
			}
			File logFile = new File(fileDir, new SimpleDateFormat("yyyyMMdd").format(new Date()).toString()+".log");
			FileWriter writer = new FileWriter(logFile, true);
			writer.append(content);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
	}
}
