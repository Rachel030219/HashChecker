package net.rachel030219.hashchecker;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.CardView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
	private static final int FILE_SELECT_CODE = 1;
	private static final int REQUEST_FILE_PERMISSION_CODE_INAPP = 2;
	private static final int REQUEST_FILE_PERMISSION_CODE_SHARE = 3;
	
	Toolbar mToolbar;
	ActionBar mActionBar;
	DrawerLayout mDrawerLayout;
	ActionBarDrawerToggle mDrawerToggle;
	FloatingActionButton mFab;
	CoordinatorLayout mRoot;
	
	CardView mResult;
	TextView mFile;
	TextView mMD5;
	TextView mSHA1;
	TextView mSHA256;
	TextView mSHA384;
	TextView mSHA512;
	
	String md5 = null;
	String sha1 = null;
	String sha256 = null;
	String sha384 = null;
	String sha512 = null;

    boolean eSHA256 = true;
    boolean eSHA384 = true;
    boolean eSHA512 = true;

	EditText mCheckInput;

	ClipboardManager manager;
	
	SharedPreferences defaultPreferences;
	boolean uppercase;
	
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
		
        setContentView(R.layout.main);
		
		manager = new ClipboardManager(this);

        getPreferences();
		bindView();
		checkUpdated();
		
		// handle share
		Intent open = getIntent();
		if(open != null){
			String action = open.getAction();
			String type = open.getType();
			if(action.equals(Intent.ACTION_SEND) && type != null){
                getPreferences();
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
					if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
						requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_FILE_PERMISSION_CODE_SHARE);
					} else {
						Uri data = open.getParcelableExtra(Intent.EXTRA_STREAM);
						showResult(data);
					}
				} else {
					Uri data = open.getParcelableExtra(Intent.EXTRA_STREAM);
					showResult(data);
				}
			}
		}
    }

    private void getPreferences(){
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        eSHA256 = defaultPreferences.getBoolean("output_sha256",true);
        eSHA384 = defaultPreferences.getBoolean("output_sha384",true);
        eSHA512 = defaultPreferences.getBoolean("output_sha512",true);
        uppercase = defaultPreferences.getBoolean("output_case",true);
    }

	private void bindView(){
		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		mToolbar.setTitle(getTitle());
		setSupportActionBar(mToolbar);
		
		mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View mDrawerView) {
                super.onDrawerOpened(mDrawerView);
            }
            @Override
            public void onDrawerClosed(View mDrawerView) {
                super.onDrawerClosed(mDrawerView);
            }
        };
        mDrawerToggle.syncState();

		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();    
			localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
			mDrawerLayout.setFitsSystemWindows(true);
			mDrawerLayout.setClipToPadding(false);
		}
		
		NavigationView mNavigation = (NavigationView) findViewById(R.id.navigation);
		mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem menuItem) {
				switch(menuItem.getItemId()){
					case R.id.setting:
						Intent settingIntent = new Intent(MainActivity.this,SettingsActivity.class);
						startActivity(settingIntent);
						break;
					case R.id.about:
						Intent aboutIntent = new Intent(MainActivity.this,AboutActivity.class);
						startActivity(aboutIntent);
						break;
				}
				mDrawerLayout.closeDrawers();
				return true;
			}
		});
		
		mFab = (FloatingActionButton)findViewById(R.id.fab);
		mFab.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
					if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
						requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_FILE_PERMISSION_CODE_INAPP);
					} else {
						showFileChooser();
					}
				} else {
					showFileChooser();
				}
			}
		});
		
		mRoot = (CoordinatorLayout)findViewById(R.id.rootLayout);

        getPreferences();
		mResult = (CardView)findViewById(R.id.result);
		mFile = (TextView)findViewById(R.id.file);
		mMD5 = (TextView)findViewById(R.id.md5);
		mSHA1 = (TextView)findViewById(R.id.sha1);
		mSHA256 = (TextView)findViewById(R.id.sha256);
        if (eSHA256)
            mSHA256.setVisibility(View.VISIBLE);
        else
            mSHA256.setVisibility(View.GONE);
		mSHA384 = (TextView)findViewById(R.id.sha384);
        if (eSHA384)
            mSHA384.setVisibility(View.VISIBLE);
        else
            mSHA384.setVisibility(View.GONE);
        mSHA512 = (TextView)findViewById(R.id.sha512);
        if (eSHA512)
            mSHA512.setVisibility(View.VISIBLE);
        else
            mSHA512.setVisibility(View.GONE);
		mCheckInput = (EditText)findViewById(R.id.checkInput);
		mCheckInput.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence text, int start, int count, int after){

			}

			@Override
			public void onTextChanged(CharSequence text, int start, int before, int count){

			}
				
			@Override
			public void afterTextChanged(Editable text){
				String check = null;
				Pattern lowerPattern = Pattern.compile("[a-z]");
				Pattern upperPattern = Pattern.compile("[A-Z]");
				if(!text.toString().equals(""))
					check = text.toString().substring(0,text.toString().length());
				if(uppercase){
					if(check != null){
						Matcher matcher = lowerPattern.matcher(check);
						if(matcher.find()){
							int selection = mCheckInput.getSelectionStart();
							mCheckInput.setText(text.toString().toUpperCase());
							mCheckInput.setSelection(selection);
						}
					}
				} else {
					if(check != null){
						Matcher matcher = upperPattern.matcher(check);
						if(matcher.find()){
							int selection = mCheckInput.getSelectionStart();
							mCheckInput.setText(text.toString().toLowerCase());
							mCheckInput.setSelection(selection);
						}
					}
				}
				
				if(!text.toString().equals(md5) && !text.toString().equals(sha1) && !text.toString().equals(sha256) && !text.toString().equals(sha384) && !text.toString().equals(sha512)) {
					mCheckInput.setTextColor(Color.parseColor("#FF0000"));
					mMD5.setTextColor(Color.parseColor("#797979"));
					mSHA1.setTextColor(Color.parseColor("#797979"));
                    if (eSHA256) {
                        mSHA256.setTextColor(Color.parseColor("#797979"));
                    }
                    if (eSHA384) {
                        mSHA384.setTextColor(Color.parseColor("#797979"));
                    }
                    if (eSHA512) {
                        mSHA512.setTextColor(Color.parseColor("#797979"));
                    }
				} else {
					if(text.toString().equals(md5)) {
						mMD5.setTextColor(Color.parseColor("#00CD00"));
						mCheckInput.setTextColor(Color.parseColor("#00CD00"));
					} else if(text.toString().equals(sha1)) {
						mSHA1.setTextColor(Color.parseColor("#00CD00"));
						mCheckInput.setTextColor(Color.parseColor("#00CD00"));
					} else if(eSHA256 && text.toString().equals(sha256)) {
						mSHA256.setTextColor(Color.parseColor("#00CD00"));
						mCheckInput.setTextColor(Color.parseColor("#00CD00"));
					} else if(eSHA384 && text.toString().equals(sha384)) {
						mSHA384.setTextColor(Color.parseColor("#00CD00"));
						mCheckInput.setTextColor(Color.parseColor("#00CD00"));
					} else if(eSHA512 && text.toString().equals(sha512)) {
						mSHA512.setTextColor(Color.parseColor("#00CD00"));
						mCheckInput.setTextColor(Color.parseColor("#00CD00"));
					}
				}
			}
		});
	}
	
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
		intent.setType("*/*"); 
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		getPreferences();
		
		try {
			startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
			mCheckInput.setText("");
		} catch (android.content.ActivityNotFoundException ex) {
			android.widget.Toast.makeText(this, "Please install a File Manager.", android.widget.Toast.LENGTH_SHORT).show();
		}
	}
	
	public void checkUpdated(){
		final SharedPreferences preferences = getSharedPreferences("updated",MODE_PRIVATE);
		if(!preferences.getBoolean("updated12",false)){
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.updated_title);
			dialog.setMessage(R.string.updated_changelog);
			dialog.setPositiveButton("GOT IT",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog,int count){
					preferences.edit().putBoolean("updated12",true).apply();
				}
			});
			dialog.show();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode){
			case FILE_SELECT_CODE:
				if(resultCode == RESULT_OK){
					showResult(data.getData());
				}
				break;
		}
		super.onActivityResult(requestCode,resultCode,data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode == REQUEST_FILE_PERMISSION_CODE_INAPP){
			int grantResult = grantResults[0];
			if (grantResult == PackageManager.PERMISSION_GRANTED){
				showFileChooser();
			}
		} else if(requestCode == REQUEST_FILE_PERMISSION_CODE_SHARE) {
			int grantResult = grantResults[0];
			if (grantResult == PackageManager.PERMISSION_GRANTED){
				showResult((Uri)getIntent().getParcelableExtra(Intent.EXTRA_STREAM));
			}
		}
		
		int grantResult = grantResults[0];
		if (grantResult == PackageManager.PERMISSION_GRANTED){
			switch(requestCode){
				case REQUEST_FILE_PERMISSION_CODE_INAPP:
					showFileChooser();
					break;
				case REQUEST_FILE_PERMISSION_CODE_SHARE:
					showResult((Uri)getIntent().getParcelableExtra(Intent.EXTRA_STREAM));
					break;
			}
		} else if(grantResult == PackageManager.PERMISSION_DENIED) {
			Snackbar.make(mRoot,"Pl",Snackbar.LENGTH_LONG).show();
		}
	}
	
	public void showResult(Uri uri){
		try{
			final File file = new File(FileUtils.getPath(this,uri));
			final Resources res = getResources();

            getPreferences();
            if (eSHA256)
                mSHA256.setVisibility(View.VISIBLE);
            else
                mSHA256.setVisibility(View.GONE);
            if (eSHA384)
                mSHA384.setVisibility(View.VISIBLE);
            else
                mSHA384.setVisibility(View.GONE);
            if (eSHA512)
                mSHA512.setVisibility(View.VISIBLE);
            else
                mSHA512.setVisibility(View.GONE);

			final ProgressDialog dialog = ProgressDialog.show(MainActivity.this,null,"Calculating...",true);
			new Thread(){
				@Override
				public void run(){
					md5 = HashTool.getFileHash("MD5",file);
					sha1 = HashTool.getFileHash("SHA1",file);
                    if (eSHA256)
					    sha256 = HashTool.getFileHash("SHA256",file);
                    if (eSHA384)
					    sha384 = HashTool.getFileHash("SHA384",file);
                    if (eSHA512)
                        sha512 = HashTool.getFileHash("SHA512",file);
					
					if(uppercase){
						md5 = md5.toUpperCase();
						sha1 = sha1.toUpperCase();
                        if (eSHA256)
						    sha256 = sha256.toUpperCase();
                        if (eSHA384)
						    sha384 = sha384.toUpperCase();
                        if (eSHA512)
						    sha512 = sha512.toUpperCase();
					} else {
						md5 = md5.toLowerCase();
						sha1 = sha1.toLowerCase();
                        if (eSHA256)
						    sha256 = sha256.toLowerCase();
                        if (eSHA384)
						    sha384 = sha384.toLowerCase();
                        if (eSHA512)
						    sha512 = sha512.toLowerCase();
					}
					
					runOnUiThread(new Runnable(){
							@Override
							public void run(){
								changeResults();

								mFile.setText(String.format(res.getString(R.string.file),file.getAbsolutePath()));

								mResult.setVisibility(View.VISIBLE);
								mCheckInput.requestFocus();
								mCheckInput.setFocusableInTouchMode(true);

								dialog.dismiss();
							}
						});
				}
			}.start();
		} catch (Exception e) {

		}
	}
	
	public void changeResults(){
		Resources res = getResources();
		mMD5.setText(String.format(res.getString(R.string.md5),md5));
		mSHA1.setText(String.format(res.getString(R.string.sha1),sha1));
		mSHA256.setText(String.format(res.getString(R.string.sha256),sha256));
		mSHA384.setText(String.format(res.getString(R.string.sha384),sha384));
		mSHA512.setText(String.format(res.getString(R.string.sha512),sha512));

		mMD5.setOnLongClickListener(new OnHashLongClick(md5,"MD5"));
		mSHA1.setOnLongClickListener(new OnHashLongClick(sha1,"SHA1"));
		mSHA256.setOnLongClickListener(new OnHashLongClick(sha256,"SHA256"));
		mSHA384.setOnLongClickListener(new OnHashLongClick(sha384,"SHA384"));
		mSHA512.setOnLongClickListener(new OnHashLongClick(sha512,"SHA512"));
	}
	
	class OnHashLongClick implements View.OnLongClickListener{
		String text;
		String type;
		
		OnHashLongClick(String text,String type){
			this.text = text;
			this.type = type;
		}
		
		@Override
		public boolean onLongClick(View v){
			manager.set(text);
			Snackbar.make(mRoot,String.format(getResources().getString(R.string.copied),type),Snackbar.LENGTH_SHORT).show();
			return true;
		}
	}
}
