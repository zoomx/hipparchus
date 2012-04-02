package activities;

import orchestration.Orchestrator;
import gr.mandim.R;
import bluetooth.BluetoothService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private static final String TAG = "HIPPARCHUS";
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

	// Name of the connected device
	private String mConnectedDeviceName;

	// Intent request codes
	private static final int REQUEST_ENABLE_BT = 3;

	// The FireFly mac address (Sparkfun Bluesmirf gold module)
	private static final String MAC_ADDRESS = "00:06:66:04:DB:38";
	private BluetoothService mService;
	private BluetoothAdapter mBluetoothAdapter;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private ProgressDialog dialog;

	private EditText latitudeText;
	private EditText longitudeText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		final Orchestrator orc = (Orchestrator)this.getApplicationContext();
		// Check bt availability. If no bt available close the application
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

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
				if (latitudeText.getText().length() == 0 || longitudeText.getText().length() == 0){
					Toast.makeText(SettingsActivity.this, "Error: Location Not Set", Toast.LENGTH_LONG).show();
				}
				else{
					orc.setLatitude(Double.parseDouble(latitudeText.getText().toString()));
					orc.setLongitude(Double.parseDouble(longitudeText.getText().toString()));
					Toast.makeText(SettingsActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
		// Stop the Bluetooth chat services
		if (mService != null)
			mService.stop();
		mBluetoothAdapter.disable();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Initialize the BluetoothChatService to perform bluetooth connections
				mService = new BluetoothService(this, mHandler);
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(MAC_ADDRESS);
				dialog = ProgressDialog.show(this, "",
						"Connecting with \nthe Telescope...", true);
				mService.connect(device);
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
			Intent twoStarAlignment = new Intent(this, TwoStarAlignmentActivity.class);			
			startActivity(twoStarAlignment);
			return true;
		}
		return false;
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:
					break;
				case BluetoothService.STATE_CONNECTING:
					break;
				case BluetoothService.STATE_LISTEN:
				case BluetoothService.STATE_NONE:
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				// mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				// mConversationArrayAdapter.add(mConnectedDeviceName+":  " +
				// readMessage);
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				dialog.dismiss();
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_UNABLE_TO_CONNECT:
				dialog.dismiss();
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;

			}
		}
	};

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
