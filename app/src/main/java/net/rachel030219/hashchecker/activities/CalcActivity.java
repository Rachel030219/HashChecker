package net.rachel030219.hashchecker.activities;

import java.io.File;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
    String value;
    Uri uri;
    File file;
    String calculationValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainIntent = getIntent();
        if(mainIntent != null){
            Bundle extras = mainIntent.getExtras();
            uri = (Uri)extras.get("net.rachel030219.hashchecker.extra.URI");
            value = (String)extras.get("net.rachel030219.hashchecker.extra.VALUE");
            if (uri == null) {
                file = (File)extras.get("net.rachel030219.hashchecker.extra.FILE");
            } else {
                file = new File(FileUtils.getPath(this,uri));
            }
            ProgressDialog.show(this, null, "Calculating…", true);
            Thread thread = new Thread(){
                @Override
                public void run(){
                    switch(mainIntent.getAction()){
                        case "net.rachel030219.hashchecker.action.COMPARE_MD5":
                            calculationValue = HashTool.getFileHash("MD5", file);
                            break;
                        case "net.rachel030219.hashchecker.action.COMPARE_SHA1":
                            calculationValue = HashTool.getFileHash("SHA1", file);
                            break;
                        case "net.rachel030219.hashchecker.action.COMPARE_SHA256":
                            calculationValue = HashTool.getFileHash("SHA256", file);
                            break;
                        case "net.rachel030219.hashchecker.action.COMPARE_SHA384":
                            calculationValue = HashTool.getFileHash("SHA384", file);
                            break;
                        case "net.rachel030219.hashchecker.action.COMPARE_SHA512":
                            calculationValue = HashTool.getFileHash("SHA512", file);
                            break;
                        case "net.rachel030219.hashchecker.action.COMPARE_CRC32_HEX":
                            calculationValue = MathTool.toHex(HashTool.getCRC32(file));
                            break;
                        case "net.rachel030219.hashchecker.action.COMPARE_CRC32_DEC":
                            calculationValue = HashTool.getCRC32(file) + "";
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_MD5":
                            calculationValue = HashTool.getFileHash("MD5", file);
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_SHA1":
                            calculationValue = HashTool.getFileHash("SHA1", file);
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_SHA256":
                            calculationValue = HashTool.getFileHash("SHA256", file);
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_SHA384":
                            calculationValue = HashTool.getFileHash("SHA384", file);
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_SHA512":
                            calculationValue = HashTool.getFileHash("SHA512", file);
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_CRC32_HEX":
                            calculationValue = MathTool.toHex(HashTool.getCRC32(file));
                            break;
                        case "net.rachel030219.hashchecker.action.CALCULATE_CRC32_DEC":
                            calculationValue = HashTool.getCRC32(file) + "";
                            break;
                        default:
                            // 去你丫的你在干嘛？
                            break;
                    }
                    returnResult();
                }
            };
            thread.start();
        }
    }

    public void returnResult(){
        if (mainIntent.getAction().matches("COMPARE") && calculationValue != null) {
            if (value.toUpperCase().equals(calculationValue.toUpperCase()))
                setResult(100);
            else
                setResult(-100);
        } else {
            Intent data = new Intent();
            data.putExtra("net.rachel030219.hashchecker.extra.VALUE", calculationValue);
            setResult(100, data);
        }
        //TODO: Hand value in data
        finish();
    }
}
