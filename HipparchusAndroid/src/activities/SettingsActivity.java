package activities;

import bluetooth.BluetoothService;

import gr.mandim.R;
import orchestration.Orchestrator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
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

	private static final String TAG = "SettingsActivity";
	private static final int REQUEST_ENABLE_BT = 3;
	public static final int MESSAGE_STATE_CONNECTED = 1;
	public static final int CONNECTED_TOAST = 2;
	public static final int CONNECTED_FAILED = 3;
	public static final int CONNECTED_LOST = 4;
	public static final String TOAST = "toast";
	public static final int MESSAGE_STATE_CHANGE = 5;
	
	
	public boolean mIsBound = false;
	
	private BluetoothAdapter mBluetoothAdapter;
	private LocationManager locationManager;
	private LocationListener locationListener;

	private EditText latitudeText;
	private EditText longitudeText;
	public static EditText log;
	
	public ProgressDialog dialog;

	private Orchestrator orc;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);

		orc = new Orchestrator();
		orc.setmHandler(mHandler);

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
		log = (EditText)findViewById(R.id.log);

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
		
		Button connectWithTelescope = (Button) findViewById(R.id.connectWithTelescope);
		connectWithTelescope.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				orc.connectWithTelescope();
				
			}
		});
		
		Button disconnectWithTelescope = (Button) findViewById(R.id.disconnectWithTelescope);
		disconnectWithTelescope.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				orc.disconnect();
				
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();		
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		orc.disconnect();
		mBluetoothAdapter.disable();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				//device = mBluetoothAdapter.getRemoteDevice(MAC_ADDRESS);
				Log.i(TAG, "Bluetooth enabled");
			} 
			else {
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, "Unable to enable BT", Toast.LENGTH_SHORT).show();
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
			
			return true;
		case R.id.starAlignment:
			// Launch TwoStarAlignment activity
			Intent twoStarAlignment = new Intent(this, TwoStarAlignmentActivity.class);
			startActivity(twoStarAlignment);
			return true;
		}
		return false;
	}
	
	// The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:                
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                    dialog.dismiss();
                    Toast.makeText(SettingsActivity.this, "Connected with telescope", Toast.LENGTH_LONG).show();                    
                    break;
                case BluetoothService.STATE_CONNECTING:
                	dialog = ProgressDialog.show(SettingsActivity.this, "", "Connecting. Please wait...", true);
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                    
                    break;
                    
                case BluetoothService.STATE_DISCONNECTED:
                	Toast.makeText(SettingsActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
                    break;
                }
                break;
            
            
            case CONNECTED_FAILED:            	
            	dialog.dismiss();
            	Toast.makeText(SettingsActivity.this, "Connection failed", Toast.LENGTH_LONG).show();
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
