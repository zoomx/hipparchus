package activities;

import orchestration.Orchestrator;
import gr.mandim.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	// Debugging
	private static final String TAG = "SettingsActivity";
	private static final boolean D = true;

	// Message types sent from the BluetoothService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_UNABLE_TO_CONNECT = 5;
	protected static final int MESSAGE_LOCATION = 6;

	// Key names received from the BluetoothService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	public static String ARDUINO_MESSAGE;

	private LocationManager locationManager;
	private LocationListener locationListener;

	private EditText latitudeText;
	private EditText longitudeText;

	private Orchestrator orc;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		orc = (Orchestrator) this.getApplicationContext();

		latitudeText = (EditText) findViewById(R.id.latitudeField);
		longitudeText = (EditText) findViewById(R.id.longitudeField);

		Button locationBtn = (Button) findViewById(R.id.locationBtn);
		locationBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				locationListener = new myLocation();
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, locationListener);

			}
		});

		Button locationSave = (Button) findViewById(R.id.locationSave);
		locationSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (latitudeText.getText().length() == 0
						|| longitudeText.getText().length() == 0) {
					Toast.makeText(SettingsActivity.this,
							"Error: Location Not Set", Toast.LENGTH_LONG)
							.show();
				} else {
					orc.setLatitude(Double.parseDouble(latitudeText.getText()
							.toString()));
					orc.setLongitude(Double.parseDouble(longitudeText.getText()
							.toString()));
					Toast.makeText(SettingsActivity.this, "Location Saved",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (D)
			Log.i(TAG, "++ ON START ++");
		orc.connectToBT();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (D)
			Log.i(TAG, "++ ON DESTROY ++");
		/*
		 * Stop the Bluetooth chat services if (mService != null)
		 * mService.stop(); mBluetoothAdapter.disable();
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.reconnect:
			orc.connectToBT();
			return true;
		case R.id.starAlignment:
			// Launch TwoStarAlignment activity
			Intent twoStarAlignment = new Intent(this,
					TwoStarAlignmentActivity.class);
			startActivity(twoStarAlignment);
			return true;
		}
		return false;
	}

	public class myLocation implements LocationListener {

		public myLocation() {
			super();
		}

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				Log.d("LOCATION CHANGED", location.getLatitude() + "");
				Log.d("LOCATION CHANGED", location.getLongitude() + "");
				latitudeText.setText(String.valueOf(location.getLatitude()));
				longitudeText.setText(String.valueOf(location.getLongitude()));
			}
			locationManager.removeUpdates(locationListener);
		}

		@Override
		public void onProviderDisabled(String arg0) {
		}

		@Override
		public void onProviderEnabled(String arg0) {
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		}
	}
}
