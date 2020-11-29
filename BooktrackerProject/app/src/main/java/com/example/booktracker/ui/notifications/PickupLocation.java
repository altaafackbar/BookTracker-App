/**
 * PickupLocation
 * Activity that shows the pickup location chosen by the owner
 * of a book for a requester
 */
package com.example.booktracker.ui.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.booktracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PickupLocation extends AppCompatActivity
        implements OnMapReadyCallback {
    private Double pickupLat;
    private Double pickupLon;
    // [START_EXCLUDE]
    // [START maps_marker_get_map_async]
    @Override

    /**
     * Set the view to be the map where user can view the location
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_pickup_location);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null)
        {
            pickupLat = Double.valueOf((String) bundle.get("pickupLat"));
            pickupLon =Double.valueOf((String) bundle.get("pickupLon"));
        }
    }
    // [END maps_marker_get_map_async]
    // [END_EXCLUDE]

    // [START_EXCLUDE silent]
    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    // [END_EXCLUDE]
    // [START maps_marker_on_map_ready_add_marker]
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // [START_EXCLUDE silent]
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        // [END_EXCLUDE]
        Log.d("TAG", "lat: "+pickupLat+" lon: "+pickupLon );
        LatLng pickupLocation = new LatLng(pickupLat, pickupLon);
        googleMap.addMarker(new MarkerOptions()
                .position(pickupLocation)
                .title("Book Pickup Location"));
        // [START_EXCLUDE silent]
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pickupLocation));
        float zoomLevel = 16.0f; //This goes up to 21
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLocation, zoomLevel));
        // [END_EXCLUDE]
    }
    // [END maps_marker_on_map_ready_add_marker]
}