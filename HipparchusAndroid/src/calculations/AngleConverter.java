package calculations;

public class AngleConverter {

	int deg;
	int min;
	int sec;

	double degreeDecimal;

	public AngleConverter() {
	}

	public void convertToDegMinSec() {

		int degree = (int) this.degreeDecimal;
		setDeg(degree);
		double minFractional = this.degreeDecimal - degree;
		double minDecimal = minFractional * 60;
		int min = (int) minDecimal;
		setMin(min);
		double secFractional = minDecimal - min;
		double secDecimal = secFractional * 60;
		int sec = (int) secDecimal;
		setSec(sec);
	}

	public double convertToDecimal(double deg, double min, double sec) {

		double decimalSeconds = sec / 60;
		double decimalMinutes = min + decimalSeconds;
		setDegreeDecimal(0d);

		int i = Double.compare(deg, 0.0);

		if (i < 0) {
			setDegreeDecimal(deg - (decimalMinutes / 60));
		}
		if (i > 0) {
			setDegreeDecimal(deg + (decimalMinutes / 60));
		}
		return this.degreeDecimal;
	}

	public int getDeg() {
		return deg;
	}

	public void setDeg(int deg) {
		this.deg = deg;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	public double getDegreeDecimal() {
		return degreeDecimal;
	}

	public void setDegreeDecimal(double degreeDecimal) {
		this.degreeDecimal = degreeDecimal;
	}
}
