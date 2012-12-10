package activities;

import orchestration.Orchestrator;
import gr.mandim.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ObjectTrackingActivity extends Activity {

	
	private static final String TAG = "Object Tracking";
	
	public TextView scopeRa;
	public TextView scopeDec;
	public TextView targetRa;
	public TextView targetDec;
	public TextView targetAlt;
	public TextView targetAz;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tracking_layout);
	    
	    Log.i(TAG, "++ ON CREATE ++");
	    
	    scopeRa = (TextView) findViewById(R.id.scope_alt);
		scopeDec = (TextView) findViewById(R.id.scope_az);
		targetRa = (TextView) findViewById(R.id.targer_ra);
		targetDec = (TextView) findViewById(R.id.target_dec);
		targetAlt = (TextView) findViewById(R.id.target_alt);
		targetAz = (TextView) findViewById(R.id.target_az);
		
		Button syncBtn = (Button) findViewById(R.id.bt_sync);
		syncBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		ToggleButton trackBtn = (ToggleButton) findViewById(R.id.toggleTrackButton);
		trackBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            // The toggle is enabled
		        } else {
		        	Orchestrator.sendMessage("S");
		        }
		    }
		});
		
		Button stopBtn = (Button) findViewById(R.id.bt_manual);
		stopBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent locateStar = new Intent(getApplicationContext(), ManualMovementActivity.class);
				startActivity(locateStar);				
			}
		});
		
		Button goTo = (Button) findViewById(R.id.bt_goto);
		goTo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent locateObject = new Intent(getApplicationContext(), InsertTargetActivity.class);
				startActivity(locateObject);				
			}
		});
	}

}
