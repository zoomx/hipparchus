package activities;

import gr.mandim.R;
import orchestration.Orchestrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SecondStarAlignmentActivity extends Activity {

	private static final String TAG = "TwoStarAlignmentActivity";
	protected static final int MESSAGE_WRITE = 1;
	protected static final int MESSAGE_READ = 2;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second_star_layout);
		Log.i(TAG, "++ ON CREATE ++");

		final TextView star1Name = (TextView) findViewById(R.id.star2NameText);
		final TextView star1RaName = (TextView) findViewById(R.id.star2RaText);
		final TextView star1DecName = (TextView) findViewById(R.id.star2DecText);
		
		final TextView star2Title = (TextView) findViewById(R.id.secondStarLabel);
		star2Title.setText("Select Second Star");

		final ArrayAdapter<String> visStarNames = new ArrayAdapter<String>(
				this, R.layout.list_item,
				Orchestrator.getVisibleStarsLabelNames());
		final ArrayAdapter<String> visStarRaStr = new ArrayAdapter<String>(
				this, R.layout.list_item, Orchestrator.getVisibleStarsLabelRa());
		final ArrayAdapter<String> visStarDecStr = new ArrayAdapter<String>(
				this, R.layout.list_item,
				Orchestrator.getVisibleStarsLabelDec());
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		Orchestrator.clearVisibleStarLists();
		Orchestrator.calcVisibleStars();		

		Button firstStarSelect = (Button) findViewById(R.id.star2SelectBtn);
		firstStarSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//builder.setTitle("Select a Star");
				builder.setAdapter(visStarNames,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int item) {
								star1Name.setText(visStarNames.getItem(item));
								star1RaName.setText(visStarRaStr.getItem(item));
								star1DecName.setText(visStarDecStr
										.getItem(item));

								Orchestrator.setSecondStarRa(Orchestrator
										.getVisibleStarsRa().get(item));
								Orchestrator.setSecondStarDec(Orchestrator
										.getVisibleStarsDec().get(item));
							}
						});
				builder.show();
			}
		});

		Button setFirstStar = (Button) findViewById(R.id.star2SetBtn);
		setFirstStar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Orchestrator.getStar2Coordinates();				
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
}