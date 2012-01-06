/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calculations;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.mhuss.AstroLib.AstroDate;
import com.mhuss.AstroLib.DateOps;

/**
 *
 * @author mandim
 */
public class TimeAndUtils {

    public TimeAndUtils() {
    }

    public double convertToDecimal(double hour, double min, double sec) {

        double decimalSeconds = sec / 60;
        double decimalMinutes = min + decimalSeconds;
        double timeInDecimal = 0;
        int i = Double.compare(hour, 0.0);
        if (i < 0) {
            timeInDecimal = hour - (decimalMinutes / 60);
        }
        if (i > 0) {
            timeInDecimal = hour + (decimalMinutes / 60);
        }
        return timeInDecimal;
    }

    public double getLocalDecimalTime(Calendar cal) {

        cal.setTimeZone(TimeZone.getDefault());
        double hour = cal.get(Calendar.HOUR_OF_DAY);
        double min = cal.get(Calendar.MINUTE);
        double sec = cal.get(Calendar.SECOND);
        //double ms = cal.get(Calendar.MILLISECOND);
        //sec += (sec + ms/1000);
        return convertToDecimal(hour, min, sec);
    }

    public double getJulianDaysFromJ2000(Calendar cal) {

        AstroDate adJ2000 = new AstroDate();
        double julianDaysFromJ2000 = DateOps.dmyToDoubleDay(adJ2000);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        AstroDate adNow = new AstroDate(day, month, year, hour, min, sec);
        double daysFromNow = DateOps.dmyToDoubleDay(adNow);
        return daysFromNow - julianDaysFromJ2000;
    }

    public double getUTCTime(Calendar cal) {

        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        double hour = cal.get(Calendar.HOUR_OF_DAY);
        double min = cal.get(Calendar.MINUTE);
        double sec = cal.get(Calendar.SECOND);
        double ms = cal.get(Calendar.MILLISECOND);
        sec = sec + (ms/1000);
        return convertToDecimal(hour, min, sec);
    }

    public double getGreenwichSiderealTime(Calendar cal) {

        double ut = getUTCTime(cal);
        Calendar midnightCalendar = new GregorianCalendar();
        midnightCalendar.set(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DATE),
                0,
                0,
                0);
        double jd = DateOps.calendarToDoubleDay(midnightCalendar);
        double s = jd - 2451545.0;
        double t = s / 36525.0;
        double to = 6.697374558 + (2400.051336 * t) + (0.000025862 * (t * t));
        while (to < 0) {
            to = to + 24;
        }
        while (to > 24) {
            to = to - 24;
        }
        ut = ut * 1.002737909;
        double gst = ut + to;
        while (gst < 0) {
            gst = gst + 24;
        }
        while (gst > 24) {
            gst = gst - 24;
        }
        return gst;
    }

    public double getLocalSiderealTime(Calendar cal, double longitude) {

        double gst = getGreenwichSiderealTime(cal);
        double longitudeHours = longitude / 15;
        double lst = gst + longitudeHours;
        while (lst < 0) {
            lst = lst + 24;
        }
        while (lst > 24) {
            lst = lst - 24;
        }
        return lst;
    }

    public double getHourAngleInHours(double lst, double ra) {

        double hourAngle = lst - ra;
        if (hourAngle < 0) {
            hourAngle = hourAngle + 24;
        }
        return hourAngle;
    }
}
