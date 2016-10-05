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
package net.rachel030219.hash;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.content.DialogInterface;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.CoordinatorLayout;

public class DonateActivity extends AppCompatActivity{
	CoordinatorLayout rootLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donate);

		Toolbar mToolbar = (Toolbar)findViewById(R.id.donate_toolbar);
		mToolbar.setTitle(getTitle());
		setSupportActionBar(mToolbar);

		ActionBar mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();    
			localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
		}
		
		rootLayout = (CoordinatorLayout)findViewById(R.id.donate_root);
		
		ImageView donateImage = (ImageView)findViewById(R.id.donate_image);
		donateImage.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View v){
				AlertDialog.Builder builder = new AlertDialog.Builder(DonateActivity.this);
				builder.setItems(R.array.donate_items,new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int count){
						switch(count){
							case 0:
								try{
									InputStream is = DonateActivity.this.getAssets().open("donate.png");
									FileOutputStream fos = new FileOutputStream(new File("/sdcard/donate.png"));
									byte[] buffer = new byte[1024];
									int byteCount = 0;
									while((byteCount = is.read(buffer)) != -1){
										fos.write(buffer,0,byteCount);
									}
									fos.flush();
									is.close();
									fos.close();
									Snackbar.make(rootLayout,R.string.donate_saved,Snackbar.LENGTH_SHORT).show();
								} catch (Exception e){
									Snackbar.make(rootLayout,"Something wrong....",Snackbar.LENGTH_SHORT).show();
								}
						}
					}
				});
				builder.show();
				return true;
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
}
