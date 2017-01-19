package net.rachel030219.hashchecker.activities;

import java.io.File;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.net.Uri;
import android.content.Intent;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import net.rachel030219.hashchecker.R;
import net.rachel030219.hashchecker.tools.FileUtils;
import net.rachel030219.hashchecker.tools.HashTool;
import net.rachel030219.hashchecker.tools.MathTool;

/**
 * Created by rachel on 17-1-18.
 * Uses to open HashChecker's API
 * This activity SHOULD NOT be launched normally.
 * For more information, please go README.md
 */

public class CalcActivity extends AppCompatActivity {
    Intent mainIntent;
    Uri uri;
    File file;
    String calculationValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainIntent = getIntent();
        if(mainIntent != null){
            uri = mainIntent.getParcelableExtra("net.rachel030219.hashchecker.extra.URI");
            if (uri == null) {
                file = mainIntent.getParcelableExtra("net.rachel030219.hashchecker.extra.FILE");
            } else {
                file = new File(FileUtils.getPath(this,uri));
            }
            ProgressDialog.show(this, null, "Calculating…", true);
            Handler handler = new Handler();
            handler.post(new Runnable(){
                @Override
                public void run(){
                    String value = mainIntent.getParcelableExtra("net.rachel030219.hashchecker.extra.VALUE");
                    value = value.toUpperCase();
                    switch(mainIntent.getAction()){
                        case "net.rachel030219.hashchecker.action.CALCULATE_MD5":
                            calculationValue = HashTool.getFileHash("MD5", file).toUpperCase();
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_SHA1":
                            calculationValue = HashTool.getFileHash("SHA1", file).toUpperCase();
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_SHA256":
                            calculationValue = HashTool.getFileHash("SHA256", file).toUpperCase();
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_SHA384":
                            calculationValue = HashTool.getFileHash("SHA384", file).toUpperCase();
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_SHA512":
                            calculationValue = HashTool.getFileHash("SHA512", file).toUpperCase();
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_CRC32_HEX":
                            calculationValue = MathTool.toHex(HashTool.getCRC32(file)).toUpperCase();
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_CRC32_DEC":
                            calculationValue = (HashTool.getCRC32(file) + "").toUpperCase();
                            break;
                        default:
                            // 去你丫的你在干嘛？
                            break;
                    }
                    if (value.equals(calculationValue)){
                        returnResult(true);
                    } else {
                        returnResult(false);
                    }
                }
            });
        }
    }

    public void returnResult(boolean result){
        if (result)
            setResult(100);
        else
            setResult(-100);
        finish();
    }
}
