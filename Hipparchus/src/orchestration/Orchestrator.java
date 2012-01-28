package orchestration;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;
import java.awt.Color;
import java.util.List;
import calculations.AngleConverter;
import calculations.CoordinatesConverterMatrix;
import calculations.SimpleCoordinatesConverter;
import calculations.TimeAndUtils;
import gui.GuiUpdater;
import Jama.Matrix;
import serial.Communicator;

/**
 *
 * @author mandim
 */
public class Orchestrator {

    public String arduinoMessage;
    /*
     * This constant determines the resolution of the encoders.
     * if mouse is used then the the encoder step is equals
     * with the circumference of the circles (inches) times the dpi
     * of the mouse. The value bellow is the result
     * of a 10cm circle (3.9")
     */
    // TODO: Needs to be defined properly when hardware installed
    public static double MOUSE_TICKS_PER_REV = 43200;
    public static double ALT_LIMIT = 0;//This can be configured in the front-end
    public static double starRaArray[] = {
        8.97,22.14,19.51,4.6,0.22,10.33,9.46,0.14,2.03,16.49,5.55,5.42,5.92,6.4,
        5.28,7.58,12.93,20.69,11.82,0.73,11.06,22.96,2.12,12.57,23.8,3.04,11.03,
        1.16,3.41,18.92,5.66,2.53,7.76,12.69,7.66,17.24,8.16,10.14,5.24,22.36,
        22.1,23.06,0.68,17.56,6.75,13.42,9.13,14.07,15.74,18.62,13.04};
    public static double starDecArray[] = {
        11.86,-46.96,27.96,16.51,15.18,19.84,-8.66,29.09,2.76,-26.43,-17.82,
        6.35,7.41,-52.7,46.0,31.89,38.32,45.28,14.57,-17.99,61.75,-29.62,23.46,
        -23.4,15.21,4.09,56.38,35.62,49.86,-26.3,-34.7,89.26,28.03,-1.6,5.23,
        14.39,-47.34,11.97,-8.2,-1.39,-0.32,28.08,56.54,-37.1,-16.71,-11.16,
        -43.43,64.38,6.43,38.78,10.96};
    public static String starLabelNamesArray[] = {
        "Acubens (α-Cnc)","Al Nair (α-Gru)","Albireo (β-Cyg)",
        "Aldebaran (α-Tau)","Algenib (γ-Peg)","Algieba (γ-Leo)",
        "Alphard (α-Hya)","Alpheratz (α-And)","Alrescha (α-Pcs)",
        "Antares (α-Sco)","Arneb (α-Lep)","Bellatrix (γ-Ori)",
        "Betelgeus (α-Ori)","Canopus (α-Car)","Capella (α-Aur)",
        "Castor (α-Gem)","Cor Caroli (α-Cvn)","Deneb (α-Cyg)",
        "Denebola (β-Leo)","Diphda (β-Cet)","Dubhe (α-UMa)",
        "Fomalhaut (α-Psa)","Hamal (α-Ari)","Kraz (β-Crv)",
        "Markab (α-Peg)","Menkar (α-Cet)","Merak (β-UMa)",
        "Mirach (β-And)","Mirfak (α-Per)","Nunki (σ-Sgr)",
        "Phact (α-Col)","Polaris (α-UMi)","Pollux (β-Gem)",
        "Porrima (γ-Vir)","Procyon (α-CMi)","Ras Algethi (α-Her)",
        "Regor (γ-Vel)","Regulus (α-Leo)","Rigel (β-Ori)",
        "Sadachbia (γ-Aqr)","Sadalmelik (α-Aqr)","Scheat (β-Peg)",
        "Schedar (α-Cas)","Schaula (λ-Sco)","Sirius (α-CMa)",
        "Spica (α-Vir)","Suhail (λ-Vel)","Thuban (α-Dra)",
        "Unukalhai (α-Ser)","Vega (α-Lyr)","Vindemiatrix (ε-Vir)"};
    public static String starLabelRaArray[] = {
        "08h 58m 29.2s","22h 08m 13.6s","19h 30m 43.2s","04h 35m 55.2s",
        "00h 13m 14.1s","10h 19m 57.9s","09h 27m 35.3s","00h 08m 22.1s",
        "02h 02m 02.7s","16h 29m 24.4s","05h 32m 43.8s","05h 25m 07.8s",
        "05h 55m 10.3s","06h 23m 57.1s","05h 16m 41.2s","07h 34m 36.2s",
        "12h 56m 02.0s","20h 41m 25.8s","11h 49m 04.2s","00h 43m 34.1s",
        "11h 03m 43.9s","22h 57m 38.4s","02h 07m 10.1s","12h 34m 23.2s",
        "23h 04m 45.4s","03h 02m 16.7s","11h 01m 50.3s","01h 09m 43.6s",
        "03h 24m 19.2s","18h 55m 15.8s","05h 39m 38.9s","02h 31m 45.6s",
        "07h 45m 19.9s","12h 41m 40.3s","07h 39m 19.0s","17h 14m 38.8s",
        "08h 09m 31.9s","10h 08m 22.7s","05h 14m 32.3s","22h 21m 39.1s",
        "22h 05m 46.9s","23h 03m 46.1s","00h 40m 30.3s","17h 33m 36.5s",
        "06h 45m 09.6s","13h 25m 11.6s","09h 07m 59.8s","14h 04m 23.5s",
        "15h 44m 15.8s","18h 36m 55.9s","13h 02m 10.9s",};
    public static String starLabelDecArray[] = {
        "+11 51m 28s","-46 57m 37s","+27 57m 34s","+16 30m 37s","+15 11m 01s",
        "+19 50m 32s","-08 39m 32s","+29 05m 29s","+02 45m 49s","-26 25m 55s",
        "-17 49m 20s","+06 20m 59s","+07 24m 25s","-52 41m 45s","+46 00m 02s",
        "+31 53m 19s","+38 19m 05s","+45 16m 49s","+14 34m 22s","-17 59m 12s",
        "+61 45m 05s","-29 37m 17s","+23 27m 47s","+23 23m 47s","+15 12m 19s",
        "+04 05m 24s","+56 22m 55s","+35 37m 16s","+49 51m 41s","-26 17m 47s",
        "-34 04m 27s","+89 15m 52s","+28 01m 35s","-01 35m 58s","+05 13m 50s",
        "+14 23m 24s","-47 20m 12s","+11 58m 01s","-08 12m 06s","-01 23m 15s",
        "-00 19m 11s","+28 04m 16s","+56 32m 15s","-37 06m 13s","-16 42m 34s",
        "-11 09m 41s","-43 25m 58s","+64 22m 34s","+06 25m 31s","+38 46m 55s",
        "+10 57m 33s"};
    public static List<Double> visibleStarsRa = new ArrayList<Double>();
    public static List<Double> visibleStarsDec = new ArrayList<Double>();
    public static List<String> visibleStarsLabelNames = new ArrayList<String>();
    public static List<String> visibleStarsLabelRa = new ArrayList<String>();
    public static List<String> visibleStarsLabelDec = new ArrayList<String>();
    public static double longitude;
    public static double latitude;
    public static double ra;
    public static double dec;
    public static double scopeAlt;
    public static double scopeAz;
    public static double objectAlt;
    public static double objectAz;
    public static double t0;

    private double t;                  //Current time

    private static Matrix star1DcEq;   //L1M1N1
    private static Matrix star1DcTel;  //l1m1n1
    private static Matrix star2DcEq;   //L2M2N2
    private static Matrix star2DcTel;  //l2m2n2
    private static Matrix star3DcEq;   //L3M3N3
    private static Matrix star3DcTel;  //l3m3n3
    private static Matrix targetDcEq;  //LMN
    private static Matrix targetDcTel; //lmn
    private static Matrix T;           //Transformation matrix T

    private static Communicator serialComm = null;

    private SimpleCoordinatesConverter cc = new SimpleCoordinatesConverter();
    private CoordinatesConverterMatrix ccm = new CoordinatesConverterMatrix();
    private TimeAndUtils tau = new TimeAndUtils();
    private Timer timer = new Timer();
    private AngleConverter ac = new AngleConverter();
    
    protected TimerTask tt;

    public Orchestrator() {
        Calendar cal = Calendar.getInstance();
        //cal.set(2011, 10, 21, 21, 0, 0);// this is a test
        t0 = tau.getLocalDecimalTime(cal);
    }

    /*
     * This method gets the serial message from Arduino and calls the appropriate function
     */
    public void getMessage() {

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
        if (keyWord.equals("xyt")){
            getTelescopeAltAz(firstVaule, secondValue);
        }
    }

    public void setLatitude(String deg, String min, String sec, boolean east) {
        if (deg.isEmpty() || min.isEmpty() || sec.isEmpty()) {
            GuiUpdater.updateLog("Latitude not set properly", Color.RED);
        } else {
            int degInt = Integer.parseInt(deg);
            int minInt = Integer.parseInt(min);
            int secInt = Integer.parseInt(sec);

            double lat = ac.convertToDecimal(degInt, minInt, secInt);
            if (east) {
                setLatitude(lat);
            } else {
                setLatitude(lat * -1.0);
            }
        }
    }

    public void setLongitude(String deg, String min, String sec, boolean north) {
        if (deg.isEmpty() || min.isEmpty() || sec.isEmpty()) {
            GuiUpdater.updateLog("Longitude not set properly", Color.RED);
        } else {
            int degInt = Integer.parseInt(deg);
            int minInt = Integer.parseInt(min);
            int secInt = Integer.parseInt(sec);

            double lon = ac.convertToDecimal(degInt, minInt, secInt);
            if (north) {
                setLongitude(lon);
            } else {
                setLongitude(lon * -1.0);
            }
        }
    }

    public void setRa(String hour, String min, String sec) {
        if (hour.isEmpty() || min.isEmpty() || sec.isEmpty()) {
            GuiUpdater.updateLog("RA not set properly", Color.RED);
        } else {
            int hourInt = Integer.parseInt(hour);
            int minInt = Integer.parseInt(min);
            int secInt = Integer.parseInt(sec);

            double localRa = ac.convertToDecimal(hourInt, minInt, secInt);
            setRa(localRa);
        }
    }

    public void setDec(String deg, String min, String sec) {
        if (deg.isEmpty() || min.isEmpty() || sec.isEmpty()) {
            GuiUpdater.updateLog("Dec not set properly");
        } else {
            int degInt = Integer.parseInt(deg);
            int minInt = Integer.parseInt(min);
            int secInt = Integer.parseInt(sec);

            double localDec = ac.convertToDecimal(degInt, minInt, secInt);
            setDec(localDec);
        }
    }

    public void calcVisibleStars() {
        for (int i = 0; i < starRaArray.length; i++){
            Calendar cal = Calendar.getInstance();
            double lst = tau.getLocalSiderealTime(cal, longitude);
            double ha = tau.getHourAngleInHours(lst, starRaArray[i]);
            
            double alt = cc.convertRaDecToAlt(starDecArray[i], latitude, ha);
            //double az = cc.convertRaDecToAz(starDecArray[i], latitude, alt, ha);
            if (alt > ALT_LIMIT){
                visibleStarsRa.add(starRaArray[i]);
                visibleStarsDec.add(starDecArray[i]);
                visibleStarsLabelNames.add(starLabelNamesArray[i]);
                visibleStarsLabelRa.add(starLabelRaArray[i]);
                visibleStarsLabelDec.add(starLabelDecArray[i]);
            }
        }
    }

    public void clearVisibleStarLists(){
        visibleStarsDec.clear();
        visibleStarsLabelNames.clear();
        visibleStarsRa.clear();
        visibleStarsLabelRa.clear();
        visibleStarsLabelDec.clear();
    }

    public void startTracking() {
        
        long delay = 0;
        long period = 1000;
        timer = new Timer();
        //timer.scheduleAtFixedRate(new TimerTask() {

            //public void run() {
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
                GuiUpdater.window.objectsAlt.setText("" + ac.getDeg() + "\u00b0" + ac.getMin() + "'" + ac.getSec() + "\"");
                ac.setDegreeDecimal(az);
                ac.convertToDegMinSec();
                GuiUpdater.window.objectsAz.setText("" + ac.getDeg() + "\u00b0" + ac.getMin() + "'" + ac.getSec() + "\"");

                //Goto functions
                // TODO: Needs improovement. Firmware runs into oscilation with pid
                double revInMin = 360*60;
                double minPerTick = revInMin/MOUSE_TICKS_PER_REV;
                double altInMin = alt*60;
                int stepsToGo = (int)(altInMin/minPerTick);
                sendMessage("G"+String.valueOf(stepsToGo));
            //}
        //}, delay, period);
    }

    public void stopTracking() {
        timer.cancel();
    }

    /*
     * Sends a message to the Arduino in String form
     */
    public void sendMessage(String message) {
        serialComm.writeData(message.getBytes());
    }

    public void updatePortList() {
        serialComm = new Communicator();
        ArrayList<String> ports = serialComm.searchForPorts();
        GuiUpdater.updateCombobox(ports);
    }

    public void connectToSerial(String port) {
        serialComm.connect(port);
        if (serialComm.getConnected() == true) {
            if (serialComm.initIOStream() == true) {
                serialComm.initListener();
            }
        }
    }

    public void disconnectSerial() {
        serialComm.disconnect();
    }

    public void getTelescopeAltAz(String x, String y) {

        // Convert string to int and store it
        double xDouble = Double.parseDouble(x);
        double yDouble = Double.parseDouble(y);

        //convert to degrees based on the steps
        double xCoordinate = (xDouble / MOUSE_TICKS_PER_REV) * 360.0;
        double yCoordinate = (yDouble / MOUSE_TICKS_PER_REV) * 360.0;

        //DecimalFormat df = new DecimalFormat("#.####");
        //AngleConverter acAz = new AngleConverter(xCoordinate);
        //AngleConverter acAlt = new AngleConverter(yCoordinate);

        ac.setDegreeDecimal(yCoordinate);
        ac.convertToDegMinSec();
        GuiUpdater.window.scopeAlt.setText("" + ac.getDeg() + "\u00b0" + ac.getMin() + "'" + ac.getSec() + "\"");
        ac.setDegreeDecimal(xCoordinate);
        ac.convertToDegMinSec();
        GuiUpdater.window.scopeAz.setText("" + ac.getDeg() + "\u00b0" + ac.getMin() + "'" + ac.getSec() + "\"");

        scopeAlt = yCoordinate;
        scopeAz = xCoordinate;
    }

    public void calcStar1(double ra, double dec) {

        Calendar cal = Calendar.getInstance();
        //cal.set(2011, 10, 21, 21, 27, 56);// this is a test
        t = tau.getLocalDecimalTime(cal);

        star1DcEq = ccm.starDcEquatorial(ra, dec, t, t0);
        //star1DcEq = ccm.starDcEquatorial(0.1316, 29.038, 21.4655, 21.0);//this is a test
        star1DcTel = ccm.starDcTelescope(scopeAz, scopeAlt);
        //star1DcTel = ccm.starDcTelescope(99.25, 83.87);
    }

    public void calcStar2(double ra, double dec) {

        Calendar cal = Calendar.getInstance();
        t = tau.getLocalDecimalTime(cal);

        star2DcEq = ccm.starDcEquatorial(ra, dec, t, t0);
        //star2DcEq = ccm.starDcEquatorial(2.3625, 89.222, 21.6172, 21.0);//this is a test
        star2DcTel = ccm.starDcTelescope(scopeAz, scopeAlt);
        //star2DcTel = ccm.starDcTelescope(310.98, 35.04);
    }

    public void twoStarAlign() {
        //Calculate the 3rd star 2 direction cosines
        if (star1DcEq != null && star2DcEq != null) {
            star3DcEq = ccm.starVectorProduct(star1DcEq, star2DcEq);
        }
        if (star1DcTel != null && star2DcTel != null) {
            star3DcTel = ccm.starVectorProduct(star1DcTel, star2DcTel);
        }
        if (star3DcTel == null || star3DcEq == null) {
            GuiUpdater.updateLog("Error: Null Matrixes found. Check your input!!", Color.RED);
        }

        //Construct two 3X3 matrix for eq direction cosines 
        //and telescope direction cosines
        double star1DcEqD[] = star1DcEq.getColumnPackedCopy();
        double star2DcEqD[] = star2DcEq.getColumnPackedCopy();
        double star3DcEqD[] = star3DcEq.getColumnPackedCopy();
        double starsDcEqD[][] = {
            {star1DcEqD[0], star2DcEqD[0], star3DcEqD[0]},
            {star1DcEqD[1], star2DcEqD[1], star3DcEqD[1]},
            {star1DcEqD[2], star2DcEqD[2], star3DcEqD[2]}};
        Matrix eqDirectionCosines = new Matrix(starsDcEqD);

        double star1DcTelD[] = star1DcTel.getColumnPackedCopy();
        double star2DcTelD[] = star2DcTel.getColumnPackedCopy();
        double star3DcTelD[] = star3DcTel.getColumnPackedCopy();
        double starsDcTelD[][] = {
            {star1DcTelD[0], star2DcTelD[0], star3DcTelD[0]},
            {star1DcTelD[1], star2DcTelD[1], star3DcTelD[1]},
            {star1DcTelD[2], star2DcTelD[2], star3DcTelD[2]}};
        Matrix telDirectionCosines = new Matrix(starsDcTelD);

        T = telDirectionCosines.times(eqDirectionCosines.inverse());
        GuiUpdater.updateLog("Two Star Alignment completed successfuly!");
    }

    public String getArduinoMessage() {
        return arduinoMessage;
    }

    public void setArduinoMessage(String arduinoMessage) {
        this.arduinoMessage = arduinoMessage;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        Orchestrator.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        Orchestrator.longitude = longitude;
    }

    public double getDec() {
        return dec;
    }

    public void setDec(double dec) {
        Orchestrator.dec = dec;
    }

    public double getRa() {
        return ra;
    }

    public void setRa(double ra) {
        Orchestrator.ra = ra;
    }

    public List<Double> getVisibleStarsDec() {
        return visibleStarsDec;
    }

    public List<String> getVisibleStarsNames() {
        return visibleStarsLabelNames;
    }

    public List<Double> getVisibleStarsRa() {
        return visibleStarsRa;
    }
}
