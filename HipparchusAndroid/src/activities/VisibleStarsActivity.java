package activities;

import orchestration.Orchestrator;
import gr.mandim.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class VisibleStarsActivity extends Activity {

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visible_stars_activity);
		
		ListView visStars = (ListView)findViewById(android.R.id.list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, Orchestrator.getVisibleStarsLabelNames());
		
		visStars.setAdapter(adapter);
		 
	}
}
