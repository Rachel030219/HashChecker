package net.rachel030219.hashchecker.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;

import net.rachel030219.hashchecker.R;
import net.rachel030219.hashchecker.tools.ClipboardManager;
import net.rachel030219.hashchecker.tools.FileUtils;
import net.rachel030219.hashchecker.tools.HashTool;
import net.rachel030219.hashchecker.tools.MathTool;

public class MainActivity extends AppCompatActivity {
	private static final int FILE_SELECT_CODE = 1;
	private static final int REQUEST_FILE_PERMISSION_CODE_INAPP = 2;
	private static final int REQUEST_FILE_PERMISSION_CODE_SHARE = 3;
    private static final int REQUEST_FILE_PERMISSION_CODE_MULTI_SHARE = 4;
	
	Toolbar mToolbar;
	ActionBar mActionBar;
	DrawerLayout mDrawerLayout;
	ActionBarDrawerToggle mDrawerToggle;
	FloatingActionButton mFab;
	CoordinatorLayout mRoot;
	
	String md5 = null;
	String sha1 = null;
	String sha256 = null;
	String sha384 = null;
	String sha512 = null;
    String crc32 = null;

    HashMap<Integer,String> mMap;

    ArrayList<HashMap<Integer,String>> mDatas;

    boolean eMD5 = true;
    boolean eSHA1 = true;
    boolean eSHA256 = true;
    boolean eSHA384 = true;
    boolean eSHA512 = true;
    boolean eCRC32 = true;
    boolean hexCRC32 = false;
    boolean eCover = true;

	ClipboardManager manager;
	
	SharedPreferences defaultPreferences;
	boolean uppercase;

    RecyclerView mRecycler;
    RecyclerAdapter adapter;

    boolean multiShare = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
		
        setContentView(R.layout.main);
		
		manager = new ClipboardManager(this);

		bindView();
		checkUpdated();

        mMap = new HashMap<>();
        mDatas = new ArrayList<>();

        mRecycler = (RecyclerView)findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter();
        mRecycler.setAdapter(adapter);

		// handle share
		Intent open = getIntent();
		if(open != null){
			String action = open.getAction();
			String type = open.getType();
			if(action.equals(Intent.ACTION_SEND) && type != null){
                multiShare = false;
                checkPermissions(REQUEST_FILE_PERMISSION_CODE_SHARE,open);
			} else if(action.equals(Intent.ACTION_SEND_MULTIPLE)){
                multiShare = true;
                checkPermissions(REQUEST_FILE_PERMISSION_CODE_MULTI_SHARE,open);
            }
		}
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferences();
    }

    private void getPreferences(){
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        eMD5 = defaultPreferences.getBoolean("output_md5",true);
        eSHA1 = defaultPreferences.getBoolean("output_sha1",true);
        eSHA256 = defaultPreferences.getBoolean("output_sha256",true);
        eSHA384 = defaultPreferences.getBoolean("output_sha384",true);
        eSHA512 = defaultPreferences.getBoolean("output_sha512",true);
        eCRC32 = defaultPreferences.getBoolean("output_crc32",true);
        hexCRC32 = defaultPreferences.getBoolean("output_crc32_hex",false);
        eCover = defaultPreferences.getBoolean("output_cover",true);
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
				checkPermissions(REQUEST_FILE_PERMISSION_CODE_INAPP,null);
			}
		});
		
		mRoot = (CoordinatorLayout)findViewById(R.id.rootLayout);
	}
	
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
		intent.setType("*/*"); 
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		
		try {
            multiShare = false;
			startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			android.widget.Toast.makeText(this, "Please install a File Manager.", android.widget.Toast.LENGTH_SHORT).show();
		}
	}
	
	public void checkUpdated(){
		final SharedPreferences preferences = getSharedPreferences("updated",MODE_PRIVATE);
		if(!preferences.getBoolean("updated14",false)){
            preferences.edit().clear().apply();
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.updated_title);
			dialog.setMessage(R.string.updated_changelog);
			dialog.setPositiveButton("GOT IT",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog,int count){
					preferences.edit().putBoolean("updated14",true).apply();
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
					updateResult(data.getData());
				}
				break;
		}
		super.onActivityResult(requestCode,resultCode,data);
	}

    public void checkPermissions(int REQUEST_CODE,Intent intent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
            } else {
                switch(REQUEST_CODE){
                    case REQUEST_FILE_PERMISSION_CODE_INAPP:
                        showFileChooser();
                        break;
                    case REQUEST_FILE_PERMISSION_CODE_SHARE:
                        Uri data = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                        updateResult(data);
                        break;
                    case REQUEST_FILE_PERMISSION_CODE_MULTI_SHARE:
                        ArrayList<Uri> list = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                        for(int i = 0;i < list.size();i++){
                            updateResult(list.get(i));
                        }
                        break;
                }
            }
        } else {
            switch(REQUEST_CODE){
                case REQUEST_FILE_PERMISSION_CODE_INAPP:
                    showFileChooser();
                    break;
                case REQUEST_FILE_PERMISSION_CODE_SHARE:
                    Uri data = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    updateResult(data);
                    break;
                case REQUEST_FILE_PERMISSION_CODE_MULTI_SHARE:
                    ArrayList<Uri> list = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                    for(int i = 0;i < list.size();i++){
                        updateResult(list.get(i));
                    }
                    break;
            }
        }
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		int grantResult = grantResults[0];
		if (grantResult == PackageManager.PERMISSION_GRANTED){
			switch(requestCode){
				case REQUEST_FILE_PERMISSION_CODE_INAPP:
					showFileChooser();
					break;
				case REQUEST_FILE_PERMISSION_CODE_SHARE:
					updateResult((Uri)getIntent().getParcelableExtra(Intent.EXTRA_STREAM));
					break;
			}
		} else if(grantResult == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this,R.string.permission_denied,Toast.LENGTH_LONG).show();
			finish();
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.copyall:
                StringBuilder builder = new StringBuilder();
                builder.append("[HashChecker]");
                String clipBefore = manager.get().toString();
                for(int i = 0;i < mDatas.size();i++){
                    builder.append("\n[File]");
                    builder.append(mDatas.get(i).get(1));
                    if (eMD5) {
                        builder.append("\n[MD5]");
                        builder.append(mDatas.get(i).get(2));
                    }
                    if (eSHA1) {
                        builder.append("\n[SHA1]");
                        builder.append(mDatas.get(i).get(3));
                    }
                    if (eSHA256) {
                        builder.append("\n[SHA256]");
                        builder.append(mDatas.get(i).get(4));
                    }
                    if (eSHA384) {
                        builder.append("\n[SHA384]");
                        builder.append(mDatas.get(i).get(5));
                    }
                    if (eSHA512) {
                        builder.append("\n[SHA512]");
                        builder.append(mDatas.get(i).get(6));
                    }
                    if (eCRC32) {
                        builder.append("\n[CRC32]");
                        builder.append(mDatas.get(i).get(7));
                    }
                }
                if (!builder.toString().equals("[HashChecker]") && !builder.toString().equals(clipBefore)) {
                    manager.set(builder.toString());
                    Snackbar.make(mRoot, String.format(getResources().getString(R.string.copied),"all"), Snackbar.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateResult(Uri uri){
		try{
			final File file = new File(FileUtils.getPath(this,uri));

			final ProgressDialog dialog = ProgressDialog.show(MainActivity.this,null,"Calculating…",true);
			new Thread(){
				@Override
				public void run(){
                    getPreferences();
                    if (eMD5)
					    md5 = HashTool.getFileHash("MD5",file);
                    if (eSHA1)
					    sha1 = HashTool.getFileHash("SHA1",file);
                    if (eSHA256)
					    sha256 = HashTool.getFileHash("SHA256",file);
                    if (eSHA384)
					    sha384 = HashTool.getFileHash("SHA384",file);
                    if (eSHA512)
                        sha512 = HashTool.getFileHash("SHA512",file);
                    if (eCRC32)
                        if (hexCRC32)
                            crc32 = MathTool.toHex(HashTool.getCRC32(file));
                        else
                            crc32 = HashTool.getCRC32(file) + "";

					if(uppercase){
                        mMap = new HashMap<>();
                        mMap.put(1,file.getAbsolutePath());

                        if (eMD5)
						    mMap.put(2,md5.toUpperCase());
                        if (eSHA1)
                            mMap.put(3,sha1.toUpperCase());
                        if (eSHA256)
						    mMap.put(4,sha256.toUpperCase());
                        if (eSHA384)
						    mMap.put(5,sha384.toUpperCase());
                        if (eSHA512)
						    mMap.put(6,sha512.toUpperCase());
                        if (eCRC32)
                            mMap.put(7,crc32.toUpperCase());

                        if(!multiShare && eCover)
                            mDatas = new ArrayList<>();
                        mDatas.add(mMap);
					} else {
                        mMap = new HashMap<>();
                        mMap.put(1,file.getAbsolutePath());

                        if (eMD5)
                            mMap.put(2,md5.toLowerCase());
                        if (eSHA1)
                            mMap.put(3,sha1.toLowerCase());
                        if (eSHA256)
                            mMap.put(4,sha256.toLowerCase());
                        if (eSHA384)
                            mMap.put(5,sha384.toLowerCase());
                        if (eSHA512)
                            mMap.put(6,sha512.toLowerCase());
                        if (eCRC32)
                            mMap.put(7,crc32.toLowerCase());

                        if(!multiShare && eCover)
                            mDatas = new ArrayList<>();
                        mDatas.add(mMap);
					}

					runOnUiThread(new Runnable(){
							@Override
							public void run(){
								dialog.dismiss();
                                adapter.notifyDataSetChanged();
							}
						});
				}
			}.start();
		} catch (Exception e) {

		}
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

    // 以下是 RecyclerView 的 Adapter
    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder>{
        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Resources res = getResources();

            String path = mDatas.get(position).get(1);
            String md5 = mDatas.get(position).get(2);
            String sha1 = mDatas.get(position).get(3);
            String sha256 = mDatas.get(position).get(4);
            String sha384 = mDatas.get(position).get(5);
            String sha512 = mDatas.get(position).get(6);
            String crc32 = mDatas.get(position).get(7);

            holder.mFile.setText(String.format(res.getString(R.string.file),path));
            
            holder.mMD5.setText(String.format(res.getString(R.string.md5),md5));
            holder.mSHA1.setText(String.format(res.getString(R.string.sha1),sha1));
            holder.mSHA256.setText(String.format(res.getString(R.string.sha256),sha256));
            holder.mSHA384.setText(String.format(res.getString(R.string.sha384),sha384));
            holder.mSHA512.setText(String.format(res.getString(R.string.sha512),sha512));
            holder.mCRC32.setText(String.format(res.getString(R.string.crc32),crc32));
            holder.mCheckInput.setText("");

            holder.mMD5.setOnLongClickListener(new OnHashLongClick(md5,"MD5"));
            holder.mSHA1.setOnLongClickListener(new OnHashLongClick(sha1,"SHA1"));
            holder.mSHA256.setOnLongClickListener(new OnHashLongClick(sha256,"SHA256"));
            holder.mSHA384.setOnLongClickListener(new OnHashLongClick(sha384,"SHA384"));
            holder.mSHA512.setOnLongClickListener(new OnHashLongClick(sha512,"SHA512"));
            holder.mCRC32.setOnLongClickListener(new OnHashLongClick(crc32,"CRC32"));

            holder.mCheckInput.addTextChangedListener(new Watcher(holder,md5,sha1,sha256,sha384,sha512,crc32));

            if (eMD5)
                holder.mMD5.setVisibility(View.VISIBLE);
            else
                holder.mMD5.setVisibility(View.GONE);
            if (eSHA1)
                holder.mSHA1.setVisibility(View.VISIBLE);
            else
                holder.mSHA1.setVisibility(View.GONE);
            if (eSHA256)
                holder.mSHA256.setVisibility(View.VISIBLE);
            else
                holder.mSHA256.setVisibility(View.GONE);
            if (eSHA384)
                holder.mSHA384.setVisibility(View.VISIBLE);
            else
                holder.mSHA384.setVisibility(View.GONE);
            if (eSHA512)
                holder.mSHA512.setVisibility(View.VISIBLE);
            else
                holder.mSHA512.setVisibility(View.GONE);
            if (eCRC32)
                holder.mCRC32.setVisibility(View.VISIBLE);
            else
                holder.mCRC32.setVisibility(View.GONE);
        }
        
        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            Holder holder = new Holder(LayoutInflater.from(MainActivity.this).inflate(R.layout.recycler_item,parent,false));
            return holder;
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class Holder extends RecyclerView.ViewHolder{
            CardView mResult;
            TextView mFile;
            TextView mMD5;
            TextView mSHA1;
            TextView mSHA256;
            TextView mSHA384;
            TextView mSHA512;
            TextView mCRC32;
            EditText mCheckInput;

            Holder(View view){
                super(view);
                // 常规
                mResult = (CardView)view.findViewById(R.id.result);
                mFile = (TextView)view.findViewById(R.id.file);

                mMD5 = (TextView)view.findViewById(R.id.md5);
                mSHA1 = (TextView)view.findViewById(R.id.sha1);

                mSHA256 = (TextView)view.findViewById(R.id.sha256);
                mSHA384 = (TextView)view.findViewById(R.id.sha384);
                mSHA512 = (TextView)view.findViewById(R.id.sha512);
                mCRC32 = (TextView)view.findViewById(R.id.crc32);

                // EditText
                mCheckInput = (EditText)view.findViewById(R.id.checkInput);
                mCheckInput.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        v.requestFocus();
                        v.setFocusableInTouchMode(true);
                    }
                });
            }
        }

        class Watcher implements TextWatcher{
            Holder holder;
            String md5;
            String sha1;
            String sha256;
            String sha384;
            String sha512;
            String crc32;

            Watcher(Holder holder,String md5,String sha1,String sha256,String sha384,String sha512,String crc32){
                this.holder = holder;
                this.md5 = md5;
                this.sha1 = sha1;
                this.sha256 = sha256;
                this.sha384 = sha384;
                this.sha512 = sha512;
                this.crc32 = crc32;
            }
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after){

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count){
                String check = null;
                if (!text.equals(""))
                    check = text.subSequence(0,text.toString().length()).toString();
                if(check != null){
                    if(!check.toString().equals(md5) && !check.toString().equals(sha1) && !check.toString().equals(sha256) && !check.toString().equals(sha384) && !check.toString().equals(sha512) && !check.toString().equals(crc32)) {
                        holder.mCheckInput.setTextColor(Color.parseColor("#FF0000"));
                        holder.mMD5.setTextColor(Color.parseColor("#797979"));
                        holder.mSHA1.setTextColor(Color.parseColor("#797979"));
                        if (eSHA256) {
                            holder.mSHA256.setTextColor(Color.parseColor("#797979"));
                        }
                        if (eSHA384) {
                            holder.mSHA384.setTextColor(Color.parseColor("#797979"));
                        }
                        if (eSHA512) {
                            holder.mSHA512.setTextColor(Color.parseColor("#797979"));
                        }
                        if (eCRC32) {
                            holder.mCRC32.setTextColor(Color.parseColor("#797979"));
                        }
                    } else {
                        if(check.toString().equals(md5)) {
                            holder.mMD5.setTextColor(Color.parseColor("#00CD00"));
                            holder.mSHA1.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA256.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA384.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA512.setTextColor(Color.parseColor("#797979"));
                            holder.mCRC32.setTextColor(Color.parseColor("#797979"));
                            holder.mCheckInput.setTextColor(Color.parseColor("#00CD00"));
                        } else if(check.toString().equals(sha1)) {
                            holder.mMD5.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA1.setTextColor(Color.parseColor("#00CD00"));
                            holder.mSHA256.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA384.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA512.setTextColor(Color.parseColor("#797979"));
                            holder.mCRC32.setTextColor(Color.parseColor("#797979"));
                            holder.mCheckInput.setTextColor(Color.parseColor("#00CD00"));
                        } else if(eSHA256 && check.toString().equals(sha256)) {
                            holder.mMD5.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA1.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA256.setTextColor(Color.parseColor("#00CD00"));
                            holder.mSHA384.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA512.setTextColor(Color.parseColor("#797979"));
                            holder.mCRC32.setTextColor(Color.parseColor("#797979"));
                            holder.mCheckInput.setTextColor(Color.parseColor("#00CD00"));
                        } else if(eSHA384 && check.toString().equals(sha384)) {
                            holder.mMD5.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA1.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA256.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA384.setTextColor(Color.parseColor("#00CD00"));
                            holder.mSHA512.setTextColor(Color.parseColor("#797979"));
                            holder.mCRC32.setTextColor(Color.parseColor("#797979"));
                            holder.mCheckInput.setTextColor(Color.parseColor("#00CD00"));
                        } else if(eSHA512 && check.toString().equals(sha512)) {
                            holder.mMD5.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA1.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA256.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA384.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA512.setTextColor(Color.parseColor("#00CD00"));
                            holder.mCRC32.setTextColor(Color.parseColor("#797979"));
                            holder.mCheckInput.setTextColor(Color.parseColor("#00CD00"));
                        } else if (eCRC32 && check.toString().equals(crc32)){
                            holder.mMD5.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA1.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA256.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA384.setTextColor(Color.parseColor("#797979"));
                            holder.mSHA512.setTextColor(Color.parseColor("#797979"));
                            holder.mCRC32.setTextColor(Color.parseColor("#00CD00"));
                            holder.mCheckInput.setTextColor(Color.parseColor("#00CD00"));
                        }
                    }
                }
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
                            int selection = holder.mCheckInput.getSelectionStart();
                            holder.mCheckInput.setText(text.toString().toUpperCase());
                            holder.mCheckInput.setSelection(selection);
                        }
                    }
                } else {
                    if(check != null){
                        Matcher matcher = upperPattern.matcher(check);
                        if(matcher.find()){
                            int selection = holder.mCheckInput.getSelectionStart();
                            holder.mCheckInput.setText(text.toString().toLowerCase());
                            holder.mCheckInput.setSelection(selection);
                        }
                    }
                }
            }
        }
    }
}
