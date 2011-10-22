import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;


public class ChiSquaredMain {
	public static final double SMALL_CONSTANT = 0.00001;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String user_home = System.getProperty("user.home");
		String grand_type = "Morphed";
		String input = user_home + "/" + grand_type + "/" + Constants.INPUT_DATA + "/"; //DataSetOut0s10/";
		String train = user_home + "/" + grand_type + "/" + Constants.TRAINFILES + "/"; //DataSetOut0s10/";
		String output = user_home + "/" + grand_type + "/ChiValues/";
		String current_percent = null;
		ChiSquared pearson = new ChiSquared(input, train, output, grand_type);
		FileManager fm = new FileManager();
		double alpha = 0.0005; //5% ???
		double chi_squared = 0.0;

		for (int dc = 0; dc <= 0; dc += Constants.DEAD_CODE_STEP) {
			for (int sc = 10; sc <= 10; sc += Constants.SUBROUTINE_STEP) {
				current_percent = dc + "s" + sc;
				fm.createDir(output + "DataSetOut" + current_percent + "/");
				for (int current_fold = 0; current_fold < 1; current_fold++) {
					//pearson.startTest(current_percent, current_fold);
					//chi_squared = pearson.computeChiSquaredDistribution(alpha, current_percent, current_fold);
					//System.out.println(chi_squared);

					readScoresValues(current_percent, current_fold);
				}
			}
		}

	}

	public static void readScoresValues(String current_percent, int current_fold) {
		String output = System.getProperty("user.home") + "/" + "Morphed" + "/ChiValues/";
		String filename = output + "DataSetOut" + current_percent + "/" + "Morphed" + current_percent + "f" + current_fold + ".chi";
		FileManager fm = new FileManager();
		ArrayList<String> data = fm.readFile(filename);
		String line = null;
		double idan_min = 0.0, idan_max = 0.0;
		double idar_min = 0.0, idar_max = 0.0;
		ArrayList<Double> idan_values = new ArrayList<Double>();
		ArrayList<Double> idar_values = new ArrayList<Double>();

		double token_value = 0.0;

		for (int i = 0; i < data.size(); i++) {
			String[] tokens = data.get(i).split("\\s");
			token_value = Double.parseDouble(tokens[1]);
			if (tokens[0].startsWith("IDAN")) {
				idan_values.add(token_value);
			} else {
				idar_values.add(token_value);
			}

		}
		ArrayList<Double> sort_idan = new ArrayList<Double>();
		/*
		int l = 0;
		System.out.println("IDAN");
		for (double v: idan_values) {
			System.out.println(l + " " + v);
			l++;
		}*/

		//Collections.sort(idan_values);
		//Collections.sort(idar_values);
		/*int ll = 0;
		System.out.println("IDAR");
		for (double v: idan_values) {
			System.out.println(ll + " " + v);
			ll++;
		}*/


		//idan_min = getMinValue(idan_values);
		//idan_max = getMaxValue(idan_values);

		System.out.println("================================================");
		System.out.format("%10s%20s\n", "IDAN" , "IDAR");
		System.out.println(idan_values.get(0) + " " + idar_values.get(0));
		System.out.println(idan_values.get(idan_values.size()-1) + " " + idar_values.get(idar_values.size()-1));
		int count = countFalsePositives(idar_values, idan_values.get(0));
		//count = countFalseNegatives(idar_values, idan_max);
		System.out.println("False Positives: " + count);
		int count1 = countFalseNegatives(idan_values, idar_values.get(0));
		//int count1 = countFalsePositives(idar_values, idan_values.get(idan_values.size()-1));
		System.out.println("False Negatives: " + count1);
		System.out.println("================================================");
		System.out.println();
		//System.out.println();
		//findThreshold(idan_values, idar_values);
		//findFalseNegativeThreshold(idan_values, idar_values);
		//findIt(idan_values, idar_values);
		test(idan_values, idar_values);
		/*
		int l = 0;
		System.out.println("IDAN");
		for (double v: idan_values) {
			System.out.println(l + " " + v);
			l++;
		}
		 */
	}

	public static void test(ArrayList<Double> idan_values, ArrayList<Double> idar_values) {
		ArrayList<ThresholdTest> dataset = new ArrayList<ThresholdTest>();
		for (double idan: idan_values) {
			dataset.add(new ThresholdTest(idan, true));
		}
		for (double idar: idar_values) {
			dataset.add(new ThresholdTest(idar, false));
		}

		Collections.sort(dataset);

		int FP = 0, FN = 0, TP = 0, TN = 0;
		int total_virus = 0, total_normal = 0;
		double false_positive_rate = 0.0, true_positive_rate = 0.0;
		double success_rate = 0.0;
		ThresholdTest threshold;
		double threshold_score;

		for (int index = 0; index <= dataset.size(); index++) {
			if (index == dataset.size()) {
				threshold = dataset.get(dataset.size() - 1);
				threshold_score = threshold.getScore() + SMALL_CONSTANT;
			} else {
				threshold = dataset.get(index);
				threshold_score = threshold.getScore();
				boolean isVirus = threshold.getIsVirusOrNot();
				// Get the total number of virus and normal scores
				if (isVirus)
					total_virus++;
				else
					total_normal++;
			}

			FP = 0; FN = 0; TP = 0; TN = 0;
			for (ThresholdTest tt: dataset) {
				double compare_score = tt.getScore();
				boolean isVoN = tt.getIsVirusOrNot();

				// This point is above the threshold
				if ( threshold_score <= compare_score) {
					// if it is a virus, then it is a false positive
					// A virus is identified as normal
					if (isVoN) {
						FN++; 
					} else {
						TN++; // correctly identify a normal
					}
				} else {
					if (isVoN) {
						TP++; // correctly identify a virus
					} else {
						FP++;
					}
				}
			}
			false_positive_rate = FP / (double) (FP + TN);
			true_positive_rate = TP / (double) (TP + FN);
			success_rate = (double)(TP + TN) / (double)(TP + TN + FP + FN);

			System.out.println(threshold.getScore() + " " + threshold.getIsVirusOrNot());
			System.out.println(false_positive_rate + " ******** " + success_rate);
			//System.out.println("TP= " + TP + " FN= " + FN + " FP= " + FP + " TN= " + TN);
		}
	}

	public static void findIt(ArrayList<Double> idan_values, ArrayList<Double> idar_values) {
		double threshold_point;
		int total = idan_values.size() + idar_values.size();

		for (int i = 0; i < idan_values.size(); i++) {
			int count = countFalsePositives(idar_values, idan_values.get(i));
			int count2 = countFalseNegatives(idan_values, idan_values.get(i));
			System.out.println(i + " IDAN: FP " + count + " FN " + (count2 - 1) + " " + idan_values.get(i));
		}
		System.out.println("================================================");
		for (int i = 0; i < idar_values.size(); i++) {
			int count = countFalsePositives(idar_values, idar_values.get(i));
			int count2 = countFalseNegatives(idan_values, idar_values.get(i));
			System.out.println(i + " IDAR: FP " + (count - 1) + " FN " + count2 + " " + idar_values.get(i));
			// 41
		}

	}

	public static void findFalseNegativeThreshold(ArrayList<Double> idan_values, ArrayList<Double> idar_values) {
		ArrayList<Double> numbers = new ArrayList<Double>();
		int count = 0;

		for (int i = 0; i < idar_values.size(); i++) {
			double value = idar_values.get(i);
			count = countFalseNegatives(idan_values, value);
			numbers.add(count + 0.0);
		}

		for (Double c: numbers) 
			System.out.println(c);
	}

	//false negative
	public static void findThreshold(ArrayList<Double> idan_values, ArrayList<Double> idar_values) {
		ArrayList<Double> numbers = new ArrayList<Double>();
		int count = 0;

		for (int i = 0; i < idan_values.size(); i++) {
			double value = idan_values.get(i);
			count = countFalsePositives(idar_values, value);
			numbers.add(count + 0.0);
		}

		for (double n: numbers) {
			System.out.println(n);
		}
	}

	/**
	 * Falsely identifies a normal file as virus file.
	 * @param data
	 * @param value
	 * @return
	 */
	public static int countFalsePositives(ArrayList<Double> idar_data, double max_idan_value) {
		int count = 0;

		for (double current: idar_data) {
			if (current <= max_idan_value) {
				count++;
			} 
		}

		return count;
	}

	/**
	 * Falsely classifies data from the virus files as normal.
	 * @param idan_data
	 * @param min_idar_value
	 * @return
	 */
	public static int countFalseNegatives(ArrayList<Double> idan_data, double min_idar_value) {
		int count = 0;

		for (double current: idan_data) {
			if (current >= min_idar_value)
				count++;
		}

		return count;
	}

	public static double getMinValue(ArrayList<Double> data) {
		double min = Double.MAX_VALUE;

		for (double value: data) {
			if (value < min)
				min = value;
		}

		return min;
	}

	public static double getMaxValue(ArrayList<Double> data) {
		double max = Double.MIN_VALUE;

		for (double value: data) {
			if (value > max)
				max = value;
		}
		return max;
	}

}
