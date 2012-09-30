package activities;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

		latitudeText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(COORDINATES_DIALOG);
			}
		});

		longitudeText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(COORDINATES_DIALOG);
			}
		});

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
		setMenuBackground();
		return true;
	}

	protected void setMenuBackground() {
		getLayoutInflater().setFactory(new Factory() {

			@Override
			public View onCreateView(String name, Context context,
					AttributeSet attrs) {

				if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {

					try { // Ask our inflater to create the view
						final LayoutInflater f = getLayoutInflater();
						final View[] view = new View[1];
						try {
							view[0] = f.createView(name, null, attrs);
						} catch (InflateException e) {
							hackAndroid23(name, attrs, f, view);
						}
						// Kind of apply our own background
						new Handler().post(new Runnable() {
							public void run() {
								//view[0].setBackgroundColor(Color.BLACK);
								view[0].setBackgroundResource(R.drawable.dialog_bg);
								//view[0].setPadding(10, 10, 10, 10);
							}
						});
						return view[0];
					} catch (InflateException e) {
					} catch (ClassNotFoundException e) {

					}
				}
				return null;
			}
		});
	}

	static void hackAndroid23(final String name,
			final android.util.AttributeSet attrs, final LayoutInflater f,
			final View[] view) {
		// mConstructorArgs[0] is only non-null during a running call to
		// inflate()
		// so we make a call to inflate() and inside that call our dully
		// XmlPullParser get's called
		// and inside that it will work to call
		// "f.createView( name, null, attrs );"!
		try {
			f.inflate(new XmlPullParser() {
				@Override
				public int next() throws XmlPullParserException, IOException {
					try {
						view[0] = (TextView) f.createView(name, null, attrs);
					} catch (InflateException e) {
					} catch (ClassNotFoundException e) {
					}
					throw new XmlPullParserException("exit");
				}

				@Override
				public void defineEntityReplacementText(String entityName,
						String replacementText) throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

				@Override
				public int getAttributeCount() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public String getAttributeName(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getAttributeNamespace(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getAttributePrefix(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getAttributeType(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getAttributeValue(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getAttributeValue(String namespace, String name) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public int getColumnNumber() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public int getDepth() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public int getEventType() throws XmlPullParserException {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public boolean getFeature(String name) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public String getInputEncoding() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public int getLineNumber() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public String getName() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getNamespace() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getNamespace(String prefix) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public int getNamespaceCount(int depth)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public String getNamespacePrefix(int pos)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getNamespaceUri(int pos)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getPositionDescription() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getPrefix() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Object getProperty(String name) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getText() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public char[] getTextCharacters(int[] holderForStartAndLength) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public boolean isAttributeDefault(int index) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean isEmptyElementTag()
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean isWhitespace() throws XmlPullParserException {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public int nextTag() throws XmlPullParserException, IOException {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public String nextText() throws XmlPullParserException,
						IOException {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public int nextToken() throws XmlPullParserException,
						IOException {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public void require(int type, String namespace, String name)
						throws XmlPullParserException, IOException {
					// TODO Auto-generated method stub

				}

				@Override
				public void setFeature(String name, boolean state)
						throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

				@Override
				public void setInput(Reader in) throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

				@Override
				public void setInput(InputStream inputStream,
						String inputEncoding) throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

				@Override
				public void setProperty(String name, Object value)
						throws XmlPullParserException {
					// TODO Auto-generated method stub

				}
			}, null, false);
		} catch (InflateException e1) {
			// "exit" ignored
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
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
		final Dialog dialog;
		switch (id) {
		case COORDINATES_DIALOG:

			dialog = new Dialog(this, R.style.myDialog);
			dialog.setContentView(R.layout.coordinates_dialog);
			dialog.setTitle("Add Coordinates");

			final WheelView degrees = (WheelView) dialog.findViewById(R.id.deg);
			degrees.setViewAdapter(new NumericWheelAdapter(this, 0, 360));

			final WheelView miminutes = (WheelView) dialog
					.findViewById(R.id.min);
			miminutes.setViewAdapter(new NumericWheelAdapter(this, 0, 59,
					"%02d"));

			Button saveCoordinates = (Button) dialog
					.findViewById(R.id.btnSaveCoordinates);
			saveCoordinates.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});

			Button cancelCoordinates = (Button) dialog
					.findViewById(R.id.btnSaveCoordinatesCancel);
			cancelCoordinates.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dialog.dismiss();
				}
			});

			// add listeners
			addChangingListener(miminutes, "min");
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

