package activities;

import bluetooth.BluetoothService;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

	// Debugging
    private static final String TAG = "Bluetooth";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;
    
	// The FireFly mac address (Sparkfun Bluesmirf gold module)
	private static final String MAC_ADDRESS = "00:06:66:04:DB:38";
	private BluetoothService mService = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
        if (resultCode == Activity.RESULT_OK) {
        	BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(MAC_ADDRESS);
    		mService.connect(device);
        }
	}
}
