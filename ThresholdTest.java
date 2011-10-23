import java.util.TreeMap;

public class ThresholdTest implements Comparable<ThresholdTest> {
	private double score;
	private boolean isPositive; //if it is a virus = true
	private boolean reversed; // if true, sort it in reversed order (descending order)
	private double successRate;
	private double falsePositiveRate;
	private double truePositiveRate;
	
	public ThresholdTest(double value, boolean condition, boolean reversed_sort) {
		score = value;
		isPositive = condition;
		reversed = reversed_sort;
	}
	
	/**
	 * Get the score for this file.
	 * @return the score
	 */
	public double getScore() {
		return score;
	}
	
	/**
	 * This return the status of this score, whether it is a virus or
	 * normal. Positive for virus, and negative for normal file.
	 * @return true for virus, false for normal file
	 */
	public boolean getIsVirusOrNot() {
		return isPositive;
	}
	
	public double getSuccessRate() {
		return successRate;
	}
	
	public double getFalsePostiveRate() {
		return falsePositiveRate;
	}
	
	public double getTruePositiveRate() {
		return truePositiveRate;
	}
	
	public void setSuccessRate(double rate) {
		successRate = rate;
	}
	
	public void setFalsePostiveRate(double rate) {
		falsePositiveRate = rate;
	}
	
	public void setTruePositiveRate(double rate) {
		truePositiveRate = rate;
	}

	@Override
	public int compareTo(ThresholdTest other) {
		if (score > other.score) {
			if (reversed)
				return -1;
			else
				return 1;
		} else if (score == other.score) {
			return 0;
		} else {
			if (reversed)
				return 1;
			else
				return -1;
		}
	}
	
}
