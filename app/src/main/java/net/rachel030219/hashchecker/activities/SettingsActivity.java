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
import android.view.MenuItem;
import android.view.WindowManager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.rachel030219.hashchecker.R;

public class SettingsActivity extends AppCompatActivity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);

		Toolbar mToolbar = (Toolbar)findViewById(R.id.settings_toolbar);
		mToolbar.setTitle(getTitle());
		setSupportActionBar(mToolbar);

		ActionBar mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();    
			localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
		}
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
}
