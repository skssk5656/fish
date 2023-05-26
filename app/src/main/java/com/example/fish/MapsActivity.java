package com.example.fish;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private boolean createMarker = false;
    private Button buttonCreateMarker;
    private Button buttonSearch;  // 추가한 코드
    private Button buttonViewMarkers;  // 추가한 코드
    private SharedPreferences sharedPref;
    private static final String IMAGE_FILE_NAME = "selected_image.png";
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Marker selectedMarker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        buttonCreateMarker = findViewById(R.id.buttonCreateMarker);
        buttonCreateMarker.setBackgroundColor(Color.GRAY);
        buttonCreateMarker.setOnClickListener(v -> {
            createMarker = !createMarker;
            buttonCreateMarker.setBackgroundColor(createMarker ? Color.GREEN : Color.GRAY);
        });

        Button buttonSearch = findViewById(R.id.button_Search);
        buttonSearch.setOnClickListener(v -> {
            EditText searchInput = findViewById(R.id.searchInput);
            String keyword = searchInput.getText().toString();

            if (keyword.isEmpty()) {
                // 키워드가 입력되지 않았을 경우 아무 동작하지 않음
                return;
            }

            // 맵에서 모든 마커를 제거
            mMap.clear();

            String json = null;
            try {
                InputStream is = getAssets().open("fish.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                int bytesRead = is.read(buffer, 0, size);
                is.close();
                if (bytesRead != -1) {
                    json = new String(buffer, StandardCharsets.UTF_8);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (json != null) {
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("장소명");
                        String address = jsonObject.getString("주소");

                        // 검색 키워드로 장소명 또는 주소가 포함되어 있는지 확인
                        if (name.contains(keyword) || address.contains(keyword)) {
                            double lat = jsonObject.getDouble("위도");
                            double lng = jsonObject.getDouble("경도");
                            LatLng location = new LatLng(lat, lng);
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(name)
                                    .snippet(address));

                            if (marker != null) {
                                String note = sharedPref.getString(marker.getId(), "");
                                marker.setTag(note);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        buttonViewMarkers = findViewById(R.id.buttonViewInfo);
        buttonViewMarkers.setOnClickListener(v -> {
            // TODO: Adjust the map's zoom level and position to include all markers
        });

        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(v -> openGallery());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setRotateGesturesEnabled(false);

        LatLng seoul = new LatLng(37.5665, 126.9780);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15));

        String json = null;
        try {
            InputStream is = getAssets().open("fish.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            int bytesRead = is.read(buffer, 0, size);
            is.close();
            if (bytesRead != -1) {
                json = new String(buffer, StandardCharsets.UTF_8);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("장소명");
                    String address = jsonObject.getString("주소");
                    double lat = jsonObject.getDouble("위도");
                    double lng = jsonObject.getDouble("경도");
                    LatLng location = new LatLng(lat, lng);
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(name)
                            .snippet(address));

                    if (marker != null) {

                        String note = sharedPref.getString(marker.getId(), "");
                        marker.setTag(note);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mMap.setOnMapClickListener(latLng -> {
            if (createMarker) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("클릭한 위치");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)); // 추가한 코드
                Marker marker = mMap.addMarker(markerOptions);
                if (marker != null) {
                    MarkerItem markerItem = new MarkerItem(marker.getId(), latLng, "클릭한 위치", "", null);
                    marker.setTag(markerItem);
                }
                createMarker = false;
                buttonCreateMarker.setBackgroundColor(Color.GRAY);
            }
        });

        mMap.setOnMarkerClickListener(marker -> {
            selectedMarker = marker;  // 추가한 코드

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MapsActivity.this);
            View bottomSheetView = getLayoutInflater().inflate(R.layout.memo, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            final EditText noteInput = bottomSheetView.findViewById(R.id.noteInput);
            noteInput.setText((String) marker.getTag());

            imageView = bottomSheetView.findViewById(R.id.imageView);
            Button selectImageButton = bottomSheetView.findViewById(R.id.selectImageButton);

            try {
                File imageFile = new File(getFilesDir(), marker.getId() + "_" + IMAGE_FILE_NAME);
                Bitmap image = BitmapFactory.decodeStream(new FileInputStream(imageFile));
                imageView.setImageBitmap(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            selectImageButton.setOnClickListener(v -> openGallery());

            Button saveButton = bottomSheetView.findViewById(R.id.saveButton);
            saveButton.setOnClickListener(v -> {
                String note = noteInput.getText().toString();
                marker.setTag(note);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(marker.getId(), note);
                editor.apply();

                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.show();

            return false;
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // 이미지 선택 후 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(selectedImage);
                saveImage(selectedImage, selectedMarker.getId() + "_" + IMAGE_FILE_NAME);  // 수정한 코드
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImage(Bitmap image, String fileName) {
        try {
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
