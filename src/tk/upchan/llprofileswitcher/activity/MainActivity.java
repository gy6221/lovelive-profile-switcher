package tk.upchan.llprofileswitcher.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import tk.upchan.llprofileswitcher.R;
import tk.upchan.llprofileswitcher.exception.RootAccessException;
import tk.upchan.llprofileswitcher.logic.SwitchLogic;
import tk.upchan.llprofileswitcher.util.CommandUtil;
import tk.upchan.llprofileswitcher.util.LogToFile;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends FinalActivity implements OnClickListener{

	@ViewInject(id=R.id.btn_backup_profile)
	private Button btnBackupProfile;
	@ViewInject(id=R.id.btn_restore_profile)
	private Button btnRestoreProfile;
	@ViewInject(id=R.id.btn_remove_current_profile)
	private Button btnRemoveCurrentProfile;
	@ViewInject(id=R.id.btn_test)
	private Button btnTest;
	@ViewInject(id=R.id.sp_profiles)
	private Spinner spProfiles;
	
	
	private String lastUsedProfileName = "";
	private Map<String, Integer> profileMap;
	private SwitchLogic switchLogic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		switchLogic = new SwitchLogic(this);
		refreshProfileList();
		if(!switchLogic.checkIfGameDataExists()){
			llNotExistsDialog();
		}
		btnBackupProfile.setOnClickListener(this);
		btnRestoreProfile.setOnClickListener(this);
		btnRemoveCurrentProfile.setOnClickListener(this);
		btnTest.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try{
			refreshProfileList();
			selectLastUsedProfile();
		}catch(Exception e){
			Toast.makeText(this, "如果你看到这个，请截图", Toast.LENGTH_LONG).show();
		}
	}
	
	private void selectLastUsedProfile(){
		Integer index = profileMap.get(lastUsedProfileName);
		if(profileMap.get(lastUsedProfileName)!=null){
			spProfiles.setSelection(index);
		}
	}

	private void refreshProfileList(){
		profileMap = switchLogic.getExistingProfiles();
		List<String> profileName = new ArrayList<String>();
		Iterator<String> iterator = profileMap.keySet().iterator();
		while(iterator.hasNext()){
			profileName.add(iterator.next());
		}
		if(profileName.size()==0){
			spProfiles.setEnabled(false);
			btnRestoreProfile.setEnabled(false);
			profileName.add("No Profiles found!");
		}
		else{
			btnRestoreProfile.setEnabled(true);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, profileName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spProfiles.setAdapter(adapter);
	}
	
	public void onClick(View v){
		if(v == btnBackupProfile){
			backup();
		}
		if(v == btnRestoreProfile){
			restore();
		}
		if(v == btnRemoveCurrentProfile){
			remove();
		}
		if(v == btnTest){
			try {
				LogToFile.log(this, CommandUtil.runAsRoot("ls -l /"));
			} catch (RootAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void remove(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.warning);
		builder.setMessage(R.string.remove_warning);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				try {
					switchLogic.removeCurrentProfile();
				} catch (RootAccessException e) {
					new AlertDialog.Builder(MainActivity.this).setTitle(R.string.error).setMessage(R.string.no_root_access).show();
				}
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				return;
			}
		});
		builder.show();
	}
	
	private void backup(){
		final EditText etProfileName = new EditText(this);
		String str = new SimpleDateFormat("yyMMddHHmm").format(new Date());
		etProfileName.setText(new StringBuffer("GameEngineActivity.xml.").append(str));
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.input_profile_name);
		builder.setView(etProfileName);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String profileName = etProfileName.getText().toString().trim();
				try {
					switchLogic.backupCurrentProfile(profileName);
				} catch (RootAccessException e) {
					new AlertDialog.Builder(MainActivity.this).setTitle(R.string.error).setMessage(R.string.no_root_access).show();
				}
				refreshProfileList();
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				return;
			}
		});
		builder.show();
	}
	
	private void restore(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.warning);
		builder.setMessage(R.string.overwriting_warning);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String toRestore = spProfiles.getSelectedItem().toString().trim();
				lastUsedProfileName = toRestore;
				try {
					switchLogic.restoreProfile(toRestore);
				} catch (RootAccessException e) {
					new AlertDialog.Builder(MainActivity.this).setTitle(R.string.error).setMessage(R.string.no_root_access).show();
				}
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				return;
			}
		});
		builder.show();
	}
	
	private void llNotExistsDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error);
		builder.setMessage(R.string.ll_data_dir_not_exists);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				System.exit(0);
			}
		});
		builder.show();
	}

}
