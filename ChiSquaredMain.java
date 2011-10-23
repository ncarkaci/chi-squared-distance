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
		// The read in data from the file
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

		//System.out.println("================================================");
		//System.out.println("================================================");
		//System.out.println();

		test(idan_values, idar_values, true);
	}
	
	public static void anotherTest(ArrayList<ThresholdTest> dataset, int total_virus, int total_normal) {
		// Correctly identify all virus, so no false negative
		// TP + FN = total_virus
		int TP = 0, FN = 0;
		// Correctly identify all normal files, so no false positive
		// TN + FP = total_normal
		int TN = 0, FP = 0;
		double false_positive_rate = 0.0, true_positive_rate = 0.0;
		double success_rate = 0.0;
		
		System.out.println("*****************************************************");
		for(ThresholdTest current: dataset) {
			if (current.getIsVirusOrNot()) {
				TP++;
			} else {
				FP++;
			}
			FN = total_virus - TP;
			TN = total_normal - FP;
			false_positive_rate = (double) FP / (double) (FP + TN);
			true_positive_rate = (double) TP / (double) (TP + FN);
			success_rate = (double) (TP + TN) / (double) (TP + TN + FP + FN);
			current.setFalsePostiveRate(false_positive_rate);
			current.setTruePositiveRate(true_positive_rate);
			current.setSuccessRate(success_rate);
		}
		
		for (ThresholdTest tt: dataset) {
			System.out.println(tt.getTruePositiveRate() + "\t" + tt.getFalsePostiveRate());
			//System.out.format("False positive rate= %.5f \t True positive rate= %.5f \t Success rate= %.5f \n", tt.getFalsePostiveRate(), tt.getTruePositiveRate(), tt.getSuccessRate());
		}
		
	}

	public static void test(ArrayList<Double> idan_values, ArrayList<Double> idar_values, boolean reverse) {
		// Total of numbers for the normal and virus files
		int total_virus = idan_values.size();
		int total_normal = idar_values.size();

		// Build a list of combined scores from the idan and idar files
		ArrayList<ThresholdTest> dataset = new ArrayList<ThresholdTest>();
		for (double idan: idan_values) {
			dataset.add(new ThresholdTest(idan, true, reverse));
		}
		for (double idar: idar_values) {
			dataset.add(new ThresholdTest(idar, false, reverse));
		}
		// Sort the list in ascending order
		Collections.sort(dataset);

		//oldTest(dataset);
		anotherTest(dataset, total_virus, total_normal);
		
	}
	
	public static void oldTest(ArrayList<ThresholdTest> dataset) {
		
		int FP = 0, FN = 0, TP = 0, TN = 0;

		double false_positive_rate = 0.0, true_positive_rate = 0.0;
		double success_rate = 0.0;
		ThresholdTest threshold;
		double threshold_score;
		double max_success = 0.0;
		double best_threshold = dataset.get(0).getScore();
		
		for (int index = 0; index <= dataset.size(); index++) {
			if (index == dataset.size()) {
				threshold = dataset.get(dataset.size() - 1);
				threshold_score = threshold.getScore() + SMALL_CONSTANT;
			} else {
				threshold = dataset.get(index);
				threshold_score = threshold.getScore();
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
			
			if (max_success < success_rate) {
				max_success = success_rate;
				//best_threshold = compare_score;
			}
			
			//System.out.println(threshold.getScore() + " " + threshold.getIsVirusOrNot());
			//System.out.println(false_positive_rate + " ******** " + success_rate);
			//System.out.println("TP= " + TP + " FN= " + FN + " FP= " + FP + " TN= " + TN);
			//System.out.format("TP= %d \t FN= %d \t FP= %d \t TN= %d \n", TP, FN, FP, TN);
			System.out.format("False positive rate=\t %.5f \t True positive rate=\t %.5f \t Success rate=\t %.5f \n", false_positive_rate, true_positive_rate, success_rate);
		}
	}



}
