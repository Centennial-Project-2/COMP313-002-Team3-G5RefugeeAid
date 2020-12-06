package com.comp313002.team3.g5refugeeaid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.security.KeyStore;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    //constants
    public static final String TAG = "map_activity";
    public static final int PERMISSION_REQUEST_CODE = 9001;
    public static final int PLAY_SERVICES_ERROR_CODE = 9002;
    public static final int GPS_REQUEST_CODE = 9003;
    private static final double TORONTO_LAT = 43.6535550755963;
    private static final double TORONTO_LNG = -79.3840760739025;

    //boolean properties
    private boolean mLocationPermissionGranted;


    //PROPERTIES
    private EditText mSearch_txt;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //edit text for searching address
        mSearch_txt = findViewById(R.id.search_txt);

        initGoogleMap();
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        supportMapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady: map is showing");
        mGoogleMap = map;

        goToLocation(TORONTO_LAT, TORONTO_LNG);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        mGoogleMap.setBuildingsEnabled(true);
        mGoogleMap.setMinZoomPreference(8);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

    //adding marker option and passing position as LatLang
    private void showMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mGoogleMap.addMarker(markerOptions);
    }

    private void goToLocation(double lat, double lng){
        LatLng latLng = new LatLng( lat, lng);
        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mGoogleMap.animateCamera(cameraUpdate);
        //adding marker
        MarkerOptions markerOptions = new MarkerOptions().title("Toronto").position(new LatLng(lat,lng));
        mGoogleMap.addMarker(markerOptions);
    }

    //permission and services
    private void initGoogleMap() {
        if(isServicesOk()){
            if(checkLocationPermission()){
                checkGpsOnLoad();

                toastMethod("Ready to Map");

            }
            else{
                requestLocationPermission();
            }
        }

    }

    private boolean checkLocationPermission() {

        return ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED;
    }

    private boolean isServicesOk(){
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result =googleApi.isGooglePlayServicesAvailable(this);
        if(result== ConnectionResult.SUCCESS){
            return true;
        }else if(googleApi.isUserResolvableError(result)){
            Dialog dialog = googleApi.getErrorDialog(this, result,PLAY_SERVICES_ERROR_CODE, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface Task) {
                    MapActivity.this.toastMethod("Canceled");
                }
            });
            dialog.show();
        }else{
            toastMethod("Play services are required by this application");

        }

        return false;
    }

    private void requestLocationPermission(){


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private boolean isGPSEnable(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsProviderEnabled= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGpsProviderEnabled){
            Log.d(TAG,"isGPSEnabled Method checked services");
            return true;
        }else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Enable GPS")
                    .setMessage("GPS Required")

                    .setPositiveButton("yes", (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            MapActivity.this.startActivityForResult(intent, GPS_REQUEST_CODE);
                        }
                    })).show();

        }


        return false;
    }

    //MAP PERMISSION OVERRIDE METHODS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
            toastMethod("Permission granted");
        }else{
            toastMethod("Permission not granted");
        }


    }


    protected void checkGpsOnLoad() {
        super.onRestart();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsProviderEnabled= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(isGPSEnable()){
            if(isGPSEnable()){
                toastMethod("GPS is enabled");
            }else{
                toastMethod("GpS is not Enabled");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsProviderEnabled= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(isGPSEnable()){
            if(isGPSEnable()){
                toastMethod("GPS is enabled");
            }else{
                toastMethod("GpS is not Enabled");
            }
        }
    }

    //adding menu for map types

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_type, menu);
        return true;
    }

    //create a boundary
    public void boundTo(double lat, double lng){
        // USER LAT AND LNG AND CREATING -0.2 AND +0.2 WILL CREATE A BOUNDARY
        double bottomBound = lat -0.2,  leftBound =lng -0.2,
                rightBound = lat + 0.2,  topBound = lng +0.2;

        LatLngBounds myLocationBounds = new LatLngBounds(
                new LatLng(bottomBound, leftBound),
                new LatLng(topBound, rightBound)
        );
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(myLocationBounds,1));
        showMarker(myLocationBounds.getCenter());
    }


    //HIDE SOFT KEYBOARD
    private void hidSoftKeyboard(View v){
        InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.maptype_Default:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Toast.makeText(this,"RoadMap",Toast.LENGTH_SHORT).show();
                break;
            case R.id.maptype_satellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Toast.makeText(this,"RoadMap",Toast.LENGTH_SHORT).show();
                break;
            case R.id.maptype_hybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                Toast.makeText(this,"RoadMap",Toast.LENGTH_SHORT).show();
                break;
            case R.id.maptype_terrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                Toast.makeText(this,"RoadMap",Toast.LENGTH_SHORT).show();
                break;

            case R.id.maptype_none:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                Toast.makeText(this,"RoadMap",Toast.LENGTH_SHORT).show();
                break;
            case R.id.map_toronto:
                gotToToronto();
        }
        return super.onOptionsItemSelected(item);
    }

    //GETTING GEOLOCATION OF SEARCH
    public void getGeoLocator(View v) {
        hidSoftKeyboard(v);
        //getting user's input
        String locationName = mSearch_txt.getText().toString();
        //creating instance of geoCode
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            //create a list of address

            List<Address> addresses= geocoder.getFromLocationName(locationName, 3);
            //to get retrieve address from lag and lng the rest of methods and code for both methoods .getFromLocation and getFromLocationName
            //List<Address> addresses = geocoder.getFromLocation(TORONTO_LAT+0.4 , TORONTO_LNG+0.4,2);
            //add the first take the first address from the lest
            if(addresses.size()>0){
                Address address = addresses.get(0);
                //convert to lan and lat
                goToLocation(address.getLatitude(),address.getLongitude());
                //add marker
                mGoogleMap.addMarker(
                        new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())).
                                title(address.getAddressLine(address.getMaxAddressLineIndex()))
                );
                Log.d(TAG,"geoLocate: Locality: " + address.getLocality());
                Toast.makeText(this, ""+ address.getLocality(),Toast.LENGTH_SHORT).show();
            }
            for (Address address:addresses){
                Log.d(TAG, "geoLocate: Address: "+ address.getAddressLine(address.getMaxAddressLineIndex()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // go to a location(Toronto)
    public void gotToToronto(){
        if(mGoogleMap!=null){
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(3.9f));

            double bottomB = TORONTO_LAT-0.02, leftB = TORONTO_LNG-0.02;
            double topB = TORONTO_LAT+ 0.04, rightB= TORONTO_LNG + 0.04;
            LatLngBounds DOWN_TOWN = new LatLngBounds(
                    new LatLng(bottomB, leftB),
                    new LatLng(topB, rightB));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(DOWN_TOWN, 1), 5000,
                    new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }

                        @Override
                        public void onCancel() {
                            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        }
                    });

            showMarker(DOWN_TOWN.getCenter());


        }

    }

    public void toastMethod (String str){
        Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();
    }





}

