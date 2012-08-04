package activities;

import gr.mandim.R;
import orchestration.Orchestrator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import bluetooth.BluetoothService;
import bluetooth.BluetoothService.LocalBinder;

public class SettingsActivity extends Activity {

	private static final String TAG = "SettingsActivity";
	private static final int REQUEST_ENABLE_BT = 3;
	private static final String MAC_ADDRESS = "00:06:66:04:DB:38";
	
	public BluetoothService btService;
	public BluetoothDevice device;
	public boolean mIsBound = false;
	
	private BluetoothAdapter mBluetoothAdapter;
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

		// Check bt availability. If no bt available close the application
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		device = mBluetoothAdapter.getRemoteDevice(MAC_ADDRESS);

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
		Intent intent = new Intent(SettingsActivity.this,
				BluetoothService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Unbind from the service
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				btService.connect(device);
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, "Unable to enable BT", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
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
			// Activate bt connection
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			btService = binder.getService();
			mIsBound = true;
			Log.i(TAG, "Service connected with activity");
			btService.setmAdapter(mBluetoothAdapter);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			btService = null;
			mIsBound = false;
			Log.i(TAG, "Service disconnected from activity");
		}
	};
}
