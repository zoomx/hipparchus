package activities;

import orchestration.Orchestrator;
import gr.mandim.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class TwoStarAlignmentActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.two_star_layout);
	    
	    Orchestrator orc = (Orchestrator)getApplication();
	    orc.clearVisibleStarLists();
	    orc.calcVisibleStars();
	    
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);	    	    
	    final ArrayAdapter<String> items = new ArrayAdapter<String>(this, R.layout.list_item, Orchestrator.getVisibleStarsLabelNames());
	    
	    Button locationBtn = (Button) findViewById(R.id.star1SelectBtn);
		locationBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.setTitle("Select a Star");
				builder.setAdapter(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				        Toast.makeText(getApplicationContext(), items.getItem(item), Toast.LENGTH_SHORT).show();
				    }
				});
				builder.show();
			}
		});
	}
}