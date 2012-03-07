package activities;

import bluetooth.BluetoothService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	// Debugging
    private static final String TAG = "Bluetooth";
    private static final boolean D = true;
    
    ProgressDialog dialog = null;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
 // Name of the connected device
    private String mConnectedDeviceName = null;
    
 // Layout Views
    private TextView mTitle;

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
		
	}
	

	@Override
	protected void onStart() {
		
		super.onStart();
		
		if(D) Log.e(TAG, "++ ON START ++");
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mService != null) mService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
	
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                    mTitle.setText("Connected to ");
                    mTitle.append(mConnectedDeviceName);
                    
                    //mConversationArrayAdapter.clear();
                    break;
                case BluetoothService.STATE_CONNECTING:
                    mTitle.setText("Connecting...");
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                    mTitle.setText("Not connected");
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                //mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
            	dialog.dismiss();
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
            	// Initialize the BluetoothChatService to perform bluetooth connections
                mService = new BluetoothService(this, mHandler);
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(MAC_ADDRESS);
                dialog = ProgressDialog.show(this,"","Connecting with Dob...",true);
                mService.connect(device);                
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, "Unable to enable BT", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
	}
}
