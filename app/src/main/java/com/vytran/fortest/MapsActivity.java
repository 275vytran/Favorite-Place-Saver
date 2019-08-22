package com.vytran.fortest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//https://console.developers.google.com/apis/credentials?project=eminent-expanse-243322
//command: keytool -list -v -alias 275vytran -keystore "C:\Program Files\Java\jdk1.8.0_192\bin\androidkey.jks" -storepass oknhuvaydi -keypass oknhuvaydi
//Latitude: 30.0406
//Longitude: -94.0731

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    Double myLatitude = 0.0;
    Double myLongitude = 0.0;
    String myAddress;
    String locationName;
    String locationType;
    String userComment;



    //Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_place, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_place) {
            Intent intent = new Intent(MapsActivity.this, UploadActivity.class);
            intent.putExtra("latitude", myLatitude.toString());
            intent.putExtra("longitude", myLongitude.toString());
            intent.putExtra("locationName", locationName);
            intent.putExtra("locationType", locationType);
            intent.putExtra("userComment", userComment);

            if (myAddress != null)
                intent.putExtra("address", myAddress);
            else
                intent.putExtra("address", "Unknown");

            if (myLatitude == 0.0 || myLongitude == 0.0) {
                Toast.makeText(MapsActivity.this, "Long click on map to choose location", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(MapsActivity.this, "Location is saved", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        }

        if (item.getItemId() == R.id.cancel_place) {
            Intent intent = new Intent(MapsActivity.this, UploadActivity.class);
            intent.putExtra("locationName", locationName);
            intent.putExtra("locationType", locationType);
            intent.putExtra("userComment", userComment);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Set up long click listener
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                Intent intent = getIntent();
                locationName = intent.getStringExtra("location_name");
                locationType = intent.getStringExtra("location_type");
                userComment = intent.getStringExtra("user_comment");
                mMap.addMarker(new MarkerOptions().position(latLng).title(locationName));

                //Get full address
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    String fullAddress = addresses.get(0).getAddressLine(0);
                    myAddress = fullAddress;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(MapsActivity.this, myAddress, Toast.LENGTH_LONG).show();

                myLatitude = latLng.latitude;
                myLongitude = latLng.longitude;

            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17f));
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //Check permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 2f, locationListener);
            mMap.clear();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            /*Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 17f));
                mMap.addMarker(new MarkerOptions().position(lastUserLocation).title("Current Location"));

            }*/
        }

    }

    //Check request code
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 2f, locationListener);
                mMap.clear();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                /*Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 17f));
                    mMap.addMarker(new MarkerOptions().position(lastUserLocation).title("Current Location"));
                }*/
            }
            else {
                Intent intent = new Intent(MapsActivity.this, UploadActivity.class);
                startActivity(intent);
                Toast.makeText(MapsActivity.this, "Please grant permission to continue", Toast.LENGTH_LONG).show();
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
