package com.example.myapplication0412;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication0412.Personal.Personal;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapMainActivity extends AppCompatActivity {

    private String destinationAddress; // 儲存Firebase中取得的地址

    private FusedLocationProviderClient fusedLocationClient;

    String latitudeOne = "22.6581";  // 指定的緯度
    String longititudeOne = "120.5115";  // 指定的經度
    String latitudeTwo = "22.6581";  // 另一個位置的緯度
    String longititudeTwo = "120.5115";  // 另一個位置的經度
    double currentLatitude;
    double currentLongitude;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapmain);

        MaterialButton pinLocationBtn = findViewById(R.id.pinLocationBtn);
        MaterialButton directionOneBtn = findViewById(R.id.directionOneBtn);
        MaterialButton directionTwoBtn = findViewById(R.id.directionTwoBtn);

        // 初始化 FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 取得使用者填寫的地址
        fetchUserAddressFromFirebase();

        // 請求位置權限並取得當前位置
        requestLocationPermission();

        pinLocationBtn.setOnClickListener(v -> pinLocationMap(latitudeOne, longititudeOne));

        directionOneBtn.setOnClickListener(v -> {
            if (currentLatitude != 0 && currentLongitude != 0) {
                directionFromCurrentMap(latitudeOne, longititudeOne);
            } else {
                Toast.makeText(MapMainActivity.this, "無法取得當前位置", Toast.LENGTH_SHORT).show();
            }
        });

        directionTwoBtn.setOnClickListener(v -> directionBetweenTwoMap(latitudeOne, longititudeOne, latitudeTwo, longititudeTwo));
    }

    private void fetchUserAddressFromFirebase() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").orderByKey().limitToLast(1)  // 假設要取得最新填寫的地址
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Personal user = snapshot.getValue(Personal.class);
                            if (user != null) {
                                destinationAddress = user.getAddress();  // 獲取地址
                                Toast.makeText(MapMainActivity.this, "地址已載入: " + destinationAddress, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MapMainActivity.this, "無法載入地址", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 取得當前位置
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                Toast.makeText(MapMainActivity.this, "當前位置：" + currentLatitude + ", " + currentLongitude, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapMainActivity.this, "無法取得當前位置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 請求位置權限
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 如果權限已經被授予，直接取得位置
            getCurrentLocation();
        }
    }

    // 處理權限請求結果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 如果權限授予，取得位置
                getCurrentLocation();
            } else {
                Toast.makeText(this, "位置權限被拒絕", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 釘選位置地圖
    private void pinLocationMap(String latitude, String longitude) {
        Uri mapUri = Uri.parse("https://maps.google.com/maps/search/" + latitude + "," + longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(intent);
    }

    // 從當前位置導航到指定位置
    private void directionFromCurrentMap(String destinationLatitude, String destinationLongitude) {
        Uri mapUri = Uri.parse("https://maps.google.com/maps?saddr=" + currentLatitude + "," + currentLongitude + "&daddr=" + destinationAddress);
        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(intent);
    }

    // 兩個位置之間導航
    private void directionBetweenTwoMap(String sourceLatitude, String sourceLongitude, String destinationLatitude, String destinationLongitude) {
        Uri mapUri = Uri.parse("https://maps.google.com/maps?saddr=" + sourceLatitude + "," + sourceLongitude + "&daddr=" + destinationLatitude + "," + destinationLongitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(intent);
    }
}
