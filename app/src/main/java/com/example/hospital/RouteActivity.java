package com.example.hospital;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    LocationRequest locationRequest;

    GoogleMap gmap;
    LatLng endLatlang = new LatLng(9.672759,77.965341);
    View mapView;
    Polyline routePolyline;
    private TextToSpeech textToSpeech;
    SupportMapFragment supportMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
        assert supportMapFragment != null;
        mapView = supportMapFragment.getView();
        supportMapFragment.getMapAsync(this);
        CheckGps();
        checkInternetConnection();

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language is not supported.");
                }
            } else {
                Log.e("TTS", "Initialization failed.");
            }
        });

    }


    private void CheckGps() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addAllLocationRequests(Collections.singleton(locationRequest)).setAlwaysShow(true);
        Task<LocationSettingsResponse> locationSettingsRequestTask = LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());
        locationSettingsRequestTask.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
            } catch (ApiException e) {
                if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(RouteActivity.this, 101);
                    } catch (IntentSender.SendIntentException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                if (e.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                    Toast.makeText(RouteActivity.this, "Settings Not Availabe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(RouteActivity.this, "Gps Enabled", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(RouteActivity.this, "Denied Gps", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void GetLocationUpdate() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (provider != null) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                LatLng latLng = new LatLng(lat, lng);
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.permision);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 120, 120, false);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location");
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)      // Sets the center of the map to Mountain View
                        .zoom(18)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                gmap.addMarker(markerOptions);
            }
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmap = googleMap;
        gmap.getUiSettings().setAllGesturesEnabled(true);
        gmap.getUiSettings().setCompassEnabled(false);
        gmap.getUiSettings().setZoomGesturesEnabled(true);
        gmap.getUiSettings().setScrollGesturesEnabled(true);
        gmap.getUiSettings().setRotateGesturesEnabled(true);
        gmap.getUiSettings().setTiltGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }// Adjust this value as needed
        gmap.setMyLocationEnabled(true);
        gmap.setPadding(0, 200, 50, 0);
        gmap.setOnMyLocationButtonClickListener(() -> {
            gmap.clear();
            checkInternetConnection();
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(3000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addAllLocationRequests(Collections.singleton(locationRequest)).setAlwaysShow(true);
            Task<LocationSettingsResponse> locationSettingsRequestTask = LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());
            locationSettingsRequestTask.addOnCompleteListener(task -> {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    Criteria criteria = new Criteria();

                    String provider = locationManager.getBestProvider(criteria, true);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(provider);
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        LatLng latLng = new LatLng(lat, lng);
                        drawRoute(latLng, endLatlang);
                        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.permision);
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 120, 120, false);
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location");
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)      // Sets the center of the map to Mountain View
                                .zoom(18)                   // Sets the zoom
                                .bearing(90)                // Sets the orientation of the camera to east
                                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        gmap.addMarker(markerOptions);
                    }
                } catch (ApiException e) {
                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult(RouteActivity.this, 101);
                        } catch (IntentSender.SendIntentException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    if (e.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        Toast.makeText(RouteActivity.this, "Settings Not Availabe", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return false;
        });
        GetLocationUpdate();
    }

    //check internet on or not
    private void checkInternetConnection() {
        if (!NetworkUtils.isInternetConnected(this)) {
            showNoInternetAlertDialog();
        } else if (NetworkUtils.isLowNetwork(this)) {
            showLowNetworkAlertDialog();
        }
    }

    private void showNoInternetAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please check your internet connection and try again.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(RouteActivity.this, LanguageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showLowNetworkAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Low Network Connection");
        builder.setMessage("You are currently on a low network connection. Some features may be limited.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(RouteActivity.this, LanguageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void drawRoute(LatLng startLocation, LatLng endLocation) {
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyCBd9MiKrWxkR0Sau2pUsMeKJ5ncvkUXFk") // Replace with your API key
                .build();

        DirectionsApiRequest directionsRequest = DirectionsApi.newRequest(geoApiContext)
                .origin(new com.google.maps.model.LatLng(startLocation.latitude, startLocation.longitude))
                .destination(new com.google.maps.model.LatLng(endLocation.latitude, endLocation.longitude))
                .mode(TravelMode.DRIVING) // Use "Driving" mode for bus routes
                .units(Unit.METRIC);

        directionsRequest.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                if (result != null && result.routes != null && result.routes.length > 0) {
                    DirectionsRoute route = result.routes[0];

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Draw the route on the map
                            PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.color(Color.BLUE)
                                    .width(17f);

                            for (DirectionsStep step : route.legs[0].steps) {
                                List<com.google.maps.model.LatLng> polylinePoints = step.polyline.decodePath();
                                for (com.google.maps.model.LatLng point : polylinePoints) {
                                    LatLng latLng = new LatLng(point.lat, point.lng);
                                    polylineOptions.add(latLng);
                                    String instruction = step.htmlInstructions;
                                    speakDirections(instruction);
                                }
                            }

                            routePolyline = gmap.addPolyline(polylineOptions);

                            // Move the camera to show the entire route
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(startLocation);
                            builder.include(endLocation);
                            LatLngBounds bounds = builder.build();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable e) {
                // Handle the failure case
            }
        });
    }

    @SuppressLint("NewApi")
    private void speakDirections(String instructions) {
        if (textToSpeech != null) {
            textToSpeech.speak(instructions, TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release Text-to-Speech resources
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @SuppressLint("MissingSuperCall")
    public void onBackPressed() {
        Intent intent = new Intent(this, LanguageActivity.class);
        startActivity(intent);
        finish();
    }
}

