package calculations;

import java.lang.Math;

/**
 * This class implements all the basic methods in order to
 * convert from RA Dec values into Alt Az. The method used
 * is the spherical trigonometry.
 * 
 * @author mandim
 *
 */
public class SimpleCoordinatesConverter {

    public SimpleCoordinatesConverter() {
    }

    public double convertRaDecToAlt(double dec, double lat, double ha) {

        double haDeg = ha * 15;
        double sinA =
                Math.sin(Math.toRadians(dec))
                * Math.sin(Math.toRadians(lat))
                + Math.cos(Math.toRadians(dec))
                * Math.cos(Math.toRadians(lat))
                * Math.cos(Math.toRadians(haDeg));
        double a = Math.asin(sinA);
        return Math.toDegrees(a);
    }

    public double convertRaDecToAz(double dec, double lat, double alt, double ha) {

        double cosA =
                (Math.sin(Math.toRadians(dec)) 
                - Math.sin(Math.toRadians(lat))
                * Math.sin(Math.toRadians(alt)))
                / (Math.cos(Math.toRadians(lat))
                * Math.cos(Math.toRadians(alt)));
        double ai = Math.acos(cosA) * 180 / Math.PI;
        double sinH = Math.sin(Math.toRadians(ha * 15));
        double az = 0;
        if (sinH < 0) {
            az = ai;
        }
        if (sinH > 0) {
            az = 360 - ai;
        }
        return az;
    }

    public double convertAltAzToDec(double alt, double lat, double az) {

        double sind =
                Math.sin(Math.toRadians(alt)) * Math.sin(Math.toRadians(lat))
                + Math.cos(Math.toRadians(alt)) * Math.cos(Math.toRadians(lat))
                * Math.cos(Math.toRadians(az));
        double d = Math.asin(sind);
        return Math.toDegrees(d);
    }

    public double convertAltAzToRa(double alt, double lat, double dec, double az, double lst) {

        double cosH =
                (Math.sin(Math.toRadians(alt)) - Math.sin(Math.toRadians(lat)) * Math.sin(Math.toRadians(dec)))
                / (Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(dec)));
        double hi = Math.acos(cosH) * 180 / Math.PI;
        double sinA = Math.sin(Math.toRadians(az));
        double ha = 0;
        if (sinA < 0) {
            ha = hi;
        }
        if (sinA > 0) {
            ha = 360 - hi;
        }

        double ra = lst - (ha / 15);
        return ra;
    }
}