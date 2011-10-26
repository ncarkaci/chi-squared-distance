import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;



public class ReadAsmMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename = System.getProperty("user.home") + "/Experiment/Lists/CrossValidationList"; //Constants.INDICES_PATH + "CrossValidationList";
		int num_of_fold = Constants.FOLD;
		//int lll = 42/5;  //(int) Math.ceil(40 * 0.8);
		
		//System.out.println(lll);
		//buildVirusInFiles(num_of_fold, filename);
		buildNormalInFiles(num_of_fold, filename);
	}
	
	public static void buildVirusInFiles(int num_of_fold, String filename) {
		String current_percent = null;
		int total_size = Constants.VIRUS_FILE_SIZE;
		
		// For morphed and unmorphed
		CreateInputFiles cif = new CreateInputFiles(total_size, num_of_fold);
		//ArrayList<ArrayList<Integer>> file_indices = cif.getCrossValidationList();
		// Only need to create these files once
		// The path including the name (the method will attach the fold # & size)
		//cif.createCrossValidationIndicesLists("");

		for (int dead_code = 10; dead_code < 11; dead_code += Constants.DEAD_CODE_STEP) {
			for (int subroutine_code = 0; subroutine_code < 1; subroutine_code += Constants.SUBROUTINE_STEP) {
				current_percent = dead_code + "s" + subroutine_code;
				//System.out.println(current_percent);
				//cif.performCrossValidation(current_percent, filename, "Morphed");
				cif.performCrossValidation(current_percent, filename, "Unmorphed");
			}
		} 
	}
	
	public static void buildNormalInFiles(int num_of_fold, String filename) {
		// For normal
		int total_size = 41;
		int total_virus_size = 200;
		String current_percent = null;
		//CreateNormalInputFiles nif = new CreateNormalInputFiles(total_size, num_of_fold, total_virus_size);
		CreateInputFiles nif = new CreateInputFiles(total_size, num_of_fold);
		ArrayList<ArrayList<Integer>> file_indices1 = nif.getCrossValidationList();
		
		// Only need to create these files once
		// The path including the name (the method will attach the fold # & size)
		//nif.createCrossValidationIndicesLists("");
		
		for (int dead_code = 0; dead_code < 1; dead_code += Constants.DEAD_CODE_STEP) {
			for (int subroutine_code = 0; subroutine_code < 1; subroutine_code += Constants.SUBROUTINE_STEP) {
				current_percent = dead_code + "s" + subroutine_code;
				//System.out.println(current_percent);

				// normal is currently not working using cif
				nif.performCrossValidation(current_percent, filename, "Normal");
				// normal is working on nif
				//nif.performCrossValidation(current_percent, filename, "Normal");
			}
		}
	}


	/*
	public static void performCrossValidation(ProcessAsm asm, FileManager fm, 
			int total_size, int num_of_fold, String current_percent) {
		//FileManager fm = new FileManager();
		//ProcessAsm asm = new ProcessAsm();
		String filename = Constants.INDICES_PATH + "CrossValidationList_";

		for (int fold = 0; fold < num_of_fold; fold++) {
			filename = Constants.INDICES_PATH + "CrossValidationList_" + fold + ".log";
			// Get the cross-validation indices - first 160 are for training, 40 for testing
			ArrayList<String> perm_list = fm.readFile(filename);
			// Create all the necessary input files
			createInputFiles(asm, fm, perm_list, total_size, num_of_fold, current_percent, fold);
		}
	}

	public static void createInputFiles(ProcessAsm asm, FileManager fm, 
			ArrayList<String> perm_list, int total_size, int num_of_fold, 
			String current_percent, int current_fold) {
		// This is where the list of instructions with their indexes
		String outputfile = "/Users/annietoderici/Documents/outputs/Input Data/All_instructions.txt";
		//String PATH_NAME = "/Users/annietoderici/Documents/outputs/DataSets/Lin/DataSetOut0s0/";
		String afile = "/Users/annietoderici/Documents/outputs/DataSets/Lin/DataSetOut0s0/IDAN0.asm";
		//"C:/Users/Annie/Desktop/School Work/Reverse Engineering/Da Lin's Data/junk35_function30/"; //DataSetOut0s0";
		//ProcessAsm asm = new ProcessAsm();
		//FileManager fm = new FileManager();
		int size_per_set = total_size / num_of_fold;
		ArrayList<String> raw_asm = new ArrayList<String>(); 

		ArrayList<String> allfiles = new ArrayList<String>(); 

		// Creating the training file, testing files, and alphabet file for a fold
		String fname = null;
		for (int i = 0; i < total_size - size_per_set; i++) {
			fname = Constants.PATH_NAME + Constants.SUBFOLDER + current_percent + "/IDAN" + perm_list.get(i) + ".asm";
			raw_asm.addAll(fm.readFile(fname));
		}

		// Create directories for the data
		String training_folder = Constants.TRAIN_FILES_PATH + Constants.SUBFOLDER + current_percent + "/";
		fm.createDir(training_folder);
		String testing_folder = Constants.OUTPUT_PATH + Constants.SUBFOLDER + current_percent + "/";
		fm.createDir(testing_folder);

		// Create the unique alphabet for this fold
		allfiles.addAll(asm.getPossibleOpcodeFromAsm(raw_asm));
		ArrayList<String> alpha = asm.getUniqueOpcode(allfiles);
		int alpha_size = alpha.size();
		String afilename = Constants.TRAIN_FILES_PATH + Constants.SUBFOLDER + current_percent + "/IDAN" + current_percent + "f" + current_fold + ".alphabet";
		fm.writeFile(alpha_size, alpha, afilename);

		// Important opcode with their corresponding indices
		HashMap<String, Integer> dictionary = asm.getIndicesDictionary(alpha);

		// Get the indices for the training file
		ArrayList<Integer> indices = asm.getInFiles(allfiles, dictionary);
		int size = indices.size();
		indices.add(0, size); // Add the size of this training file to the first line of the input file
		String tfilename = Constants.TRAIN_FILES_PATH + Constants.SUBFOLDER + current_percent + "/IDAN" + current_percent + "f" + current_fold + ".in";
		fm.writeInFile(indices, tfilename); 

		// Create testing files using the remaining virus files
		for (int i = total_size - size_per_set; i < total_size; i++) {
			String asmfname2 = Constants.PATH_NAME + Constants.SUBFOLDER + current_percent + "/IDAN" + perm_list.get(i) + ".asm";
			ArrayList<String> temp_asm = new ArrayList<String>();
			temp_asm.addAll(fm.readFile(asmfname2));
			ArrayList<String> valid_asm = new ArrayList<String>();
			valid_asm.addAll(asm.getPossibleOpcodeFromAsm(temp_asm));
			ArrayList<Integer> temp_indices = new ArrayList<Integer>();
			temp_indices = asm.getInFiles(valid_asm, dictionary);
			int tsize = temp_indices.size();
			temp_indices.add(0, tsize); // Add the size of this test file to the first line of the input file
			String tfname = Constants.OUTPUT_PATH + Constants.SUBFOLDER + 
				current_percent + "/IDAN" + current_percent + "f" + current_fold + 
				"_" + perm_list.get(i) + ".in";
			fm.writeInFile(temp_indices, tfname);
		}  

		// Create testing files using the normal files
		for (int i = 0; i < size_per_set; i++) {
			String asmfname3 = Constants.PATH_NAME + Constants.SUBFOLDER_OTHERSETS + "/IDAR" + i + ".asm";
			ArrayList<String> temp_asm = new ArrayList<String>();
			temp_asm.addAll(fm.readFile(asmfname3));
			ArrayList<String> valid_asm = new ArrayList<String>();
			valid_asm.addAll(asm.getPossibleOpcodeFromAsm(temp_asm));
			ArrayList<Integer> temp_indices = new ArrayList<Integer>();
			temp_indices = asm.getInFiles(valid_asm, dictionary);
			int tsize = temp_indices.size();
			temp_indices.add(0, tsize); // Add the size of this test file to the first line of the input file
			String tfname = Constants.OUTPUT_PATH + Constants.SUBFOLDER + 
				current_percent + "/IDAR" + current_percent + "f" + current_fold + 
				"_" + i + ".in";
			fm.writeInFile(temp_indices, tfname);
		}
	}

	 */


}
