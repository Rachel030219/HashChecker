package net.rachel030219.hashchecker.activities;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ClipData;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.rachel030219.hashchecker.R;
import net.rachel030219.hashchecker.tools.ClipboardManager;
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

    SparseArray<String> mMap;

    ArrayList<SparseArray<String>> mDatas;
    ArrayList<CardView> mCards;

    boolean eMD5 = true;
    boolean eSHA1 = true;
    boolean eSHA256 = true;
    boolean eSHA384 = true;
    boolean eSHA512 = true;
    boolean eCRC32 = true;
    boolean hexCRC32 = false;
    boolean eTags = true;
    boolean eCover = true;

	ClipboardManager manager;
	
	SharedPreferences defaultPreferences;
	boolean uppercase;

    RecyclerView mRecycler;
    RecyclerAdapter adapter;

    boolean multiShare = false;
    boolean multiShareEnd = false;

    String[] colorList = new String[]{"#F8BBD0","#D1C4E9","#C5CAE9","#F0F4C3","#FFCCBC","#B2DFDB","#C8C6D9","#BBDEFB","#CFD8DC"};
    int nowColor = -1;
    int shouldUse = 0;
    ArrayList<Integer> shouldColor = new ArrayList<>();
	
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
		
        setContentView(R.layout.main);
		
		manager = new ClipboardManager(this);

		bindView();

        mMap = new SparseArray<>();
        mDatas = new ArrayList<>();
        mCards = new ArrayList<>();

        mRecycler = findViewById(R.id.recycler);
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
                startCalculation(REQUEST_FILE_PERMISSION_CODE_SHARE,open);
			} else if(action.equals(Intent.ACTION_SEND_MULTIPLE)){
                multiShare = true;
                startCalculation(REQUEST_FILE_PERMISSION_CODE_MULTI_SHARE,open);
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
        eTags = defaultPreferences.getBoolean("output_copyall_tag",true);
        eCover = defaultPreferences.getBoolean("output_cover",true);
        uppercase = defaultPreferences.getBoolean("output_case",true);
        if (eMD5)
            shouldUse = 2;
        else if (eSHA1)
            shouldUse = 3;
        else if (eSHA256)
            shouldUse = 4;
        else if (eSHA384)
            shouldUse = 5;
        else if (eSHA512)
            shouldUse = 6;
        else if (eCRC32)
            shouldUse = 7;
    }

	private void bindView(){
		mToolbar = findViewById(R.id.toolbar);
		mToolbar.setTitle(getTitle());
		setSupportActionBar(mToolbar);
		
		mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		mDrawerLayout = findViewById(R.id.drawer);
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
		
		NavigationView mNavigation = findViewById(R.id.navigation);
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
		
		mFab = findViewById(R.id.fab);
		mFab.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				startCalculation(REQUEST_FILE_PERMISSION_CODE_INAPP,null);
			}
		});
		
		mRoot = findViewById(R.id.rootLayout);
	}
	
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.setType("*/*"); 
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		try {
			startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
			for (int i = 0;i<mCards.size();i++) {
			    mCards.get(i).setCardBackgroundColor(getResources().getColor(android.R.color.white));
            }
		} catch (android.content.ActivityNotFoundException ex) {
			android.widget.Toast.makeText(this, "Please install a File Manager.", android.widget.Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            ClipData clipData = data.getClipData();
            if (clipData == null) {
                multiShare = false;
                updateResult(data.getData());
            } else {
                multiShare = true;
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    multiShareEnd = (i == clipData.getItemCount() - 1);
                    updateResult(clipData.getItemAt(i).getUri());
                }
            }
        }
		super.onActivityResult(requestCode,resultCode,data);
	}

    public void startCalculation(int REQUEST_CODE, Intent intent){
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
                if (eTags)
                    builder.append("[HashChecker]");
                String clipBefore = manager.get().toString();
                for(int i = 0;i < mDatas.size();i++){
                    if (eTags) {
                        builder.append("\n");
                        builder.append("[File]");
                    }
                    builder.append(mDatas.get(i).get(1));
                    if (eMD5) {
                        builder.append("\n");
                        if (eTags)
                            builder.append("[MD5]");
                        builder.append(mDatas.get(i).get(2));
                    }
                    if (eSHA1) {
                        builder.append("\n");
                        if (eTags)
                            builder.append("[SHA1]");
                        builder.append(mDatas.get(i).get(3));
                    }
                    if (eSHA256) {
                        builder.append("\n");
                        if (eTags)
                            builder.append("[SHA256]");
                        builder.append(mDatas.get(i).get(4));
                    }
                    if (eSHA384) {
                        builder.append("\n");
                        if (eTags)
                            builder.append("[SHA384]");
                        builder.append(mDatas.get(i).get(5));
                    }
                    if (eSHA512) {
                        builder.append("\n");
                        if (eTags)
                            builder.append("[SHA512]");
                        builder.append(mDatas.get(i).get(6));
                    }
                    if (eCRC32) {
                        builder.append("\n");
                        if (eTags)
                            builder.append("[CRC32]");
                        builder.append(mDatas.get(i).get(7));
                    }
                }
                if (!builder.toString().equals("[HashChecker]") && !builder.toString().equals("") && !builder.toString().equals(clipBefore)) {
                    manager.set(builder.toString());
                    Snackbar.make(mRoot, String.format(getResources().getString(R.string.copied),"all"), Snackbar.LENGTH_SHORT).show();
                }
                return true;
            case R.id.repeated:
                if (mCards.size() > 1 && mDatas.size() > 1) {
                    new Thread(){
                        @Override
                        public void run() {
                            for (int i1 = 0;i1 < mDatas.size() - 1;i1++){
                                for (int i2 = mDatas.size() - 1;i2 > 0;i2--) {
                                    if (shouldUse > 1 && i1 != i2 && mDatas.get(i1).get(shouldUse).toUpperCase().equals(mDatas.get(i2).get(shouldUse).toUpperCase())) {
                                        if (!shouldColor.contains(i1)) {
                                            shouldColor.add(i1);
                                        }
                                        if (!shouldColor.contains(i2)) {
                                            shouldColor.add(i2);
                                        }
                                    }
                                }
                            }
                            if (nowColor+1 == colorList.length) {
                                nowColor = 0;
                            } else {
                                nowColor++;
                            }
                            if (shouldColor.size() > 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i : shouldColor) {
                                            if (shouldUse > 1 && i >= 1 && !mDatas.get(shouldColor.get(shouldColor.indexOf(i))).get(shouldUse).toUpperCase().equals(mDatas.get(shouldColor.get(shouldColor.indexOf(i))).get(shouldUse).toUpperCase())) {
                                                if (nowColor + 1 == colorList.length) {
                                                    nowColor = 0;
                                                } else {
                                                    nowColor++;
                                                }
                                            }
                                            mCards.get(i).setCardBackgroundColor(Color.parseColor(colorList[nowColor]));
                                        }
                                    }
                                });
                            }
                            if (shouldColor.size() == mDatas.size()) {
                                boolean same = true;
                                for (int count : shouldColor) {
                                    if (same)
                                        same = mDatas.get(count).get(shouldUse).equals(mDatas.get(mDatas.size() - 1).get(shouldUse));
                                }
                                if (same)
                                    Snackbar.make(mRoot,R.string.same,Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }.start();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateResult(final Uri uri){
		try{
            Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
            returnCursor.moveToFirst();
            final String fileName = returnCursor.getString(returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

			final ProgressDialog dialog = ProgressDialog.show(MainActivity.this,null,"Calculating…",true);
			new Thread(){
				@Override
				public void run(){
                    getPreferences();

                    String md5 = null;
                    String sha1 = null;
                    String sha256 = null;
                    String sha384 = null;
                    String sha512 = null;
                    String crc32 = null;

                    try {
                        if (eMD5)
                            md5 = HashTool.getFileHash("MD5", new BufferedInputStream(getContentResolver().openInputStream(uri)));
                        if (eSHA1)
                            sha1 = HashTool.getFileHash("SHA1", new BufferedInputStream(getContentResolver().openInputStream(uri)));
                        if (eSHA256)
                            sha256 = HashTool.getFileHash("SHA256", new BufferedInputStream(getContentResolver().openInputStream(uri)));
                        if (eSHA384)
                            sha384 = HashTool.getFileHash("SHA384", new BufferedInputStream(getContentResolver().openInputStream(uri)));
                        if (eSHA512)
                            sha512 = HashTool.getFileHash("SHA512", new BufferedInputStream(getContentResolver().openInputStream(uri)));
                        if (eCRC32)
                            if (hexCRC32)
                                crc32 = MathTool.toHex(HashTool.getCRC32(new BufferedInputStream(getContentResolver().openInputStream(uri))));
                            else
                                crc32 = HashTool.getCRC32(new BufferedInputStream(getContentResolver().openInputStream(uri))) + "";
                    } catch (Exception e) {
                        android.util.Log.e("HashChecker exception", e.toString());
                    }
					if(uppercase){
                        mMap = new SparseArray<>();
                        mMap.put(1,fileName);

                        if (eMD5)
                            mMap.put(2, md5.toUpperCase());
                        if (eSHA1)
                            mMap.put(3, sha1.toUpperCase());
                        if (eSHA256)
                            mMap.put(4, sha256.toUpperCase());
                        if (eSHA384)
                            mMap.put(5, sha384.toUpperCase());
                        if (eSHA512)
                            mMap.put(6, sha512.toUpperCase());
                        if (eCRC32)
                            mMap.put(7, crc32.toUpperCase());

                        if((!multiShare && eCover))
                            mDatas = new ArrayList<>();
                        mDatas.add(mMap);
					} else {
                        mMap = new SparseArray<>();
                        mMap.put(1,fileName);

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

                        if((!multiShare && eCover))
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
            android.util.Log.e("HashChecker exception", e.toString());
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
            return new Holder(LayoutInflater.from(MainActivity.this).inflate(R.layout.recycler_item,parent,false));
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
                mResult = view.findViewById(R.id.result);
                mCards.add(mResult);
                mFile = view.findViewById(R.id.file);

                mMD5 = view.findViewById(R.id.md5);
                mSHA1 = view.findViewById(R.id.sha1);

                mSHA256 = view.findViewById(R.id.sha256);
                mSHA384 = view.findViewById(R.id.sha384);
                mSHA512 = view.findViewById(R.id.sha512);
                mCRC32 = view.findViewById(R.id.crc32);

                // EditText
                mCheckInput = view.findViewById(R.id.checkInput);
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
                    if(!check.equals(md5) && !check.equals(sha1) && !check.equals(sha256) && !check.equals(sha384) && !check.equals(sha512) && !check.equals(crc32)) {
                        holder.mCheckInput.setTextColor(Color.parseColor("#FF0000"));
                        holder.mMD5.setTextColor(Color.parseColor("#000000"));
                        holder.mSHA1.setTextColor(Color.parseColor("#000000"));
                        if (eSHA256) {
                            holder.mSHA256.setTextColor(Color.parseColor("#000000"));
                        }
                        if (eSHA384) {
                            holder.mSHA384.setTextColor(Color.parseColor("#000000"));
                        }
                        if (eSHA512) {
                            holder.mSHA512.setTextColor(Color.parseColor("#000000"));
                        }
                        if (eCRC32) {
                            holder.mCRC32.setTextColor(Color.parseColor("#000000"));
                        }
                    } else {
                        if(check.equals(md5)) {
                            holder.mMD5.setTextColor(Color.parseColor("#00FF00"));
                            holder.mSHA1.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA256.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA384.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA512.setTextColor(Color.parseColor("#000000"));
                            holder.mCRC32.setTextColor(Color.parseColor("#000000"));
                            holder.mCheckInput.setTextColor(Color.parseColor("#00FF00"));
                        } else if(check.equals(sha1)) {
                            holder.mMD5.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA1.setTextColor(Color.parseColor("#00FF00"));
                            holder.mSHA256.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA384.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA512.setTextColor(Color.parseColor("#000000"));
                            holder.mCRC32.setTextColor(Color.parseColor("#000000"));
                            holder.mCheckInput.setTextColor(Color.parseColor("#00FF00"));
                        } else if(eSHA256 && check.equals(sha256)) {
                            holder.mMD5.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA1.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA256.setTextColor(Color.parseColor("#00FF00"));
                            holder.mSHA384.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA512.setTextColor(Color.parseColor("#000000"));
                            holder.mCRC32.setTextColor(Color.parseColor("#000000"));
                            holder.mCheckInput.setTextColor(Color.parseColor("#00FF00"));
                        } else if(eSHA384 && check.equals(sha384)) {
                            holder.mMD5.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA1.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA256.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA384.setTextColor(Color.parseColor("#00FF00"));
                            holder.mSHA512.setTextColor(Color.parseColor("#000000"));
                            holder.mCRC32.setTextColor(Color.parseColor("#000000"));
                            holder.mCheckInput.setTextColor(Color.parseColor("#00FF00"));
                        } else if(eSHA512 && check.equals(sha512)) {
                            holder.mMD5.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA1.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA256.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA384.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA512.setTextColor(Color.parseColor("#00FF00"));
                            holder.mCRC32.setTextColor(Color.parseColor("#000000"));
                            holder.mCheckInput.setTextColor(Color.parseColor("#00FF00"));
                        } else if (eCRC32 && check.equals(crc32)){
                            holder.mMD5.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA1.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA256.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA384.setTextColor(Color.parseColor("#000000"));
                            holder.mSHA512.setTextColor(Color.parseColor("#000000"));
                            holder.mCRC32.setTextColor(Color.parseColor("#00FF00"));
                            holder.mCheckInput.setTextColor(Color.parseColor("#00FF00"));
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
                    check = text.toString();
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
