/*
 Copyright 2017 Rachel030219

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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.rachel030219.hashchecker.R;

public class AboutActivity extends AppCompatActivity{

	TextView mAboutMe;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		Toolbar mToolbar = findViewById(R.id.about_toolbar);
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
		mAboutMe = findViewById(R.id.about_me);
		mAboutMe.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("https://me.stfw.info"));
				startActivity(intent);
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId() == android.R.id.home)
			finish();
		return true;
	}
}
