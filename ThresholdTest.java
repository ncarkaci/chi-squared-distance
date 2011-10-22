import java.util.TreeMap;

public class ThresholdTest implements Comparable {
	private double score;
	private boolean isPositive; //if it is a virus = true
	
	public ThresholdTest(double value, boolean condition) {
		score = value;
		isPositive = condition;
	}
	
	public double getScore() {
		return score;
	}
	
	public boolean getIsVirusOrNot() {
		return isPositive;
	}

	@Override
	public int compareTo(Object otherObj) {
		ThresholdTest other = (ThresholdTest) otherObj;
		if (score > other.score)
			return 1;
		else if (score == other.score)
			return 0;
		else
			return -1;
	}
	
	
	
	
	/*
	private double false_positive_rate;
	private double false_negative_rate;
	private double threshold;
	private double FP;
	private double FN;
	
	public ThresholdTest(double line, double fp, double fn) {
		threshold = line;
		FP = fp;
		FN = fn;
	}
	
	public void setFalsePositiveRate(int fpos, int total) {
		double tpos = total - fpos;
		false_positive_rate = fpos / fpos + tpos;
	}
	
	public double computeSuccessRate() {
		double rate = 0.0;
		false_positive_rate = FP / 40;
		double true_positive_rate = 1.0 - false_positive_rate;
		double true_negative_rate = 1.0 - false_negative_rate;
		
		rate = FP;
		
		return rate;
	}
	*/

	
	

}
