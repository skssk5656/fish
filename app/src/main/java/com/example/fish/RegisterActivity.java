package com.example.fish;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterTask";

    private static final String SERVER_URL = "http://52.78.205.192/Register2.php";
    private EditText userID;
    private EditText userPassword;
    private EditText userName;
    private EditText userMajor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userID = findViewById(R.id.userid);
        userPassword = findViewById(R.id.password);
        userName = findViewById(R.id.name);
        userMajor = findViewById(R.id.major);

        TextView registerButton = findViewById(R.id.sign_up5);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = userID.getText().toString();
                String userpassword = userPassword.getText().toString();
                String username = userName.getText().toString();
                String usermajor = userMajor.getText().toString();

                performRegistration(userid, userpassword, username, usermajor);
            }
        });
    }

    private void performRegistration(String userid, String userpassword, String username, String usermajor) {

        JSONObject postData = new JSONObject();
        try {
            postData.put("userID", userid);
            postData.put("userPassword", userpassword);
            postData.put("userName", username);
            postData.put("userMajor", usermajor);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(SERVER_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setDoOutput(true);

            // 데이터 전송
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(postData.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();

            // 서버 응답 확인
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 응답 데이터 수신
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 응답 데이터 처리
                JSONObject jsonResponse = new JSONObject(response.toString());
                boolean success = jsonResponse.getBoolean("success");
                if (success) {
                    // 회원가입 성공
                    Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                } else {
                    // 회원가입 실패
                    Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("RegisterActivity", "Server response code: " + responseCode);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}


