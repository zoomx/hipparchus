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

public class FirstStarAlignmentActivity extends Activity {

	private static final String TAG = "First Star Alignment";
	protected static final int MESSAGE_WRITE = 1;
	protected static final int MESSAGE_READ = 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_star_layout);
		Log.i(TAG, "++ ON CREATE ++");

		final TextView star1Name = (TextView) findViewById(R.id.star1NameText);
		final TextView star1RaName = (TextView) findViewById(R.id.star1RaText);
		final TextView star1DecName = (TextView) findViewById(R.id.star1DecText);
		final TextView star1Title = (TextView) findViewById(R.id.firstStarLabel);
		star1Title.setText("Select First Star");

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

		Button firstStarSelect = (Button) findViewById(R.id.star1SelectBtn);
		firstStarSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*builder.setAdapter(visStarNames,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int item) {
								star1Name.setText(visStarNames.getItem(item));
								star1RaName.setText(visStarRaStr.getItem(item));
								star1DecName.setText(visStarDecStr
										.getItem(item));

								Orchestrator.setFirstStarRa(Orchestrator
										.getVisibleStarsRa().get(item));
								Orchestrator.setFirstStarDec(Orchestrator
										.getVisibleStarsDec().get(item));
							}
						});				
				builder.show();*/
				Intent visStar = new Intent(getApplicationContext(), VisibleStarsActivity.class);
				startActivity(visStar);
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
}