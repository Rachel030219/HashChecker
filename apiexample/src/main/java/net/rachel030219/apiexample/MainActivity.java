package net.rachel030219.apiexample;
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
import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.net.Uri;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void calculate(View v){
        // start calculating
        Intent intent = new Intent();
        intent.setAction("net.rachel030219.hashchecker.action.CALCULATE_MD5");                      // Turn in action
        intent.putExtra("net.rachel030219.hashchecker.extra.URI",Uri.parse(
                "file:///storage/emulated/0/Android/obb/.nomedia"));                                          // Turn in uri
        // intent.putExtra("net.rachel030219.hashchecker.extra.FILE",new File(
        //         "/storage/emulated/0/Android/obb/.nomedia"));                                    // Select one(prefer uri)
        intent.putExtra("net.rachel030219.hashchecker.extra.VALUE",
                "D41D8CD98F00B204E9800998ECF8427E");                                                // Turn in value to compare with. For .nomedia files, it is usually D41D8CD98F00B204E9800998ECF8427E
        intent.setType("*/*");                                                                      // I don't know what its type is, so just type "*/*"
        startActivityForResult(intent,REQUEST_CODE);                                                // Let's try it now!
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            switch (resultCode){
                case 100:                                                                           // This means your value matches your file
                    Toast.makeText(this, "Yay! Matches!", Toast.LENGTH_SHORT).show();
                    break;
                case -100:                                                                          // This means your value does not match your file
                    Toast.makeText(this, "What? Does not match…", Toast.LENGTH_SHORT).show();
                    break;
                default:                                                                            // This means "这届用户不行啊"
                    Toast.makeText(this, "What's wrong?", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
