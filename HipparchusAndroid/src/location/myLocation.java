package location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class myLocation implements LocationListener {

	public myLocation() {
		super();

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
