import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

public class ChiSquared {
	private String input_path;
	private String train_path;
	private String output_path;
	private String grand_type;

	public ChiSquared(String input, String train, String output, String type) {
		input_path = input;
		train_path = train;
		output_path = output;
		grand_type = type;
	}

	public void startTest(String current_percent, int current_fold) {
		String match = current_percent + "f" + current_fold; //"0s10f0";
		String input = input_path + "DataSetOut" + current_percent + "/"; //"/Users/annietoderici/Morphed/Input Data/DataSetOut0s10/";
		String train = train_path + "DataSetOut" + current_percent + "/"; //"/Users/annietoderici/Morphed/TrainFiles/DataSetOut0s10/";
		String output = output_path + "DataSetOut" + current_percent + "/";
		FileManager fm = new FileManager();
		//fm.createDir(output);

		ArrayList<String> raw_indices = new ArrayList<String>();
		HashMap<Integer, Double> expected_frequency = new HashMap<Integer, Double>();
		raw_indices = fm.readFile(train + "IDAN" + match + ".in");
		expected_frequency = getFrequencyCounts(raw_indices);

		ArrayList<String> titles = fm.getListOfFilenamesFromDir(input);
		ArrayList<String> chi_values = new ArrayList<String>();
		int k = 0;
		for (String current: titles) {
			if (current.contains(match)) {
				//String current = titles.get(0);
				ArrayList<String> observed_asm = fm.readFile(input + current);
				HashMap<Integer, Double> observed_frequency = getFrequencyCounts(observed_asm);

				double chi1 = computePearsonChiSquared(observed_frequency, expected_frequency);
				chi_values.add(current + "\t" + chi1);
			}
		}
		fm.writeFile(chi_values, output + grand_type + match + ".chi");

	}

	/**
	 * Compute the Pearson's Chi-Squared test values for two set of data.
	 * @param expected The expected values. 
	 * @param observed The observed values.
	 * @return The Pearson's Chi-Squared value.
	 */
	public double computePearsonChiSquared(HashMap<Integer, Double> observed, HashMap<Integer, Double> expected) {
		double chi_squared = 0.0, sum = 0.0;
		double expected_value = 0.0, observed_value = 0.0;
		int expected_key = 0;

		for (Map.Entry<Integer, Double> expected_pair: expected.entrySet()) {
			expected_value = expected_pair.getValue();
			expected_key = expected_pair.getKey();
			if (observed.containsKey(expected_key)) {
				observed_value = observed.get(expected_key);
			} else {
				observed_value = 0.0;  // (0 - E)(0 - E) / E = E
			}
			sum = (observed_value - expected_value) * (observed_value - expected_value) / expected_value;
			chi_squared += sum;
		}
		return chi_squared;
	}

	/**
	 * Normalize the values to sum to 1.
	 * @param data Values to be normalized.
	 * @param total Total number of all values from this data.
	 * @return The normalized values for the given data.
	 */
	public HashMap<Integer, Double> normalizeValues(HashMap<String, Double> data, int total) {
		HashMap<Integer, Double> norm = new HashMap<Integer, Double>();
		double value = 0.0, norm_value = 0.0;
		int key = 0;

		for (Map.Entry<String, Double> pair: data.entrySet()) {
			key = Integer.parseInt(pair.getKey());
			value = pair.getValue();
			norm_value = value / total;
			norm.put(key, norm_value);
		}
		return norm;
	}

	/**
	 * Get the frequency counts for a given data of indices.
	 * @param raw_indices All of the indices from a file.
	 * @return The frequency counts in normalized values.
	 */
	public HashMap<Integer, Double> getFrequencyCounts(ArrayList<String> raw_indices) {
		HashMap<String, Double> frequency = new HashMap<String, Double>();
		double count = 0.0;
		int total = 0;
		String index = null;

		// First line is the total number of instructions in this file
		int total_lines = Integer.parseInt(raw_indices.get(0));
		// The opcode indices start on line 1
		for (int i = 1; i < raw_indices.size(); i++) {
			index = raw_indices.get(i);
			if (frequency.containsKey(index)) {
				count = frequency.get(index);
				count += 1.0;
				frequency.put(index, count);
			} else {
				frequency.put(index, 1.0);
			}
			total++;
		}

		// Make sure the number of lines matches
		if (total != total_lines) {
			System.err.println("Number of instructions read in from the in file is incorrect!");
			System.exit(-1);
		}

		// Normalize the values
		HashMap<Integer, Double> norm = normalizeValues(frequency, total_lines); 
		return norm;
	}

	/* 
	 * Gamma function is obtained from 
	 * http://www.cs.princeton.edu/introcs/91float/Gamma.java.html
	 */
	private static double logGamma(double x) {
		double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
		double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
		+ 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
		+  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
		return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
	}

	private static double gamma(double x) { return Math.exp(logGamma(x)); }

	/*
	 * @param x is correspond to the desired D squared 
	 * @param k is the degrees of freedom (# of distinct instructions - 1)
	 */
	private double ChiSquared(double x, int k) {
		double chi = 0;

		chi = (Math.pow(x, ((double)k/2)-1) * Math.exp(-x/2)) / (Math.pow(2, (double)k/2) * gamma((double)k/2));

		return chi;
	}

	public double computeChiSquaredDistribution(double alpha, 
			String current_percent, int current_fold) {
		String train = train_path + "DataSetOut" + current_percent + "/";
		String afile = "IDAN" + current_percent + "f" + current_fold + ".alphabet";
		double chi = 0.0;
		int degree = 39;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(train + afile));
			String line = null;
			if ((line = reader.readLine()) != null) {
				degree = Integer.parseInt(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		chi = getD4Error(alpha, degree - 1);

		return chi;
	}

	/**
	 * 
	 * @param alpha
	 * @param k The degree of freedom.
	 * @return
	 */
	public double getD4Error(double alpha, int k) {
		double sum = 0, x = 0, delta = 0.01;

		while (sum < alpha) {
			sum += ChiSquared(x, k);
			x += delta;
		}
		return x - delta;
	}
}
