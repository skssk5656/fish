package com.example.fish;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationListener;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {
    private static final String API_KEY = "811d37af9a9dcdb1b4abd557a20d24ea";
    private static final String API_BASE_URL = "http://api.openweathermap.org/data/2.5/";

    private ImageView weatherIconImageView;
    private TextView locationTextView;
    private TextView temperatureTextView;
    private TextView weatherDescriptionTextView;
    private TextView humidityTextView;
    private TextView windTextView;
    private TextView temperatureMinTextView;
    private TextView temperatureMaxTextView;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        weatherIconImageView = findViewById(R.id.weatherIcon);
        locationTextView = findViewById(R.id.locationText);
        temperatureTextView = findViewById(R.id.temperatureText);
        weatherDescriptionTextView = findViewById(R.id.weatherDescriptionText);
        humidityTextView = findViewById(R.id.humidityText);
        windTextView = findViewById(R.id.windText);
        temperatureMinTextView = findViewById(R.id.temperatureMinText);
        temperatureMaxTextView = findViewById(R.id.temperatureMaxText);

        // 위치 관련 객체 초기화
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // 위치가 변경될 때마다 해당 위치의 날씨 정보를 가져옴
                fetchWeatherByCoordinates(latitude, longitude);
            }
        };

        // 위치 권한이 허용되어 있는지 확인
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없으면 권한 요청
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // 권한이 있으면 위치 정보 요청
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                fetchWeatherByCoordinates(latitude, longitude);
            }
        }
    }

    private void fetchWeatherByCoordinates(double latitude, double longitude) {
        String apiUrl = API_BASE_URL + "weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY;

        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute(apiUrl);
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    // Handle error case
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONObject mainObject = json.getJSONObject("main");
                    JSONObject weatherObject = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject windObject = json.getJSONObject("wind");

                    double temperatureKelvin = mainObject.getDouble("temp");
                    double temperatureCelsius = temperatureKelvin - 273.15; // Kelvin to Celsius conversion
                    double humidity = mainObject.getDouble("humidity");
                    double windSpeed = windObject.getDouble("speed");
                    double temperatureMin = mainObject.getDouble("temp_min");
                    double temperatureMinC = temperatureMin - 273.15;
                    double temperatureMax = mainObject.getDouble("temp_max");
                    double temperatureMaxC = temperatureMax - 273.15;
                    String weatherDescription = weatherObject.getString("description");
                    String weatherIcon = weatherObject.getString("icon");
                    String location = json.getString("name");

                    String temperatureText = String.format("%.1f°C", temperatureCelsius);
                    String humidityText = "Humidity: " + String.format("%.1f%%", humidity);
                    String windText = "Wind: " + String.format("%.1f km/h", windSpeed);
                    String temperatureMinText = "Min: " + String.format("%.1f°C", temperatureMinC);
                    String temperatureMaxText = "Max: " + String.format("%.1f°C", temperatureMaxC);

                    Glide.with(WeatherActivity.this)
                            .load("http://openweathermap.org/img/w/" + weatherIcon + ".png")
                            .into(weatherIconImageView);

                    locationTextView.setText(location);
                    temperatureTextView.setText(temperatureText);
                    weatherDescriptionTextView.setText(weatherDescription);
                    humidityTextView.setText(humidityText);
                    windTextView.setText(windText);
                    temperatureMinTextView.setText(temperatureMinText);
                    temperatureMaxTextView.setText(temperatureMaxText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(WeatherActivity.this, "날씨 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
