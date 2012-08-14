package orchestration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bluetooth.BluetoothService;

import calculations.AngleConverter;
import calculations.CoordinatesConverterMatrix;
import calculations.SimpleCoordinatesConverter;
import calculations.TimeAndUtils;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

import Jama.Matrix;

public class Orchestrator extends Application {

	public static final String TAG = "Orchestrator";
		
	/*
	 * This constant determines the resolution of the encoders. if mouse is used
	 * then the the encoder step is equals with the circumference of the circles
	 * (inches) times the dpi of the mouse. The value bellow is the result of a
	 * 10cm circle (3.9")
	 */
	// TODO: Needs to be defined properly when hardware installed
	public static double MOUSE_TICKS_PER_REV = 21600;
	public static double scopeAlt;
	public static double scopeAz;
	private double t; // Current time
	private static Matrix targetDcEq; // LMN
	private static Matrix targetDcTel; // lmn
	private CoordinatesConverterMatrix ccm = new CoordinatesConverterMatrix();
	private AngleConverter ac = new AngleConverter();
	public static double ALT_LIMIT = 0;// This can be configured in the
										// front-end
	public static double t0;
	private static Matrix T; // Transformation matrix T
	public static double ra;
	public static double dec;

	public double longitude;
	public double latitude;

	public static List<Double> visibleStarsRa = new ArrayList<Double>();
	public static List<Double> visibleStarsDec = new ArrayList<Double>();
	public static List<String> visibleStarsLabelNames = new ArrayList<String>();
	public static List<String> visibleStarsLabelRa = new ArrayList<String>();
	public static List<String> visibleStarsLabelDec = new ArrayList<String>();

	// Message types sent from the BluetoothService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_UNABLE_TO_CONNECT = 5;
	protected static final int MESSAGE_LOCATION = 6;
	
	// Key names received from the BluetoothService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	public static String ARDUINO_MESSAGE;

	public String arduinoMessage;
	
	private Handler mHandler;
	
	public static BluetoothService btService;
	public static BluetoothDevice device;
	public static BluetoothAdapter btAdapter;
	private static final String MAC_ADDRESS = "00:06:66:04:DB:38";
	private BluetoothAdapter mBluetoothAdapter;

	private TimeAndUtils tau = new TimeAndUtils();
	private SimpleCoordinatesConverter cc = new SimpleCoordinatesConverter();

	
	public static double starRaArray[] = { 8.97, 22.14, 19.51, 4.6, 0.22,
			10.33, 9.46, 0.14, 2.03, 16.49, 5.55, 5.42, 5.92, 6.4, 5.28, 7.58,
			12.93, 20.69, 11.82, 0.73, 11.06, 22.96, 2.12, 12.57, 23.8, 3.04,
			11.03, 1.16, 3.41, 18.92, 5.66, 2.53, 7.76, 12.69, 7.66, 17.24,
			8.16, 10.14, 5.24, 22.36, 22.1, 23.06, 0.68, 17.56, 6.75, 13.42,
			9.13, 14.07, 15.74, 18.62, 13.04 };

	public static double starDecArray[] = { 11.86, -46.96, 27.96, 16.51, 15.18,
			19.84, -8.66, 29.09, 2.76, -26.43, -17.82, 6.35, 7.41, -52.7, 46.0,
			31.89, 38.32, 45.28, 14.57, -17.99, 61.75, -29.62, 23.46, -23.4,
			15.21, 4.09, 56.38, 35.62, 49.86, -26.3, -34.7, 89.26, 28.03, -1.6,
			5.23, 14.39, -47.34, 11.97, -8.2, -1.39, -0.32, 28.08, 56.54,
			-37.1, -16.71, -11.16, -43.43, 64.38, 6.43, 38.78, 10.96 };

	public static String starLabelNamesArray[] = { "Acubens (á-Cnc)",
			"Al Nair (á-Gru)", "Albireo (â-Cyg)", "Aldebaran (á-Tau)",
			"Algenib (ã-Peg)", "Algieba (ã-Leo)", "Alphard (á-Hya)",
			"Alpheratz (á-And)", "Alrescha (á-Pcs)", "Antares (á-Sco)",
			"Arneb (á-Lep)", "Bellatrix (ã-Ori)", "Betelgeus (á-Ori)",
			"Canopus (á-Car)", "Capella (á-Aur)", "Castor (á-Gem)",
			"Cor Caroli (á-Cvn)", "Deneb (á-Cyg)", "Denebola (â-Leo)",
			"Diphda (â-Cet)", "Dubhe (á-UMa)", "Fomalhaut (á-Psa)",
			"Hamal (á-Ari)", "Kraz (â-Crv)", "Markab (á-Peg)",
			"Menkar (á-Cet)", "Merak (â-UMa)", "Mirach (â-And)",
			"Mirfak (á-Per)", "Nunki (ó-Sgr)", "Phact (á-Col)",
			"Polaris (á-UMi)", "Pollux (â-Gem)", "Porrima (ã-Vir)",
			"Procyon (á-CMi)", "Ras Algethi (á-Her)", "Regor (ã-Vel)",
			"Regulus (á-Leo)", "Rigel (â-Ori)", "Sadachbia (ã-Aqr)",
			"Sadalmelik (á-Aqr)", "Scheat (â-Peg)", "Schedar (á-Cas)",
			"Schaula (ë-Sco)", "Sirius (á-CMa)", "Spica (á-Vir)",
			"Suhail (ë-Vel)", "Thuban (á-Dra)", "Unukalhai (á-Ser)",
			"Vega (á-Lyr)", "Vindemiatrix (å-Vir)" };

	public static String starLabelRaArray[] = { "08h 58m 29.2s",
			"22h 08m 13.6s", "19h 30m 43.2s", "04h 35m 55.2s", "00h 13m 14.1s",
			"10h 19m 57.9s", "09h 27m 35.3s", "00h 08m 22.1s", "02h 02m 02.7s",
			"16h 29m 24.4s", "05h 32m 43.8s", "05h 25m 07.8s", "05h 55m 10.3s",
			"06h 23m 57.1s", "05h 16m 41.2s", "07h 34m 36.2s", "12h 56m 02.0s",
			"20h 41m 25.8s", "11h 49m 04.2s", "00h 43m 34.1s", "11h 03m 43.9s",
			"22h 57m 38.4s", "02h 07m 10.1s", "12h 34m 23.2s", "23h 04m 45.4s",
			"03h 02m 16.7s", "11h 01m 50.3s", "01h 09m 43.6s", "03h 24m 19.2s",
			"18h 55m 15.8s", "05h 39m 38.9s", "02h 31m 45.6s", "07h 45m 19.9s",
			"12h 41m 40.3s", "07h 39m 19.0s", "17h 14m 38.8s", "08h 09m 31.9s",
			"10h 08m 22.7s", "05h 14m 32.3s", "22h 21m 39.1s", "22h 05m 46.9s",
			"23h 03m 46.1s", "00h 40m 30.3s", "17h 33m 36.5s", "06h 45m 09.6s",
			"13h 25m 11.6s", "09h 07m 59.8s", "14h 04m 23.5s", "15h 44m 15.8s",
			"18h 36m 55.9s", "13h 02m 10.9s", };
	public static String starLabelDecArray[] = { "+11 51m 28s", "-46 57m 37s",
			"+27 57m 34s", "+16 30m 37s", "+15 11m 01s", "+19 50m 32s",
			"-08 39m 32s", "+29 05m 29s", "+02 45m 49s", "-26 25m 55s",
			"-17 49m 20s", "+06 20m 59s", "+07 24m 25s", "-52 41m 45s",
			"+46 00m 02s", "+31 53m 19s", "+38 19m 05s", "+45 16m 49s",
			"+14 34m 22s", "-17 59m 12s", "+61 45m 05s", "-29 37m 17s",
			"+23 27m 47s", "+23 23m 47s", "+15 12m 19s", "+04 05m 24s",
			"+56 22m 55s", "+35 37m 16s", "+49 51m 41s", "-26 17m 47s",
			"-34 04m 27s", "+89 15m 52s", "+28 01m 35s", "-01 35m 58s",
			"+05 13m 50s", "+14 23m 24s", "-47 20m 12s", "+11 58m 01s",
			"-08 12m 06s", "-01 23m 15s", "-00 19m 11s", "+28 04m 16s",
			"+56 32m 15s", "-37 06m 13s", "-16 42m 34s", "-11 09m 41s",
			"-43 25m 58s", "+64 22m 34s", "+06 25m 31s", "+38 46m 55s",
			"+10 57m 33s" };

	public Orchestrator() {
		super();
		Calendar cal = Calendar.getInstance();
		t0 = tau.getLocalDecimalTime(cal);
	}

	/*
	 * This method gets the serial message from Arduino and calls the
	 * appropriate function
	 */
	public void getMessage() {

		Log.i(TAG,
				"+++ GOT MESSAGE FROM ARDUINO +++" + this.getArduinoMessage());
		String[] msgSplit = getArduinoMessage().split(":");
		String keyWord = "";
		String firstVaule = "";
		String secondValue = "";

		if (msgSplit.length > 0) {
			keyWord = msgSplit[1];
			firstVaule = msgSplit[2];
			secondValue = msgSplit[3];
		}

		if (keyWord.equals("xy")) {
			getTelescopeAltAz(firstVaule, secondValue);
			startTracking();
		}
		if (keyWord.equals("xyt")) {
			Log.i(TAG, "+++ GOT TELESCOPE POSITION " + firstVaule + " "
					+ secondValue);
			getTelescopeAltAz(firstVaule, secondValue);
		}
	}

	public void startTracking() {

		Calendar cal = Calendar.getInstance();
		t = tau.getLocalDecimalTime(cal);
		targetDcEq = ccm.starDcEquatorial(ra, dec, t, t0);
		targetDcTel = T.times(targetDcEq);

		double[] targetArrayDcTel = targetDcTel.getColumnPackedCopy();
		double tanFi = targetArrayDcTel[1] / targetArrayDcTel[0];
		double az = Math.toDegrees(Math.atan(tanFi));
		if (az < 0) {
			az = 180 + az;
		}
		double sinThi = targetArrayDcTel[2];
		double alt = Math.toDegrees(Math.asin(sinThi));

		ac.setDegreeDecimal(alt);
		ac.convertToDegMinSec();
		// FIXME
		// GuiUpdater.window.objectsAlt.setText("" + ac.getDeg() + "\u00b0" +
		// ac.getMin() + "'" + ac.getSec() + "\"");
		ac.setDegreeDecimal(az);
		ac.convertToDegMinSec();
		// FIXME
		// GuiUpdater.window.objectsAz.setText("" + ac.getDeg() + "\u00b0" +
		// ac.getMin() + "'" + ac.getSec() + "\"");

		double revInMin = 21600;// 360*60;
		double minPerTick = revInMin / MOUSE_TICKS_PER_REV;
		double altInMin = alt * 60;
		double azInMin = az * 60;
		int stepsToGoAlt = (int) (altInMin / minPerTick);
		int stepsToGoAz = (int) (azInMin / minPerTick);
		sendMessage("G" + String.valueOf(stepsToGoAz) + ","
				+ String.valueOf(stepsToGoAlt));
	}

	/*
	 * Sends a message to the Arduino in String form
	 */
	public void sendMessage(String message) {		
		btService.write(message.getBytes());
	}

	public void getTelescopeAltAz(String x, String y) {

		// Convert string to int and store it
		double xDouble = Double.parseDouble(x);
		double yDouble = Double.parseDouble(y);

		// convert to degrees based on the steps
		double xCoordinate = (xDouble / MOUSE_TICKS_PER_REV) * 360.0;
		double yCoordinate = (yDouble / MOUSE_TICKS_PER_REV) * 360.0;

		ac.setDegreeDecimal(yCoordinate);
		// ac.convertToDegMinSec();
		// GuiUpdater.window.scopeAlt.setText("" + ac.getDeg() + "\u00b0" +
		// ac.getMin() + "'" + ac.getSec() + "\"");
		ac.setDegreeDecimal(xCoordinate);
		// ac.convertToDegMinSec();
		// GuiUpdater.window.scopeAz.setText("" + ac.getDeg() + "\u00b0" +
		// ac.getMin() + "'" + ac.getSec() + "\"");

		scopeAlt = yCoordinate;
		scopeAz = xCoordinate;

		Log.i(TAG, " +++ GET TELESCOPE ALT AZ +++ " + scopeAlt + " " + scopeAz);
	}

	public void calcVisibleStars() {
		Log.i(TAG, "++ calcVisibleStars() ++");
		for (int i = 0; i < starRaArray.length; i++) {
			Calendar cal = Calendar.getInstance();
			double lst = tau.getLocalSiderealTime(cal, longitude);
			double ha = tau.getHourAngleInHours(lst, starRaArray[i]);

			double alt = cc.convertRaDecToAlt(starDecArray[i], latitude, ha);
			if (alt > ALT_LIMIT) {
				visibleStarsRa.add(starRaArray[i]);
				visibleStarsDec.add(starDecArray[i]);
				visibleStarsLabelNames.add(starLabelNamesArray[i]);
				visibleStarsLabelRa.add(starLabelRaArray[i]);
				visibleStarsLabelDec.add(starLabelDecArray[i]);
			}
		}
	}

	public void clearVisibleStarLists() {
		visibleStarsDec.clear();
		visibleStarsLabelNames.clear();
		visibleStarsRa.clear();
		visibleStarsLabelRa.clear();
		visibleStarsLabelDec.clear();
	}
	
	public void connectWithTelescope() {
		
		btService = new BluetoothService(this);
		btService.setmHandler(getmHandler());
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		device = mBluetoothAdapter.getRemoteDevice(MAC_ADDRESS);
		btService.connect(device);
	}
	
	public void disconnect() {
		
		btService.stop();
		
	}

	public static List<String> getVisibleStarsLabelNames() {
		return visibleStarsLabelNames;
	}

	public static void setVisibleStarsLabelNames(
			List<String> visibleStarsLabelNames) {
		Orchestrator.visibleStarsLabelNames = visibleStarsLabelNames;
	}

	public static List<String> getVisibleStarsLabelRa() {
		return visibleStarsLabelRa;
	}

	public static void setVisibleStarsLabelRa(List<String> visibleStarsLabelRa) {
		Orchestrator.visibleStarsLabelRa = visibleStarsLabelRa;
	}

	public static List<String> getVisibleStarsLabelDec() {
		return visibleStarsLabelDec;
	}

	public static void setVisibleStarsLabelDec(List<String> visibleStarsLabelDec) {
		Orchestrator.visibleStarsLabelDec = visibleStarsLabelDec;
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

	public String getArduinoMessage() {
		return arduinoMessage;
	}

	public static double getScopeAlt() {
		return scopeAlt;
	}

	public static void setScopeAlt(double scopeAlt) {
		Orchestrator.scopeAlt = scopeAlt;
	}

	public static double getScopeAz() {
		return scopeAz;
	}

	public static void setScopeAz(double scopeAz) {
		Orchestrator.scopeAz = scopeAz;
	}

	public static double getRa() {
		return ra;
	}

	public static void setRa(double ra) {
		Orchestrator.ra = ra;
	}

	public static double getDec() {
		return dec;
	}

	public static void setDec(double dec) {
		Orchestrator.dec = dec;
	}

	public void setArduinoMessage(String arduinoMessage) {
		this.arduinoMessage = arduinoMessage;
	}

	public static List<Double> getVisibleStarsRa() {
		return visibleStarsRa;
	}

	public static void setVisibleStarsRa(List<Double> visibleStarsRa) {
		Orchestrator.visibleStarsRa = visibleStarsRa;
	}

	public static List<Double> getVisibleStarsDec() {
		return visibleStarsDec;
	}

	public static void setVisibleStarsDec(List<Double> visibleStarsDec) {
		Orchestrator.visibleStarsDec = visibleStarsDec;
	}

	public static BluetoothService getBtService() {
		return btService;
	}

	public static void setBtService(BluetoothService btService) {
		Orchestrator.btService = btService;
	}

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}
	
	
}
