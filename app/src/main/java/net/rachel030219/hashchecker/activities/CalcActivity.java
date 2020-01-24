package net.rachel030219.hashchecker.activities;

import java.io.File;
import java.io.FileDescriptor;

import android.os.Bundle;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    FileDescriptor file;
    String calculationValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainIntent = getIntent();
        if(mainIntent != null){
            Bundle extras = mainIntent.getExtras();
            value = (String) extras.get("net.rachel030219.hashchecker.extra.VALUE");
            file = (FileDescriptor) extras.get("net.rachel030219.hashchecker.extra.FILE");
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    Dialog dialog = ProgressDialog.show(CalcActivity.this, null, "Calculating…", true);
                    new Thread(){
                        @Override
                        public void run() {
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
                    }.start();
                    dialog.dismiss();
                }
            });
        }
    }

    public void returnResult() {
        if (calculationValue != null) {
            if (mainIntent.getAction().startsWith("net.rachel030219.hashchecker.action.COMPARE")) {
                if (value.toUpperCase().equals(calculationValue.toUpperCase()))
                    setResult(100);
                else
                    setResult(10);
            } else {
                mainIntent.putExtra("net.rachel030219.hashchecker.extra.RESULT_VALUE",calculationValue);
                setResult(100, mainIntent);
            }
        } else {
            setResult(10);
        }
        finish();
    }
}
