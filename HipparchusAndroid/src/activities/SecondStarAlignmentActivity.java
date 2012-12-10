package activities;

import gr.mandim.R;
import orchestration.Orchestrator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SecondStarAlignmentActivity extends Activity {

	private static final String TAG = "Second Star Alignment";
	protected static final int MESSAGE_WRITE = 1;
	protected static final int MESSAGE_READ = 2;
	protected static final int ACTIVITY_REQUEST = 4;

	public TextView star2Name;
	public TextView star2RaName;
	public TextView star2DecName;
	public TextView star2Title;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second_star_layout);
		Log.i(TAG, "++ ON CREATE ++");

		star2Name = (TextView) findViewById(R.id.star2NameText);
		star2RaName = (TextView) findViewById(R.id.star2RaText);
		star2DecName = (TextView) findViewById(R.id.star2DecText);		
		star2Title = (TextView) findViewById(R.id.secondStarLabel);
		star2Title.setText("Select Second Star");

		Orchestrator.clearVisibleStarLists();
		Orchestrator.calcVisibleStars();		

		Button firstStarSelect = (Button) findViewById(R.id.star2SelectBtn);
		firstStarSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				Intent visStar = new Intent(getApplicationContext(), VisibleStarsActivity.class);
				visStar.putExtra("Activity", "secondStar");
				startActivityForResult(visStar, ACTIVITY_REQUEST);
			}
		});

		Button setFirstStar = (Button) findViewById(R.id.star2SetBtn);
		setFirstStar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Orchestrator.getStar2Coordinates();				
			}
		});
		
		Button locate = (Button) findViewById(R.id.locate2ndStar);
		locate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
								
				Intent locateStar = new Intent(getApplicationContext(), ManualMovementActivity.class);
				startActivity(locateStar);
			}
		});
		
		Button calibrate = (Button) findViewById(R.id.calibrateBtn);
		calibrate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Orchestrator.calcStar1(Orchestrator.getFirstStarRa(), Orchestrator.getFirstStarDec());
				Orchestrator.calcStar2(Orchestrator.getSecondStarRa(), Orchestrator.getSecondStarDec());
				Orchestrator.twoStarAlign();
				Intent trackActivity = new Intent(getApplicationContext(), ObjectTrackingActivity.class);
				startActivity(trackActivity);
			}
		});	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTIVITY_REQUEST && resultCode == RESULT_OK){
			if (data.hasExtra("selectedPosition")){
				int position = data.getExtras().getInt("selectedPosition");
				
				star2Name.setText(Orchestrator.getVisibleStarsLabelNames().get(position));
				star2RaName.setText(Orchestrator.getVisibleStarsLabelRa().get(position));
				star2DecName.setText(Orchestrator.getVisibleStarsLabelDec().get(position));
				
				Orchestrator.setSecondStarDec(Orchestrator.getVisibleStarsDec().get(position));
				Orchestrator.setSecondStarRa(Orchestrator.getVisibleStarsRa().get(position));
			}
			
		}
	}
}