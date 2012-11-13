package activities;

import gr.mandim.R;
import orchestration.Orchestrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import bluetooth.BluetoothService;

public class FirstStarAlignmentActivity extends Activity {

	private static final String TAG = "TwoStarAlignmentActivity";
	protected static final int MESSAGE_WRITE = 1;
	protected static final int MESSAGE_READ = 2;

	public Orchestrator orc;

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

		orc = (Orchestrator)this.getApplicationContext();
		((Orchestrator) this.getApplicationContext()).clearVisibleStarLists();
		((Orchestrator) this.getApplicationContext()).calcVisibleStars();
		final BluetoothService btService = Orchestrator.getBtService();

		Button firstStarSelect = (Button) findViewById(R.id.star1SelectBtn);
		firstStarSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.setTitle("Select a Star");
				builder.setAdapter(visStarNames,
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
				builder.show();
			}
		});

		Button setFirstStar = (Button) findViewById(R.id.star1SetBtn);
		setFirstStar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/*
				byte[] out = new String("T").getBytes();
				btService.write(out);
				
				((Orchestrator) getApplicationContext()).calcStar1(Orchestrator.getRa(), Orchestrator.getDec());
				*/
				Orchestrator.getStar1Coordinates();
				//Log.i(TAG, "Star1 Alt="+Orchestrator.getStar1Alt()+" Az="+Orchestrator.getStar1Az());
				Intent secondStarAlignment = new Intent(getApplicationContext(), SecondStarAlignmentActivity.class);
				startActivity(secondStarAlignment);
			}
		});		

		Button moveLeft = (Button) findViewById(R.id.bt_move_left);
		moveLeft.setOnTouchListener(new OnTouchListener() {
			byte[] out;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					out = new String("l").getBytes();
					btService.write(out);
					break;
				case MotionEvent.ACTION_UP:
					out = new String("ls").getBytes();
					btService.write(out);
					break;
				}
				return false;
			}

		});

		Button moveRight = (Button) findViewById(R.id.bt_move_right);
		moveRight.setOnTouchListener(new OnTouchListener() {
			byte[] out;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					out = new String("r").getBytes();
					btService.write(out);
					break;
				case MotionEvent.ACTION_UP:
					out = new String("rs").getBytes();
					btService.write(out);
					break;
				}
				return false;
			}

		});

		Button moveUp = (Button) findViewById(R.id.bt_move_up);
		moveUp.setOnTouchListener(new OnTouchListener() {
			byte[] out;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					out = new String("u").getBytes();
					btService.write(out);
					break;
				case MotionEvent.ACTION_UP:
					out = new String("us").getBytes();
					btService.write(out);
					break;
				}
				return false;
			}

		});

		Button moveDown = (Button) findViewById(R.id.bt_move_down);
		moveDown.setOnTouchListener(new OnTouchListener() {
			byte[] out;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					out = new String("d").getBytes();
					btService.write(out);
					break;
				case MotionEvent.ACTION_UP:
					out = new String("ds").getBytes();
					btService.write(out);
					break;
				}
				return false;
			}

		});

		Button moveReset = (Button) findViewById(R.id.bt_reset_position);
		moveReset.setOnTouchListener(new OnTouchListener() {
			byte[] out;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					out = new String("R").getBytes();
					btService.write(out);
					break;
				}
				return false;
			}

		});

	}

}