package location;

import activities.SettingsActivity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class myLocation implements LocationListener {
	
	private final Handler myHandler;

	public myLocation(Handler lHandler) {
		super();
		myHandler = lHandler;

	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			Log.d("LOCATION CHANGED", location.getLatitude() + "");
			Log.d("LOCATION CHANGED", location.getLongitude() + "");

			// TODO send location lat and lon back to SettingsActivity or store
			// them in a global place
			// TODO examine global fields/values
			/*
			 * Toast.makeText(SettingsActivity.this, location.getLatitude() + ""
			 * + location.getLongitude(), Toast.LENGTH_LONG).show();
			 */
			// Send a failure message back to the Activity
			Message msg = myHandler.obtainMessage(SettingsActivity.MESSAGE_TOAST);
	        Bundle bundle = new Bundle();
	        bundle.putString(SettingsActivity.TOAST, "Lat: "+location.getLatitude()+"\nLon: "+location.getLongitude());
	        msg.setData(bundle);
	        myHandler.sendMessage(msg);
		}

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
