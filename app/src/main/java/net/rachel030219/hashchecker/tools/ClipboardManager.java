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
package net.rachel030219.hashchecker.tools;

public class ClipboardManager{
	android.text.ClipboardManager oldManager;
	android.content.ClipboardManager newManager;
	
	boolean newAPI = android.os.Build.VERSION.SDK_INT >= 11;
	
	public ClipboardManager(android.content.Context context){
		if(newAPI){
			newManager = (android.content.ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
		} else {
			oldManager = (android.text.ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
		}
	}
	
	public void set(String text){
		if(newAPI){
			newManager.setPrimaryClip(android.content.ClipData.newPlainText(null, text));
		} else {
			oldManager.setText(text);
		}
	}
	
	public CharSequence get(){
		if(newAPI){
			return newManager.getPrimaryClip().getItemAt(0).getText();
		} else {
			return oldManager.getText();
		}
	}
	
	public boolean has(){
		if(newAPI){
			return newManager.hasPrimaryClip();
		} else {
			return oldManager.hasText();
		}
	}
}
