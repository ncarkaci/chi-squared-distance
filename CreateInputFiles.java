import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateInputFiles {

	protected FileManager fm;
	protected ProcessAsm asm;
	protected int total_size;// = Constants.VIRUS_FILE_SIZE;
	protected int num_of_fold;// = Constants.FOLD;
	protected int size_per_set;
	protected String user_home;
	protected String grand_type = "Morphed";
	//String current_percent = null;
	// The path to the list of file indices for cross-validation used
	protected String cv_indices_path = user_home + "/Experiment/Lists/" + "CrossValidationList"; //Constants.INDICES_PATH
	protected ArrayList<ArrayList<Integer>> file_indices = new ArrayList<ArrayList<Integer>>();

	public CreateInputFiles(int total, int fold) {
		fm = new FileManager();
		asm = new ProcessAsm(fm.readFile(Constants.OPCODE_DATABASE));
		total_size = total;
		num_of_fold = fold;
		size_per_set = total / fold;
		user_home = System.getProperty("user.home");
	}

	/************************************************************************
	 * Create the cross-validation list of training and testing files.
	 * With n-fold cross-validation, the first n-1 fold of files indices
	 * will be used for training, and the last fold for testing files
	 * indices.
	 * @return The list of files indices assigned for training and testing.
	 ***********************************************************************/
	public ArrayList<ArrayList<Integer>> getCrossValidationList() {
		file_indices = asm.createCrossValidationList(total_size, num_of_fold);
		return file_indices;
	}

	/************************************************************************
	 * Create the files that contain the list of indices of training
	 * and testing files for all n-fold of cross-validation.
	 * @param location The path to where the files will be saved to. 
	 * 			If not given a path, it will save to a defaulted path.
	 ***********************************************************************/
	public void createCrossValidationIndicesLists(String location) {
		if (location.isEmpty()) {
			cv_indices_path = user_home + "/Experiment/Lists/";
		} else {
			cv_indices_path = location;
		}
		fm.createDir(cv_indices_path);
		cv_indices_path = cv_indices_path  + "CrossValidationList";
		int fold = 0;

		for (ArrayList<Integer> data: file_indices) {
			fm.writeInFile(data, cv_indices_path + fold + "_" + total_size + ".log");
			fold++;
		} 
	}

	/************************************************************************
	 * Perform the n-fold cross-validation. 
	 * First step is to get the indices for the current fold.
	 * Then start the process of creating all the input files.
	 * @param current_percent The current dead code and subroutine
	 * 			code for this experiment.
	 ***********************************************************************/
	public void performCrossValidation(String current_percent, String cvfilename, String indicator) {
		String filename = cv_indices_path; 
		//String name = null;
		grand_type = indicator; // either be Morphed, Unmorphed, or Normal

		for (int fold = 0; fold < num_of_fold; fold++) {
			filename = cvfilename + fold + "_" + total_size + ".log";
			// Get the cross-validation indices - first 160 are for training, 40 for testing
			//System.out.println(filename);
			ArrayList<String> perm_list = fm.readFile(filename);
			//System.out.println("Current fold is " + fold);
			
			// Create directories for the data
			createInputFolders(current_percent);
			
			// Create all the necessary input files
			createInputFiles(perm_list, current_percent, fold);

		}
	}
	
	/**
	 * Create the folders for the training and testing input files.
	 * @param current_percent The current morphed percent of combinations
	 */
	private void createInputFolders(String current_percent) {
		String training_folder = user_home + "/";
		
		if (grand_type.equalsIgnoreCase("Morphed")) {
			training_folder = user_home + "/" + grand_type + "/TrainFiles/" + Constants.SUBFOLDER + current_percent + "/";
		} else { //for Unmorphed and Normal variants
			training_folder = user_home + "/" + grand_type + "/TrainFiles/" + Constants.SUBFOLDER + "0s0/";
		} 
		fm.createDir(training_folder);
		String testing_folder = user_home + "/" + grand_type + "/Input Data/" + Constants.SUBFOLDER + current_percent + "/";
		fm.createDir(testing_folder);
	}

	/************************************************************************
	 * 
	 * @param perm_list
	 * @param current_percent The current morphed percentage for dead 
	 * code and subroutine code.
	 * @param current_fold
	 ***********************************************************************/
	public void createInputFiles(ArrayList<String> perm_list, String current_percent, int current_fold) {
		ArrayList<String> raw_asm = new ArrayList<String>(); 
		ArrayList<String> legit_opcode = new ArrayList<String>(); 
		System.out.println("Start of the createInputFiles: " + current_fold);
		
		// Creating the training file, testing files, and alphabet file for a fold
		raw_asm.addAll(getTrainingAsm(current_percent, perm_list));

		// Create the unique alphabet for this fold
		//legit_opcode.addAll(asm.getPossibleOpcodeFromAsm(raw_asm));
		legit_opcode.addAll(asm.getPossibleOpcodeUsingDatabase(raw_asm));
		raw_asm.clear();
		ArrayList<String> alpha = asm.getUniqueOpcode(legit_opcode);
		createAlphabetFile(alpha, current_percent, current_fold);
		
		// Important opcode with their corresponding indices
		HashMap<String, Integer> dictionary = asm.getIndicesDictionary(alpha);

		// Get the indices for the training file
		ArrayList<Integer> indices = asm.getInFiles(legit_opcode, dictionary);
		createTrainingFile(indices, current_percent, current_fold);
		indices.clear();

		// The partial path for the testing purposes
		String testing_input_part = null;
		String testing_output_part = null;

		int test_start = (int) Math.ceil(total_size * 0.8);
		int test_end = size_per_set;
		
		// Create the testing files using the remaining dataset from training.
		if (grand_type.equalsIgnoreCase("Normal")) {
			testing_input_part = Constants.PATH_NAME + Constants.SUBFOLDER_OTHERSETS + "/IDAR";
			testing_output_part = Constants.SUBFOLDER + current_percent + "/IDAR" + current_percent + "f" + current_fold;
		} else {
			testing_input_part = Constants.PATH_NAME + Constants.SUBFOLDER + current_percent + "/IDAN";
			testing_output_part = Constants.SUBFOLDER + current_percent + "/IDAN" + current_percent + "f" + current_fold;
		}
		//createTestingFile(train_start, train_end, current_percent, current_fold, dictionary, testing_asm_path, "IDAN");
		createTestingFile(test_start, test_end, testing_input_part, testing_output_part, perm_list, dictionary);

		// Create the testing files for the comparing dataset.
		ArrayList<String> other_list = new ArrayList<String>();
		if (grand_type.equalsIgnoreCase("Normal")) {
			String other_list_fname = user_home + "/Experiment/Lists/" + "CrossValidationList" + current_fold + "_200.log";
			other_list = fm.readFile(other_list_fname);
			int virus_total_size = 200;
			// Only use one fold of the dataset for the testing files.
			test_start = (int) Math.ceil(virus_total_size * 0.8);
			test_end = virus_total_size / num_of_fold; //40
			
			testing_input_part = Constants.PATH_NAME + Constants.SUBFOLDER + current_percent 
			+ "/IDAN";
			testing_output_part = Constants.SUBFOLDER + 
			current_percent + "/" + "IDAN" + current_percent + "f" + current_fold;
			
			createTestingFile(test_start, test_end, testing_input_part, testing_output_part, other_list, dictionary);
		} else { 
			// for Morphed and Unmorphed, all data from the normal files will be used
			test_start = 0;
			test_end = 41;
			for (int ol = test_start; ol < test_end; ol++)
				other_list.add(String.valueOf(ol));
			
			testing_input_part = Constants.PATH_NAME + Constants.SUBFOLDER_OTHERSETS + "/IDAR";
			testing_output_part = Constants.SUBFOLDER + current_percent + "/IDAR" + current_percent + "f" + current_fold;		
			createTestingFile(test_start, test_end, testing_input_part, testing_output_part, other_list, dictionary);
		}
	}

	/**
	 * Create the testing files.
	 * @param start
	 * @param end
	 * @param testing_path
	 * @param output
	 * @param perm_list
	 * @param dictionary
	 */
	public void createTestingFile(int start, int end, String testing_path, 
			String output, ArrayList<String> perm_list, HashMap<String, Integer> dictionary) {
		String asm_filename = null; int index = 0;
		// Create testing files using the remaining virus files
		//for (int i = total_size - size_per_set; i < total_size; i++) {
		for (int i = start; i < start + end; i++) {
			if (grand_type.equalsIgnoreCase("Normal")) {
				asm_filename = testing_path + perm_list.get(i) + ".asm"; 
				index = Integer.parseInt(perm_list.get(i));
			} else {
				//asm_filename = testing_path + i + ".asm"; index = i;
				asm_filename = testing_path + perm_list.get(i) + ".asm"; 
				index = Integer.parseInt(perm_list.get(i));
			}
			//String asm_filename = Constants.PATH_NAME + Constants.SUBFOLDER + current_percent 
			//+ "/IDAN" + perm_list.get(i) + ".asm";
			String tfname = user_home + "/" + grand_type + "/Input Data/" + output + 
			"_" + index + ".in";

			System.out.println("asm_filename is " + asm_filename + " tfname is " + tfname);
			createOneTestingFile(asm_filename, tfname, dictionary);
		} 
	}

	public void createOneTestingFile(String input_filename, 
			String output_filename, HashMap<String, Integer> dictionary) {
		// Store the data read in from this assembly file
		ArrayList<String> temp_asm = new ArrayList<String>();
		temp_asm.addAll(fm.readFile(input_filename));

		// Process the data to only save the legitimate instructions
		ArrayList<String> valid_asm = new ArrayList<String>();
		//valid_asm.addAll(asm.getPossibleOpcodeFromAsm(temp_asm));
		valid_asm.addAll(asm.getPossibleOpcodeUsingDatabase(temp_asm));
		temp_asm.clear();

		// Use the dictionary to get the corresponding indices for the
		// instructions
		ArrayList<Integer> temp_indices = new ArrayList<Integer>();
		temp_indices = asm.getInFiles(valid_asm, dictionary);
		valid_asm.clear();

		// Add the total number of indices for this testing file to the 
		// first line of the input file
		int tsize = temp_indices.size();
		temp_indices.add(0, tsize); 

		// Write the indices into a file
		fm.writeInFile(temp_indices, output_filename);	
	}

	/**
	 * Read the files that will be used to create the training file.
	 * The training file in this test is based on 5-fold cross-validation,
	 * therefore the 80% of the dataset will be used for building a
	 * training file. 
	 * @param current_percent The current morphed percentage for dead 
	 * code and subroutine code.
	 * @param perm_list The list that contain the dataset's indices.
	 * @return All the instructions read from the 80% dataset.
	 */
	public ArrayList<String> getTrainingAsm(String current_percent, ArrayList<String> perm_list) {
		ArrayList<String> raw_asm = new ArrayList<String>(); 
		int num_of_train_files = total_size - size_per_set;

		// Creating the training file, testing files, and alphabet file for a fold
		String fname = null;
		if (grand_type.equalsIgnoreCase("Morphed")) {
			for (int i = 0; i < num_of_train_files; i++) {
				fname = Constants.PATH_NAME + Constants.SUBFOLDER + current_percent + "/IDAN" + perm_list.get(i) + ".asm";
				raw_asm.addAll(fm.readFile(fname));
				System.out.println("getTrainingAsm: " + fname);
			}
		} else if (grand_type.equalsIgnoreCase("Unmorphed")) {
			for (int i = 0; i < num_of_train_files; i++) {
				fname = Constants.PATH_NAME + Constants.SUBFOLDER + "0s0/IDAN" + perm_list.get(i) + ".asm";
				raw_asm.addAll(fm.readFile(fname));
			}
		} else { // for Normal
			for (int i = 0; i < num_of_train_files; i++) {
				System.out.println("In getTrainingAsm " + total_size + " " + size_per_set + " list is : " + perm_list.get(i));
				fname = Constants.PATH_NAME + Constants.SUBFOLDER_OTHERSETS + "/IDAR" + perm_list.get(i) + ".asm";
				raw_asm.addAll(fm.readFile(fname));
			}
		}
		//System.out.println("getTrainingAsm: " + fname + " size= " + num_of_train_files);
		return raw_asm;
	}

	/**
	 * Create the training alphabet file with the unique alphabet 
	 * given. If the file is already existed, it will not overwrite 
	 * the file. 
	 * Please make sure to delete the file if you want new data.
	 * @param alpha The unique alphabet.
	 * @param current_percent The current morphed percentage for dead 
	 * code and subroutine code.
	 * @param current_fold The current fold for the cross-validation 
	 * experiment.
	 */
	public void createAlphabetFile(ArrayList<String> alpha, 
			String current_percent, int current_fold) {
		int alpha_size = alpha.size();
		String afilename = null;

		if (grand_type.equalsIgnoreCase("Morphed")) {
			afilename = user_home + "/" + grand_type + "/TrainFiles/" + Constants.SUBFOLDER 
				+ current_percent + "/IDAN" + current_percent + "f" 
				+ current_fold + ".alphabet";
		} else if (grand_type.equalsIgnoreCase("Unmorphed")) {
			afilename = user_home + "/" + grand_type + "/TrainFiles/" + Constants.SUBFOLDER 
				+ "0s0/IDAN0s0f" + current_fold + ".alphabet";
		} else { // for Normal
			afilename = user_home + "/" + grand_type + "/TrainFiles/" + Constants.SUBFOLDER 
				+ "0s0/IDAR0s0f" + current_fold + ".alphabet";
		}
		if (new File(afilename).exists()) // it will not overwrite existing files
			return;

		fm.writeFile(alpha_size, alpha, afilename);
	}

	/**
	 * Create the training in file with the given indices given. If the 
	 * file is already existed, it will not overwrite the file. 
	 * Please make sure to delete the file if you want new data.
	 * @param indices Indices for the training input file.
	 * @param current_percent The current morphed percentage for dead 
	 * code and subroutine code.
	 * @param current_fold The current fold for the cross-validation 
	 * experiment.
	 */
	public void createTrainingFile(ArrayList<Integer> indices, String current_percent, int current_fold) {
		int size = indices.size();
		indices.add(0, size); // Add the size of this training file to the first line of the input file
		String tfilename = null;

		if (grand_type.equalsIgnoreCase("Morphed")) {
			tfilename = user_home + "/" + grand_type + "/TrainFiles/" + Constants.SUBFOLDER 
				+ current_percent + "/IDAN" + current_percent + "f" 
				+ current_fold + ".in";
		} else if (grand_type.equalsIgnoreCase("Unmorphed")) {
			tfilename = user_home + "/" + grand_type + "/TrainFiles/" + Constants.SUBFOLDER 
				+ "0s0/IDAN0s0f" + current_fold + ".in";
		} else { // For Normal
			tfilename = user_home + "/" + grand_type + "/TrainFiles/" + Constants.SUBFOLDER 
				+ "0s0/IDAR0s0f" + current_fold + ".in";
		}
		if (new File(tfilename).exists()) // it will not overwrite existing files
			return;
		
		fm.writeInFile(indices, tfilename); 
	}
}
