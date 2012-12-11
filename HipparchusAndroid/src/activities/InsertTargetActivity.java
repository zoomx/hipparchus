package activities;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import gr.mandim.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class InsertTargetActivity extends Activity {
	
	public boolean raChanged = false;		
	public boolean raScrolled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insert_target_layout);
		
		final TextView raLabel = (TextView)findViewById(R.id.insertRaLbl);
		
		Button save = (Button)findViewById(R.id.saveTargetCoord);
	}
}
