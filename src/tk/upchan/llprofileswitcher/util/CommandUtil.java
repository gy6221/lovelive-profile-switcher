package tk.upchan.llprofileswitcher.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tk.upchan.llprofileswitcher.exception.RootAccessException;
import android.util.Log;

public class CommandUtil {
	
	private static final String TAG = "CommandUtil";
	
	public static String runAsRoot(String command) throws RootAccessException{
		List<String> commands = new ArrayList<String>();
		commands.add(command);
		return runAsRoot(commands);
	}
	
	public static String runAsRoot(List<String> commands) throws RootAccessException{
		String result = "";
		String line = null;
		Process shell = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		try {
			shell = Runtime.getRuntime().exec("su");
			dis = new DataInputStream(shell.getInputStream());
			dos = new DataOutputStream(shell.getOutputStream());
			commands.add("exit");
			for(String command : commands){
				dos.write((command+"\n").getBytes());
				dos.flush();
			}
			dos.close();
			while((line=dis.readLine())!=null){
				result += (line+"\n");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			Log.e(TAG, e.getMessage(), e);
		}
		finally{
			try {
				if(dis!=null){
					dis.close();
				}
				if(dos!=null){
					dos.close();
				}
				if(shell!=null){
					shell.waitFor();
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return result;
	}
}
