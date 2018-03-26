package com.ar.sphinx.gmap;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;

/**
 * Created by sPhinx on 19/03/18.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

	private static final String TAG = "MapActivity";
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
	String FINE_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
	String COARSE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
	private boolean locationPermissionsGranted = false;
	private GoogleMap gMap;
	private FusedLocationProviderClient fusedLocationProviderClient;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		getLocationPermissions();
	}

	public void getCurrentLocation() {
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
		try {
			if(locationPermissionsGranted) {
				Task location = fusedLocationProviderClient.getLastLocation();
				location.addOnCompleteListener(new OnCompleteListener() {
					@Override
					public void onComplete(@NonNull Task task) {
						if(task.isSuccessful()) {
							Location currentLocation = (Location) task.getResult();
							moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f);
						}
					}
				});
			}
		} catch(SecurityException ex) {
			Log.e(TAG, "getCurrentLocation: exception.");
		}
	}

	public void moveCamera(LatLng latLng, float zoom) {
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
	}

	public void initMap() {
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(MapActivity.this);
	}

	public void getLocationPermissions() {
		Log.d(TAG, "getLocationPermissions: running..");
		String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
		if(ContextCompat.checkSelfPermission(getApplicationContext(), FINE_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
			if(ContextCompat.checkSelfPermission(getApplicationContext(), COARSE_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
				locationPermissionsGranted = true;
				Log.d(TAG, "getLocationPermissions: granted permissions :)");
				initMap();
			} else {
				ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
				Log.d(TAG, "getLocationPermissions: requesting permissions :)");
			}
		} else {
			ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
			Log.d(TAG, "getLocationPermissions: requesting permissions :)");
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//		super.onRequestPermissionsResult(requestCode,permissions,grantResults);
		locationPermissionsGranted = false;
		switch(requestCode) {

			case LOCATION_PERMISSION_REQUEST_CODE: {
				if(grantResults.length > 0) {
					for(int grantResult : grantResults) {
						if(grantResult != PackageManager.PERMISSION_GRANTED) {
							locationPermissionsGranted = false;
							return;
						}
					}
					locationPermissionsGranted = true;
					Log.d(TAG, "onRequestPermissionsResult: Permissions granted, initiazling map..");
					//initialize map
					initMap();
				}
			}
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		Toast.makeText(MapActivity.this, "Map is ready guys", Toast.LENGTH_SHORT).show();
		gMap = googleMap;
		if(locationPermissionsGranted) {
			getCurrentLocation();
			if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return;
			}
			gMap.setMyLocationEnabled(true);

		}
	}
}
