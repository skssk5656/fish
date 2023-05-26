package com.example.fish;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // 데이터를 받아올 php 주소
    String url = "http://52.78.205.192/getjson.php";

    RequestQueue queue;
    TextView textView;
    TextView Temp;
    TextView BITE;
    TextView DO;
    TextView WEATHER;
    Button btn;

    private RequestQueue mRequestQueue;

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
                                try {
                                    int j = 0;
                                    JSONObject jsonObject = new JSONObject(response);
                                        String data = jsonObject.getString("SE");
                                        String TEMP = jsonObject.getString("TEMP");
                                        String Do = jsonObject.getString("DO");
                                        String bite = jsonObject.getString("BITE");
                                        String weather = jsonObject.getString("WEATHER");
                                        Log.d("MainActivity", "Data: " + bite);
                                        textView.setText(data);
                                        Temp.setText(TEMP);
                                        DO.setText(Do);
                                        WEATHER.setText(weather);

                                        if(bite.equals("catch.") == true) {
                                            j += 1;
                                            // 알림을 생성하는 코드
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default")
                                                    .setSmallIcon(R.drawable.ome_icon) // 알림 아이콘 설정
                                                    .setContentTitle("CATCH!") // 알림 제목 설정
                                                    .setContentText("물고기를 잡았습니다.") // 알림 내용 설정
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT); // 알림 우선순위 설정

                                            // 알림을 표시
                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                            notificationManager.notify(1, builder.build()); // 1은 알림 식별자입니다.

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




