package activities;

import bluetooth.BluetoothService;
import orchestration.Orchestrator;
import gr.mandim.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ObjectTrackingActivity extends Activity {

	
	private static final String TAG = "ObjectTrackingActivity";
	//protected static final int MESSAGE_WRITE = 1;
	//protected static final int MESSAGE_READ = 2;
	public Orchestrator orc;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tracking_layout);
	    
	    Log.i(TAG, "++ ON CREATE ++");
	    
	    final TextView scopeRa = (TextView) findViewById(R.id.scope_alt);
		final TextView scopeDec = (TextView) findViewById(R.id.scope_az);
		final TextView targetRa = (TextView) findViewById(R.id.targer_ra);
		final TextView targetDec = (TextView) findViewById(R.id.target_dec);
		final TextView targetAlt = (TextView) findViewById(R.id.target_alt);
		final TextView targetAz = (TextView) findViewById(R.id.target_az);
		
		final BluetoothService btService = Orchestrator.getBtService();
		
		Button syncBtn = (Button) findViewById(R.id.bt_sync);
		syncBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Button trackBtn = (Button) findViewById(R.id.bt_track);
		trackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Button stopBtn = (Button) findViewById(R.id.bt_stop);
		stopBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
