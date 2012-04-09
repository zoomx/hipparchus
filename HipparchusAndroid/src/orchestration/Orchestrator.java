package orchestration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import calculations.SimpleCoordinatesConverter;
import calculations.TimeAndUtils;

import android.app.Application;

public class Orchestrator extends Application{
	
	public static double ALT_LIMIT = 0;//This can be configured in the front-end
	public static double t0;
	
	public double longitude;
    public double latitude;

	public static List<Double> visibleStarsRa = new ArrayList<Double>();
    public static List<Double> visibleStarsDec = new ArrayList<Double>();
    public static List<String> visibleStarsLabelNames = new ArrayList<String>();
    public static List<String> visibleStarsLabelRa = new ArrayList<String>();
    public static List<String> visibleStarsLabelDec = new ArrayList<String>();
    
    private TimeAndUtils tau = new TimeAndUtils();
    private SimpleCoordinatesConverter cc = new SimpleCoordinatesConverter();
    
    public static double starRaArray[] = {
        8.97, 22.14, 19.51, 4.6, 0.22, 10.33, 9.46, 0.14, 2.03, 16.49, 5.55, 5.42, 5.92, 6.4,
        5.28, 7.58, 12.93, 20.69, 11.82, 0.73, 11.06, 22.96, 2.12, 12.57, 23.8, 3.04, 11.03,
        1.16, 3.41, 18.92, 5.66, 2.53, 7.76, 12.69, 7.66, 17.24, 8.16, 10.14, 5.24, 22.36,
        22.1, 23.06, 0.68, 17.56, 6.75, 13.42, 9.13, 14.07, 15.74, 18.62, 13.04};
    
    public static double starDecArray[] = {
        11.86, -46.96, 27.96, 16.51, 15.18, 19.84, -8.66, 29.09, 2.76, -26.43, -17.82,
        6.35, 7.41, -52.7, 46.0, 31.89, 38.32, 45.28, 14.57, -17.99, 61.75, -29.62, 23.46,
        -23.4, 15.21, 4.09, 56.38, 35.62, 49.86, -26.3, -34.7, 89.26, 28.03, -1.6, 5.23,
        14.39, -47.34, 11.97, -8.2, -1.39, -0.32, 28.08, 56.54, -37.1, -16.71, -11.16,
        -43.43, 64.38, 6.43, 38.78, 10.96};
    
    public static String starLabelNamesArray[] = {
        "Acubens (�-Cnc)", "Al Nair (�-Gru)", "Albireo (�-Cyg)",
        "Aldebaran (�-Tau)", "Algenib (�-Peg)", "Algieba (�-Leo)",
        "Alphard (�-Hya)", "Alpheratz (�-And)", "Alrescha (�-Pcs)",
        "Antares (�-Sco)", "Arneb (�-Lep)", "Bellatrix (�-Ori)",
        "Betelgeus (�-Ori)", "Canopus (�-Car)", "Capella (�-Aur)",
        "Castor (�-Gem)", "Cor Caroli (�-Cvn)", "Deneb (�-Cyg)",
        "Denebola (�-Leo)", "Diphda (�-Cet)", "Dubhe (�-UMa)",
        "Fomalhaut (�-Psa)", "Hamal (�-Ari)", "Kraz (�-Crv)",
        "Markab (�-Peg)", "Menkar (�-Cet)", "Merak (�-UMa)",
        "Mirach (�-And)", "Mirfak (�-Per)", "Nunki (�-Sgr)",
        "Phact (�-Col)", "Polaris (�-UMi)", "Pollux (�-Gem)",
        "Porrima (�-Vir)", "Procyon (�-CMi)", "Ras Algethi (�-Her)",
        "Regor (�-Vel)", "Regulus (�-Leo)", "Rigel (�-Ori)",
        "Sadachbia (�-Aqr)", "Sadalmelik (�-Aqr)", "Scheat (�-Peg)",
        "Schedar (�-Cas)", "Schaula (�-Sco)", "Sirius (�-CMa)",
        "Spica (�-Vir)", "Suhail (�-Vel)", "Thuban (�-Dra)",
        "Unukalhai (�-Ser)", "Vega (�-Lyr)", "Vindemiatrix (�-Vir)"};
    
    public static String starLabelRaArray[] = {
        "08h 58m 29.2s", "22h 08m 13.6s", "19h 30m 43.2s", "04h 35m 55.2s",
        "00h 13m 14.1s", "10h 19m 57.9s", "09h 27m 35.3s", "00h 08m 22.1s",
        "02h 02m 02.7s", "16h 29m 24.4s", "05h 32m 43.8s", "05h 25m 07.8s",
        "05h 55m 10.3s", "06h 23m 57.1s", "05h 16m 41.2s", "07h 34m 36.2s",
        "12h 56m 02.0s", "20h 41m 25.8s", "11h 49m 04.2s", "00h 43m 34.1s",
        "11h 03m 43.9s", "22h 57m 38.4s", "02h 07m 10.1s", "12h 34m 23.2s",
        "23h 04m 45.4s", "03h 02m 16.7s", "11h 01m 50.3s", "01h 09m 43.6s",
        "03h 24m 19.2s", "18h 55m 15.8s", "05h 39m 38.9s", "02h 31m 45.6s",
        "07h 45m 19.9s", "12h 41m 40.3s", "07h 39m 19.0s", "17h 14m 38.8s",
        "08h 09m 31.9s", "10h 08m 22.7s", "05h 14m 32.3s", "22h 21m 39.1s",
        "22h 05m 46.9s", "23h 03m 46.1s", "00h 40m 30.3s", "17h 33m 36.5s",
        "06h 45m 09.6s", "13h 25m 11.6s", "09h 07m 59.8s", "14h 04m 23.5s",
        "15h 44m 15.8s", "18h 36m 55.9s", "13h 02m 10.9s",};
    public static String starLabelDecArray[] = {
        "+11 51m 28s", "-46 57m 37s", "+27 57m 34s", "+16 30m 37s", "+15 11m 01s",
        "+19 50m 32s", "-08 39m 32s", "+29 05m 29s", "+02 45m 49s", "-26 25m 55s",
        "-17 49m 20s", "+06 20m 59s", "+07 24m 25s", "-52 41m 45s", "+46 00m 02s",
        "+31 53m 19s", "+38 19m 05s", "+45 16m 49s", "+14 34m 22s", "-17 59m 12s",
        "+61 45m 05s", "-29 37m 17s", "+23 27m 47s", "+23 23m 47s", "+15 12m 19s",
        "+04 05m 24s", "+56 22m 55s", "+35 37m 16s", "+49 51m 41s", "-26 17m 47s",
        "-34 04m 27s", "+89 15m 52s", "+28 01m 35s", "-01 35m 58s", "+05 13m 50s",
        "+14 23m 24s", "-47 20m 12s", "+11 58m 01s", "-08 12m 06s", "-01 23m 15s",
        "-00 19m 11s", "+28 04m 16s", "+56 32m 15s", "-37 06m 13s", "-16 42m 34s",
        "-11 09m 41s", "-43 25m 58s", "+64 22m 34s", "+06 25m 31s", "+38 46m 55s",
        "+10 57m 33s"};

    public Orchestrator() {
		super();
		Calendar cal = Calendar.getInstance();
        t0 = tau.getLocalDecimalTime(cal);
	}

	public void calcVisibleStars() {
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
	
	public static List<String> getVisibleStarsLabelNames() {
		return visibleStarsLabelNames;
	}

	public static void setVisibleStarsLabelNames(List<String> visibleStarsLabelNames) {
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
}
