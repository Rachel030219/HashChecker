/*
 Copyright 2016 Rachel030219

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package net.rachel030219.hashchecker.activities;

import android.os.Build;
import android.os.Bundle;
import android.net.Uri;
import android.view.View;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.rachel030219.hashchecker.R;

public class AboutActivity extends AppCompatActivity{
	TextView mPreferenceFragmentCompat;
	TextView mMaterialSettingsActivityDemo;
	
	TextView mAboutMe;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		Toolbar mToolbar = (Toolbar)findViewById(R.id.about_toolbar);
		mToolbar.setTitle(getTitle());
		setSupportActionBar(mToolbar);

		ActionBar mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);

		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();    
			localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
		}
		
		bindViews();
	}

	private void bindViews(){
		Resources res = getResources();
		mPreferenceFragmentCompat = (TextView)findViewById(R.id.about_projects_preferencefragment);
		mPreferenceFragmentCompat.setOnClickListener(new OnProjectClick(this,res.getString(R.string.about_projects_preferencefragment),res.getString(R.string.about_projects_preferencefragment_license),"https://github.com/Machinarius/PreferenceFragment-Compat"));
		mMaterialSettingsActivityDemo = (TextView)findViewById(R.id.about_projects_materialsettingsactivitydemo);
		mMaterialSettingsActivityDemo.setOnClickListener(new OnProjectClick(this,res.getString(R.string.about_projects_materialsettingsactivitydemo),res.getString(R.string.about_projects_materialsettingsactivitydemo_license),"https://drakeet.me/material-design-settings-activity"));
		
		mAboutMe = (TextView)findViewById(R.id.about_me);
		mAboutMe.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
				builder.setTitle(R.string.about_me_title);
				builder.setMessage(R.string.about_me_content);
				builder.setPositiveButton("GOT IT",null);
				builder.show();
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		// TODO: Implement this method
		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				break;
		}
		return true;
	}
	
	class OnProjectClick implements View.OnClickListener{
		Context context;
		String title;
		String license;
		String url;
		
		OnProjectClick(Context context,String title,String license,String url){
			this.context = context;
			this.title = title;
			this.license = license;
			this.url = url;
		}
		
		@Override
		public void onClick(View v){
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle(title);
			dialog.setMessage(license);
			dialog.setPositiveButton("GOT IT",null);
			dialog.setNegativeButton("MORE...",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog,int i){
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_VIEW,uri);
					startActivity(intent);
				}
			});
			dialog.show();
		}
	}
}
