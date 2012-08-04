package activities;

import bluetooth.BluetoothService;
import orchestration.Orchestrator;
import gr.mandim.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class TwoStarAlignmentActivity extends Activity {
	
	private static final String TAG = "TwoStarAlignmentActivity";
	protected static final int MESSAGE_WRITE = 1;
	protected static final int MESSAGE_READ = 2;	
		
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.two_star_layout);
	    Log.i(TAG, "++ ON CREATE ++");
	    	    
	    final TextView star1Name = (TextView) findViewById(R.id.star1NameText);
	    final TextView star1RaName = (TextView) findViewById(R.id.star1RaText);
	    final TextView star1DecName = (TextView) findViewById(R.id.star1DecText);
	    final TextView star2Name = (TextView) findViewById(R.id.star2NameText);
	    final TextView star2RaName = (TextView) findViewById(R.id.star2RaText);
	    final TextView star2DecName = (TextView) findViewById(R.id.star2DecText);
	    
	    final ArrayAdapter<String> visStarNames = new ArrayAdapter<String>(this, R.layout.list_item, Orchestrator.getVisibleStarsLabelNames());
	    final ArrayAdapter<String> visStarRaStr = new ArrayAdapter<String>(this, R.layout.list_item, Orchestrator.getVisibleStarsLabelRa());
	    final ArrayAdapter<String> visStarDecStr = new ArrayAdapter<String>(this, R.layout.list_item, Orchestrator.getVisibleStarsLabelDec());
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    ((Orchestrator)this.getApplicationContext()).clearVisibleStarLists();
	    ((Orchestrator)this.getApplicationContext()).calcVisibleStars();
	    final BluetoothService btService = Orchestrator.getBtService();
	    //orc.clearVisibleStarLists();
	    //orc.calcVisibleStars();
	    
	    Button firstStarSelect = (Button) findViewById(R.id.star1SelectBtn);
		firstStarSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.setTitle("Select a Star");
				builder.setAdapter(visStarNames, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	star1Name.setText(visStarNames.getItem(item));
				    	star1RaName.setText(visStarRaStr.getItem(item));
				    	star1DecName.setText(visStarDecStr.getItem(item));
				    	
				    	Orchestrator.setRa(Orchestrator.getVisibleStarsRa().get(item));
				    	Orchestrator.setDec(Orchestrator.getVisibleStarsDec().get(item));
				    }
				});
				builder.show();
			}
		});
		
		Button secondStarSelect = (Button) findViewById(R.id.star2SelectBtn);
		secondStarSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.setTitle("Select a Star");
				builder.setAdapter(visStarNames, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	star2Name.setText(visStarNames.getItem(item));
				    	star2RaName.setText(visStarRaStr.getItem(item));
				    	star2DecName.setText(visStarDecStr.getItem(item));
				    	
				    	Orchestrator.setRa(Orchestrator.getVisibleStarsRa().get(item));
				    	Orchestrator.setDec(Orchestrator.getVisibleStarsDec().get(item));
				    }
				});
				builder.show();
			}
		});
		
		Button setFirstStar = (Button) findViewById(R.id.star1SetBtn);
		setFirstStar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				byte[] out = new String("T").getBytes();
				btService.write(out);
				
			}
		});
	}	
}