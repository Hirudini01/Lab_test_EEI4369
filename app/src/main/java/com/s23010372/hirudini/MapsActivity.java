package com.s23010372.hirudini;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText etAddress;
    private Button btnShowLocation, btnNextScreen;
    private static final float DEFAULT_ZOOM = 15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize UI components
        etAddress = findViewById(R.id.etAddress);
        btnShowLocation = findViewById(R.id.btnShowLocation);
        btnNextScreen = findViewById(R.id.btnNextScreen);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set up button click listeners
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();
            }
        });

        btnNextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Sensor Activity
                Intent intent = new Intent(MapsActivity.this, SensorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set a default location (e.g., university campus)
        LatLng defaultLocation = new LatLng(6.9271, 79.8612); // Example: Colombo
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
    }

    private void geoLocate() {
        String addressText = etAddress.getText().toString();

        if (addressText.isEmpty()) {
            Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(addressText, 1);
        } catch (IOException e) {
            Toast.makeText(this, "Geocoding error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(this, "No location found for the address", Toast.LENGTH_SHORT).show();
            return;
        }

        Address address = addresses.get(0);
        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

        // Add marker and move camera
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(location).title(addressText));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));

        Toast.makeText(this, "Location found: " + address.getAddressLine(0), Toast.LENGTH_SHORT).show();
    }
}