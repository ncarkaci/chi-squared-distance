import java.util.ArrayList;
import java.util.HashMap;


public class CreateNormalInputFiles extends CreateInputFiles {
	private int virus_total;
	private String cv_partial_path = "/Experiment/Lists/CrossValidationList";

	public CreateNormalInputFiles(int total, int fold) {
		super(total, fold);
		virus_total = 200;
	}

	/************************************************************************
	 * 
	 * @param perm_list
	 * @param current_percent
	 * @param current_fold
	 ***********************************************************************/
	public void createInputFiles(ArrayList<String> perm_list, String current_percent, int current_fold) {
		ArrayList<String> raw_asm = new ArrayList<String>(); 
		ArrayList<String> legit_opcode = new ArrayList<String>(); 

		// Creating the training file, testing files, and alphabet file for a fold
		raw_asm.addAll(getTrainingAsm(current_percent, perm_list));

		// Create directories for the data
		String training_folder = user_home + "/" + grand_type + "/TrainFiles/" + Constants.SUBFOLDER + current_percent + "/";
		fm.createDir(training_folder);
		String testing_folder = user_home + "/" + grand_type + "/Input Data/" + Constants.SUBFOLDER + current_percent + "/";
		fm.createDir(testing_folder);

		// Create the unique alphabet for this fold
		//legit_opcode.addAll(asm.getPossibleOpcodeFromAsm(raw_asm));
		legit_opcode.addAll(asm.getPossibleOpcodeUsingDatabase(raw_asm));
		raw_asm.clear();
		ArrayList<String> alpha = asm.getUniqueOpcode(legit_opcode);
		createAlphabetFile(alpha, current_percent, current_fold);
		//System.out.println("Alpha size is " + alpha.size());
		// Important opcode with their corresponding indices
		HashMap<String, Integer> dictionary = asm.getIndicesDictionary(alpha);

		// Get the indices for the training file
		ArrayList<Integer> indices = asm.getInFiles(legit_opcode, dictionary);
		createTrainingFile(indices, current_percent, current_fold);
		indices.clear();

		// for the virus input assembly files
		String testing_asm_path = Constants.PATH_NAME + Constants.SUBFOLDER + current_percent + "/";
		String testing_input_part = Constants.PATH_NAME + Constants.SUBFOLDER + current_percent + "/IDAN";
		String testing_output_part = "IDAN" + current_percent + "f" + current_fold;

		int train_start = 0;  //only for virus testing
		int train_end = size_per_set;

		train_start = Integer.parseInt(perm_list.get(total_size - size_per_set)); 
		testing_input_part = Constants.PATH_NAME + Constants.SUBFOLDER_OTHERSETS + "/IDAR";
		testing_output_part = Constants.SUBFOLDER + 
		current_percent + "/" + "IDAR" + current_percent + "f" + current_fold;

		//createTestingFile(train_start, train_end, current_percent, current_fold, dictionary, testing_asm_path, "IDAN");
		createTestingFile(train_start, train_end, testing_input_part, testing_output_part, perm_list, dictionary);

		train_start = 160;  //only for virus testing
		train_end = 200;
		String other_list_fname = user_home + "/Experiment/Lists/CrossValidationList" + current_fold + "_200.log";
		ArrayList<String> other_list = fm.readFile(other_list_fname);

		testing_input_part = Constants.PATH_NAME + Constants.SUBFOLDER + current_percent +"/IDAN";
		testing_output_part = Constants.SUBFOLDER + 
		current_percent + "/" + "IDAN" + current_percent + "f" + current_fold;

		train_start = Integer.parseInt(other_list.get(200 - 40)); 
		train_end = 200 / num_of_fold;

		createTestingFile(train_start, train_end, testing_input_part, testing_output_part, other_list, dictionary);
	}
	

}
