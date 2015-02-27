package tk.upchan.llprofileswitcher.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tk.upchan.llprofileswitcher.exception.RootAccessException;
import tk.upchan.llprofileswitcher.util.FileUtil;
import tk.upchan.llprofileswitcher.util.LogToFile;
import android.content.Context;

public class SwitchLogic {
	
	private Context context;
	private static final String LOVELIVE_PROFILE_PATH = "/data/data/klb.android.lovelive/shared_prefs/GameEngineActivity.xml";
	
	public SwitchLogic(Context context){
		this.context = context;
	}
	
	private File getProfileDir(){
		File filesDir = context.getFilesDir();
		File profileDir = new File(filesDir, "profiles");
		return profileDir;
	}
	
	public Map<String, Integer> getExistingProfiles(){
		//List<String> result = new ArrayList<String>();
		Map<String, Integer> filePosition = new LinkedHashMap<String, Integer>();
		File filesDir = context.getFilesDir();
		File profileDir = new File(filesDir, "profiles");
		if(!profileDir.exists()){
			profileDir.mkdirs();
		}
		File[] profiles = profileDir.listFiles();
		int i = 0;
		for(File f : profiles){
			filePosition.put(f.getName(), i++);
			//result.add(f.getName());
		}
		return filePosition;
	}
	
	public boolean checkIfGameDataExists(){
		File llData = new File("/data/data/klb.android.lovelive/shared_prefs");
		return llData.exists();
	}
	
	public void backupCurrentProfile(String profileName) throws RootAccessException{
		File profileDir = getProfileDir();
		File target = new File(profileDir, profileName);
		String result = FileUtil.cp(LOVELIVE_PROFILE_PATH, target.getAbsolutePath()); 
		LogToFile.log(context, result);
	}
	
	public void restoreProfile(String profileName) throws RootAccessException{
		File toRestore = new File(getProfileDir(), profileName);
		String result = FileUtil.cp(toRestore.getAbsolutePath(), LOVELIVE_PROFILE_PATH);
		LogToFile.log(context, result);
	}
	
	public void removeCurrentProfile() throws RootAccessException{
		String result = FileUtil.rm(LOVELIVE_PROFILE_PATH);
		LogToFile.log(context, result);
	}
}
