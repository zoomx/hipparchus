package calculations;

import Jama.Matrix;

/**
 * @author mandim
 *
 */
public class CoordinatesConverterMatrix {

    public static final double K = 1.002737908;

    public CoordinatesConverterMatrix() {
        super();
    }

    /**
     * Calculates direction consines from a star taking
     * the telescope coordinates and returns a Matrix object
     *
     * @param az is the azimuth in Decimal degrees of the telescope
     * @param alt is the altitude in decimal degrees of the telescope
     * @return Matrix
     */
    public Matrix starDcTelescope(double az, double alt) {

        az = Math.toRadians(az);
        alt = Math.toRadians(alt);
        double l = Math.cos(alt) * Math.cos(az);
        double m = Math.cos(alt) * Math.sin(az);
        double n = Math.sin(alt);

        double lmn[] = {l, m, n};
        Matrix starDcTelescope = new Matrix(lmn, 3);

        return starDcTelescope;
    }

    /**
     * Calculates direction consines from ra star taking
     * the equatorial coordinates of the object and returns ra Matrix object
     * t is the current time and t0 is the initial time
     *
     * @param ra is the right ascension in Decimal hours (RA) of the star
     * @param dec is the declination in Decimal degrees (Dec) of the star
     * @param t is the current time in decimal
     * @param t0 is the initial time
     * @return Matrix
     */
    public Matrix starDcEquatorial(double ra, double dec, double t, double t0) {

        ra = Math.toRadians(ra * 15);
        dec = Math.toRadians(dec);
        t = Math.toRadians(t * 15);
        t0 = Math.toRadians(t0 * 15);
        double L = Math.cos(dec) * Math.cos(ra - (K * (t - t0)));
        double M = Math.cos(dec) * Math.sin(ra - (K * (t - t0)));
        double N = Math.sin(dec);

        double LMN[] = {L, M, N};
        Matrix starDcEquatorial = new Matrix(LMN, 3);

        return starDcEquatorial;
    }

    public Matrix starVectorProduct(Matrix star1, Matrix star2) {

        double l1 = star1.get(0, 0);
        double m1 = star1.get(1, 0);
        double n1 = star1.get(2, 0);

        double l2 = star2.get(0, 0);
        double m2 = star2.get(1, 0);
        double n2 = star2.get(2, 0);

        double denominator = Math.pow((m1*n2 - n1*m2), 2)
                + Math.pow((n1*l2 - l1*n2), 2)
                + Math.pow((l1*m2 - m1*l2), 2);
        double fraction = 1/Math.sqrt(denominator);

        double l3m3n3[] = {(m1*n2 - n1*m2), (n1*l2 - l1*n2), (l1*m2 - m1*l2)};
        Matrix starVectorProduct = new Matrix(l3m3n3, 3);
        starVectorProduct.timesEquals(fraction);

        return starVectorProduct;
    }
}
