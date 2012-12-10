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
		
		final WheelView raHour = (WheelView) findViewById(R.id.ra_h);
		raHour.setViewAdapter(new NumericWheelAdapter(this, 0, 23));
	
		final WheelView raMins = (WheelView) findViewById(R.id.ra_min);
		raMins.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d"));
		raMins.setCyclic(true);
		
		final TextView decLabel = (TextView)findViewById(R.id.insertDecLbl);
		
		final WheelView decDeg = (WheelView) findViewById(R.id.ra_h);
		decDeg.setViewAdapter(new NumericWheelAdapter(this, -90, 90));
	
		final WheelView decMin = (WheelView) findViewById(R.id.ra_min);
		decMin.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d"));
		decMin.setCyclic(true);
		
		Button save = (Button)findViewById(R.id.saveTargetCoord);
		
		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				raScrolled = true;
			}
			public void onScrollingFinished(WheelView wheel) {
				raScrolled = false;
				raChanged = true;
				//picker.setCurrentHour(hours.getCurrentItem());
				System.out.print(raHour.getCurrentItem()+"h");
				System.out.println(raMins.getCurrentItem()+"m");
				//picker.setCurrentMinute(mins.getCurrentItem());
				raChanged = false;
			}
		};
		
		raHour.addScrollingListener(scrollListener);
		raMins.addScrollingListener(scrollListener);
	}
}
