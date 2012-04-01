package orchestration;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

public class Orchestrator extends Application{
	
	public double longitude;
    public double latitude;

	public static List<Double> visibleStarsRa = new ArrayList<Double>();
    public static List<Double> visibleStarsDec = new ArrayList<Double>();
    public static List<String> visibleStarsLabelNames = new ArrayList<String>();
    public static List<String> visibleStarsLabelRa = new ArrayList<String>();
    public static List<String> visibleStarsLabelDec = new ArrayList<String>();

	public void clearVisibleStarLists() {
        visibleStarsDec.clear();
        visibleStarsLabelNames.clear();
        visibleStarsRa.clear();
        visibleStarsLabelRa.clear();
        visibleStarsLabelDec.clear();
    }
	
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
