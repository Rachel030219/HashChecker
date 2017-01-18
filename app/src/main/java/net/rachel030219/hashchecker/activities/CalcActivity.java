package net.rachel030219.hashchecker.activities;

import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by rachel on 17-1-18.
 * Uses to open HashChecker's API
 * This activity SHOULD NOT be launched normally.
 * For more information, please go README.md
 */

public class CalcActivity extends AppCompatActivity {
    Intent mainIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainIntent = getIntent();
        if(mainIntent != null){
            Uri uri = mainIntent.getParcelableExtra("net.rachel030219.hashchecker.extra.URI");
            String value = mainIntent.getParcelableExtra("net.rachel030219.hashchecker.extra.VALUE");
            switch(mainIntent.getAction()){
                case "net.rachel030219.hashchecker.action.CALCULATE_MD5":
                    // TODO:Calculate and compare md5
                    break;
                case "net.rachel030219.hashchecker.action.CALCULATE_SHA1":
                    // TODO:Calculate and compare sha1
                    break;
                case "net.rachel030219.hashchecker.action.CALCULATE_SHA256":
                    // TODO:Calculate and compare sha256
                    break;
                case "net.rachel030219.hashchecker.action.CALCULATE_SHA384":
                    // TODO:Calculate and compare sha384
                    break;
                case "net.rachel030219.hashchecker.action.CALCULATE_SHA512":
                    // TODO:Calculate and compare sha512
                    break;
                case "net.rachel030219.hashchecker.action.CALCULATE_CRC32_HEX":
                    // TODO:Calculate and compare crc32(hex)
                    break;
                case "net.rachel030219.hashchecker.action.CALCULATE_CRC32_DEC":
                    // TODO:Calculate and compare crc32(dec)
                    break;
                default:
                    // 去你丫的你在干嘛？
                    break;
            }
        }
    }

    public void returnResult(boolean result){
        if (result)
            setResult(RESULT_OK);
        else
            setResult(RESULT_CANCELED);
    }
}
