package com.example.fish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // 데이터를 받아올 php 주소
    String url = "http://52.78.205.192/getjson.php";
    String previousBite = null;

    RequestQueue queue;
    TextView textView;
    TextView Temp;
    TextView BITE;
    TextView DO;
    TextView WEATHER;
    Button btn;

    private int j = 0;

    private RequestQueue mRequestQueue;

    private void createNotificationAndIncrease() {
        j += 1;
        // 알림을 생성하는 코드

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // 알림 아이콘 설정
                .setContentTitle("입질이 발생하였습니다.") // 알림 제목 설정
                .setContentText("낚시대를 확인해 주세요") // 알림 내용 설정
                .setPriority(NotificationCompat.PRIORITY_HIGH); // 알림 우선순위 설정

        // 알림을 표시
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build()); // 1은 알림 식별자입니다.
    }

    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // 알림 아이콘 설정
                .setContentTitle("도난이 의심되는 상황입니다") // 알림 제목 설정
                .setContentText("낚시대를 확인해 주세요") // 알림 내용 설정
                .setPriority(NotificationCompat.PRIORITY_HIGH); // 알림 우선순위 설정

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build()); // 1은 알림 식별자입니다.
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.k);
        Temp = findViewById(R.id.k3);
        DO = findViewById(R.id.k4);
        BITE = findViewById(R.id.k2);
        WEATHER = findViewById(R.id.k6);

        btn = (Button)findViewById(R.id.message_icon);

        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
        // Volley 요청 대기열 초기화
        queue = Volley.newRequestQueue(getApplicationContext());

        // 일정 시간마다 자동으로 데이터를 업데이트하기 위해 Handler 객체 생성
        final android.os.Handler handler = new android.os.Handler();
        final int delay = 5000; // 5초마다 업데이트
        handler.postDelayed(new Runnable() {
            public void run() {
                // Volley를 사용하여 데이터 가져오기
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> task) {
                                                if(!task.isSuccessful()) {
                                                    Log.w("FCM Token", "토큰 가져오기 실패", task.getException());
                                                    return;
                                                }
                                                String token = task.getResult();
                                                Log.d("FCM Token", token);
                                            }
                                        });
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                        String data = jsonObject.getString("SE");
                                        String TEMP = jsonObject.getString("TEMP");
                                        String Do = jsonObject.getString("DO");
                                        String bite = jsonObject.getString("BITE");
                                        String weather = jsonObject.getString("WEATHER");
                                        Log.d("MainActivity", "Data: " + bite);
                                        textView.setText(data);
                                        Temp.setText(TEMP + "°C");
                                        DO.setText(Do + "mg");
                                        WEATHER.setText(weather);

                                        if(textView.equals("security_on")) {
                                            createNotification();
                                        }

                                        if(bite.equals("no catch. ")) {
                                            Log.d("일치", "Data");

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    createNotificationAndIncrease();
                                                }
                                            }, 5000);
                                        }
                                        BITE.setText(j + "회");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textView.setText(error.getMessage());
                    }
                });

                // 요청 대기열에 추가
                queue.add(stringRequest);

                // 일정 시간이 지난 후에 다시 실행
                handler.postDelayed(this, delay);
            }
        }, delay);
    }
}




