import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;


public class ChiSquaredMain {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String user_home = System.getProperty("user.home");
		String grand_type = "Morphed";
		//String grand_type = "Unmorphed";
		//String grand_type = "Normal";
		String input = user_home + "/" + grand_type + "/" + Constants.INPUT_DATA + "/"; //DataSetOut0s10/";
		String train = user_home + "/" + grand_type + "/" + Constants.TRAINFILES + "/"; //DataSetOut0s10/";
		String output = user_home + "/" + grand_type + "/ChiValues/";
		String current_percent = null;
		
		// For performing the Chi-Squared test
		ChiSquared pearson = new ChiSquared(input, train, output, grand_type);
		FileManager fm = new FileManager();
		double alpha = 0.0005; //5% ???
		double chi_squared = 0.0;

		for (int dc = 0; dc <= 0; dc += Constants.DEAD_CODE_STEP) {
			for (int sc = 10; sc <= 10; sc += Constants.SUBROUTINE_STEP) {
				current_percent = dc + "s" + sc;
				// create folder for the chi-squared values
				fm.createDir(output + "DataSetOut" + current_percent + "/");
				
				for (int current_fold = 0; current_fold < 1; current_fold++) {
					// Compute Pearson's Chi-Squared Test
					pearson.startPearsonTest(current_percent, current_fold);
					
					// Compute the Chi-Squared Distribution
					//chi_squared = pearson.computeChiSquaredDistribution(alpha, current_percent, current_fold);
					//System.out.println(chi_squared);
				}
			}
		}

	}

}
