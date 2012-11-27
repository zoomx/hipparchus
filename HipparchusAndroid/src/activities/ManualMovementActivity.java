package activities;

import orchestration.Orchestrator;
import bluetooth.BluetoothService;
import gr.mandim.R;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.app.Activity;

public class ManualMovementActivity extends Activity {
	
	//If this doesn't work put it inside onCreate()
	final BluetoothService btService = Orchestrator.getBtService();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_movement_dialog);
        
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
		
		Button close = (Button) findViewById(R.id.button_close);
		close.setOnClickListener(new OnClickListener() {			

			@Override
			public void onClick(View v) {
				finish();
			}

		});
    }
}
