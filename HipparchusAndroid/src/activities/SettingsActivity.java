package activities;

import gr.mandim.R;
import orchestration.Orchestrator;
import android.app.Activity;
import android.app.Dialog;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import bluetooth.BluetoothService;
import calculations.AngleConverter;

public class SettingsActivity extends Activity {

	private static final String TAG = "SettingsActivity";
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

	public ProgressDialog dialog;
	public Dialog coordinatesDialog;

	private Orchestrator orc;
	protected boolean timeScrolled = false;
	protected boolean timeChanged = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);

		orc = new Orchestrator();
		ac = new AngleConverter();
		orc.setmHandler(mHandler);
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

			}
		});		
		Button connectWithTelescope = (Button) findViewById(R.id.connectWithTelescope);
		connectWithTelescope.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				orc.connectWithTelescope();
			}
		});
		
		// Check bt availability. If no bt available close the application
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
		// TODO: disconnect first if connections has been established
		// orc.disconnect();
		if (mBluetoothAdapter.isEnabled()) {
			orc.disconnect();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.starAlignment:

			Intent firstStarAlignment = new Intent(this,
					FirstStarAlignmentActivity.class);
			startActivity(firstStarAlignment);
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
					showToast("Connected with Hipparchus mount",
							Toast.LENGTH_LONG);
					break;
				case BluetoothService.STATE_CONNECTING:
					dialog = new ProgressDialog(SettingsActivity.this, R.style.myDialog);
					//dialog = ProgressDialog.show(SettingsActivity.this, "","Connecting. Please wait...", true);
					dialog.setMessage("Connecting...");
					dialog.show();
					break;
				case BluetoothService.STATE_LISTEN:
				case BluetoothService.STATE_NONE:

					break;

				case BluetoothService.STATE_DISCONNECTED:
					showToast("Disconnected from mount", Toast.LENGTH_LONG);
					break;
				}
				break;

			case CONNECTED_FAILED:
				dialog.dismiss();
				showToast("Failure connection", Toast.LENGTH_LONG);
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
				
				orc.setLatitude(location.getLatitude());
				orc.setLongitude(location.getLongitude());
				
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
