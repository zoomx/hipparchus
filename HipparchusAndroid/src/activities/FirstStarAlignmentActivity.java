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

public class FirstStarAlignmentActivity extends Activity {

	private static final String TAG = "First Star Alignment";
	protected static final int MESSAGE_WRITE = 1;
	protected static final int MESSAGE_READ = 2;
	protected static final int ACTIVITY_REQUEST = 3;
	
	public TextView star1Name;
	public TextView star1RaName;
	public TextView star1DecName;
	public TextView star1Title;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_star_layout);
		Log.i(TAG, "++ ON CREATE ++");
		
		star1Title = (TextView) findViewById(R.id.firstStarLabel);
		star1Title.setText("Select First Star");
		star1DecName = (TextView) findViewById(R.id.star1DecText);
		star1RaName = (TextView) findViewById(R.id.star1RaText);
		star1Name = (TextView) findViewById(R.id.star1NameText);

		Orchestrator.clearVisibleStarLists();
		Orchestrator.calcVisibleStars();		

		Button firstStarSelect = (Button) findViewById(R.id.star1SelectBtn);
		firstStarSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent visStar = new Intent(getApplicationContext(), VisibleStarsActivity.class);
				visStar.putExtra("Activity", "firstStar");
				startActivityForResult(visStar, ACTIVITY_REQUEST);
			}
		});

		Button setFirstStar = (Button) findViewById(R.id.star1SetBtn);
		setFirstStar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
				Orchestrator.getStar1Coordinates();				
				Intent secondStarAlignment = new Intent(getApplicationContext(), SecondStarAlignmentActivity.class);
				startActivity(secondStarAlignment);
			}
		});
		
		Button locate = (Button) findViewById(R.id.locate1stStar);
		locate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
								
				Intent locateStar = new Intent(getApplicationContext(), ManualMovementActivity.class);
				startActivity(locateStar);
			}
		});	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTIVITY_REQUEST && resultCode == RESULT_OK){
			if (data.hasExtra("selectedPosition")){
				int position = data.getExtras().getInt("selectedPosition");
				
				star1Name.setText(Orchestrator.getVisibleStarsLabelNames().get(position));
				star1RaName.setText(Orchestrator.getVisibleStarsLabelRa().get(position));
				star1DecName.setText(Orchestrator.getVisibleStarsLabelDec().get(position));
				
				Orchestrator.setFirstStarDec(Orchestrator.getVisibleStarsDec().get(position));
				Orchestrator.setFirstStarRa(Orchestrator.getVisibleStarsRa().get(position));
			}
			
		}
	}
	
}