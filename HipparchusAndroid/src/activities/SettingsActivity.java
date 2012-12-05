package activities;

import gr.mandim.R;
import orchestration.Orchestrator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import bluetooth.BluetoothService;
import calculations.AngleConverter;

public class SettingsActivity extends Activity {

	private static final String TAG = "Settings";
	private static final int REQUEST_ENABLE_BT = 3;
	public static final int MESSAGE_STATE_CONNECTED = 1;
	public static final int CONNECTED_TOAST = 2;
	public static final int CONNECTED_FAILED = 3;
	public static final int CONNECTED_LOST = 4;
	public static final String TOAST = "toast";
	public static final int MESSAGE_STATE_CHANGE = 5;
	public static final int COORDINATES_DIALOG = 6;

	public boolean mIsBound = false;

	private BluetoothAdapter mBluetoothAdapter;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private AngleConverter ac;

	private TextView latitudeDegree;
	private TextView latitudeMinutes;
	private TextView latitudeSeconds;
	private TextView longitudeDegree;
	private TextView longitudeMinutes;
	private TextView longitudeSeconds;
	private TextView connectedNotification;
	
	private Button connectWithTelescope;
	private Button twoStarAlignment;

	public ProgressBar connectedBar;
	public ProgressBar updateLocationBar;
	
	//private Orchestrator orc;
	protected boolean timeScrolled = false;
	protected boolean timeChanged = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		
		Orchestrator.calcInitialTime();
		ac = new AngleConverter();
		Orchestrator.setmHandler(mHandler);

		connectedNotification = (TextView)findViewById(R.id.connectedNotification);		
		connectedBar = (ProgressBar)findViewById(R.id.connectProgressBar);		
		
		updateLocationBar = (ProgressBar)findViewById(R.id.updateLocationProgress);
		updateLocationBar.setIndeterminate(true);
		
		latitudeDegree = (TextView) findViewById(R.id.latitudeDegreeField);
		latitudeMinutes = (TextView) findViewById(R.id.latitudeMinField);
		latitudeSeconds = (TextView) findViewById(R.id.latitudeSecondsField);
		longitudeDegree = (TextView) findViewById(R.id.longitudeDegreeField);
		longitudeMinutes = (TextView) findViewById(R.id.longitudeMinField);
		longitudeSeconds = (TextView) findViewById(R.id.longitudeSecField);

		Button locationBtn = (Button) findViewById(R.id.locationBtn);
		locationBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				locationListener = new myLocation();
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, locationListener);
				updateLocationBar.setVisibility(View.VISIBLE);

			}
		});		
		connectWithTelescope = (Button) findViewById(R.id.connectWithTelescope);
		connectWithTelescope.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Orchestrator.connectWithTelescope();
			}
		});
		
		twoStarAlignment = (Button) findViewById(R.id.bt_twoStar);
		twoStarAlignment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Orchestrator.getBtService() != null && Orchestrator.getBtService().getState() == BluetoothService.STATE_CONNECTED) {
					Intent firstStarAlignment = new Intent(getApplicationContext(), FirstStarAlignmentActivity.class);
					startActivity(firstStarAlignment);
				}
				else {
					showToast("The mount is not connected!", Toast.LENGTH_LONG);
				}
				
			}
		});
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			showToast("Bluetooth is not available for this device!",
					Toast.LENGTH_LONG);
			finish();
			return;
		}
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
		if (Orchestrator.getBtService().getState() == BluetoothService.STATE_CONNECTED){
			Orchestrator.disconnect();
		}
		if (mBluetoothAdapter.isEnabled()) {			
			mBluetoothAdapter.disable();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				Log.i(TAG, "Bluetooth enabled");
			} else {
				Log.d(TAG, "BT not enabled");
				showToast("Unable to open Bluetooth!", Toast.LENGTH_SHORT);
			}
			break;
		}
	}
	
	// The Handler that gets information back from the BluetoothService	
	private Handler mHandler = new Handler() {		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:
					showToast("Connected with mount",Toast.LENGTH_LONG);
					connectedBar.setVisibility(View.INVISIBLE);
					connectedNotification.setText("Connected");
					connectedNotification.setBackgroundColor(Color.parseColor("#DC2D2D"));
					connectedNotification.setTextColor(Color.BLACK);
					connectWithTelescope.setEnabled(false);
					connectWithTelescope.setBackgroundColor(Color.parseColor("#892121"));
					connectWithTelescope.setTextColor(Color.parseColor("#DC2D2D"));
					break;
				case BluetoothService.STATE_CONNECTING:
					connectedBar.setVisibility(View.VISIBLE);
					connectedNotification.setText("Connecting...");
					break;
				case BluetoothService.STATE_LISTEN:
					break;
				case BluetoothService.STATE_NONE:
					break;
				case BluetoothService.STATE_DISCONNECTED:
					showToast("Disconnected from mount", Toast.LENGTH_SHORT);
					connectedBar.setVisibility(View.INVISIBLE);
					connectedNotification.setText("Disconnected!");
					connectedNotification.setBackgroundColor(Color.BLACK);
					connectedNotification.setTextColor(Color.parseColor("#DC2D2D"));
					break;
				}
				break;

			case CONNECTED_FAILED:
				showToast("Failure connection", Toast.LENGTH_SHORT);
				connectedBar.setVisibility(View.INVISIBLE);
				connectedNotification.setText("Connection failed");
				break;
			}
		}
	};

	public void showToast(String message, int duration) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.red_toast,
				(ViewGroup) findViewById(R.id.toast_layout_root));
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(message);
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(duration);
		toast.setView(layout);
		toast.show();
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
				ac.setDegreeDecimal(location.getLatitude());				
				ac.convertToDegMinSec();
				latitudeDegree.setText(String.valueOf(ac.getDeg()));
				latitudeMinutes.setText(String.valueOf(ac.getMin()));
				latitudeSeconds.setText(String.valueOf(ac.getSec()));
				
				ac.setDegreeDecimal(location.getLongitude());				
				ac.convertToDegMinSec();
				longitudeDegree.setText(String.valueOf(ac.getDeg()));
				longitudeMinutes.setText(String.valueOf(ac.getMin()));
				longitudeSeconds.setText(String.valueOf(ac.getSec()));
				
				Orchestrator.setLatitude(location.getLatitude());
				Orchestrator.setLongitude(location.getLongitude());
				updateLocationBar.setVisibility(View.INVISIBLE);
				showToast("Location Updated", Toast.LENGTH_SHORT);
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
