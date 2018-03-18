package com.ar.sphinx.gmap;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";
	private static final int ERROR_DIALOG_REQUEST = 9001;
	private Button gotoMapBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(isServicesOk()){
			Toast.makeText(this,"Services Ok",Toast.LENGTH_SHORT).show();
			init();
		}
	}

	private void init() {
		gotoMapBtn =  findViewById(R.id.btn_gotomap);
		gotoMapBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { //method is the number
				Intent intent = new Intent(MainActivity.this,MapActivity.class);
				startActivity(intent);
			}
		});

	}

	public boolean isServicesOk(){
		Log.d(TAG, "isServicesOk: checking google services version ");
		int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
		if(available == ConnectionResult.SUCCESS){
			Log.d(TAG, "isServicesOk: User can make map requests");
			return true;
		}else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
			Log.d(TAG, "isServicesOk: Error can be resolved.");
			Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this,available,ERROR_DIALOG_REQUEST);
			dialog.show();
		}
		else {
			Log.d(TAG, "isServicesOk: You cant make map requests.");
		}
		return false;
	}
}
