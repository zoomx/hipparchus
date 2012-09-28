package activities;

import gr.mandim.R;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import bluetooth.BluetoothService;

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

	private EditText latitudeText;
	private EditText longitudeText;

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
		orc.setmHandler(mHandler);

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

		// Check bt availability. If no bt available close the application
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
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
		// orc.disconnect();
		if (mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.disable();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				// device = mBluetoothAdapter.getRemoteDevice(MAC_ADDRESS);
				Log.i(TAG, "Bluetooth enabled");
			} else {
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

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:
					dialog.dismiss();
					Toast.makeText(SettingsActivity.this,
							"Connected with telescope", Toast.LENGTH_LONG)
							.show();
					break;
				case BluetoothService.STATE_CONNECTING:
					dialog = ProgressDialog.show(SettingsActivity.this, "",
							"Connecting. Please wait...", true);
					break;
				case BluetoothService.STATE_LISTEN:
				case BluetoothService.STATE_NONE:

					break;

				case BluetoothService.STATE_DISCONNECTED:
					Toast.makeText(SettingsActivity.this, "Disconnected",
							Toast.LENGTH_LONG).show();
					break;
				}
				break;

			case CONNECTED_FAILED:
				dialog.dismiss();
				Toast.makeText(SettingsActivity.this, "Connection failed",
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case COORDINATES_DIALOG:

			dialog = new Dialog(this.getApplicationContext());
			dialog.setContentView(R.layout.coordinates_dialog);
			dialog.setTitle("Add Coordinates");

			final WheelView degrees = (WheelView) findViewById(R.id.deg);
			degrees.setViewAdapter(new NumericWheelAdapter(this, 0, 360));

			final WheelView miminutes = (WheelView) findViewById(R.id.min);
			miminutes.setViewAdapter(new NumericWheelAdapter(this, 0, 59,
					"%02d"));

			// add listeners addChangingListener(miminutes, "min");
			addChangingListener(degrees, "hour");

			OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					if (!timeScrolled) {
						timeChanged = true;
						timeChanged = false;
					}
				}
			};
			degrees.addChangingListener(wheelListener);
			miminutes.addChangingListener(wheelListener);

			OnWheelClickedListener click = new OnWheelClickedListener() {
				public void onItemClicked(WheelView wheel, int itemIndex) {
					wheel.setCurrentItem(itemIndex, true);
				}
			};
			degrees.addClickingListener(click);
			miminutes.addClickingListener(click);

			OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
				public void onScrollingStarted(WheelView wheel) {
					timeScrolled = true;
				}

				public void onScrollingFinished(WheelView wheel) {
					timeScrolled = false;
					timeChanged = true;

					timeChanged = false;
				}
			};

			degrees.addScrollingListener(scrollListener);
			miminutes.addScrollingListener(scrollListener);

			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	/**
	 * Adds changing listener for wheel that updates the wheel label
	 * 
	 * @param wheel
	 *            the wheel
	 * @param label
	 *            the wheel label
	 */
	private void addChangingListener(final WheelView wheel, final String label) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// wheel.setLabel(newValue != 1 ? label + "s" : label);
			}
		});
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
