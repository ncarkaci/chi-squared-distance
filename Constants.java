
public interface Constants {
	/******************************************************************
	 * The paths to the assembly files.
	 * 1. The path to the metamorphic worm files 
	 * 2. The location to the compare set used for testing (non-family virus and normal files)
	 *****************************************************************/
	public static final String PATH_NAME = "/Users/annietoderici/Documents/outputs/DataSets/Wings/";
		//"C:/Users/Annie/Desktop/School Work/Reverse Engineering/Da Lin's Data/junk35_function30/"; //DataSetOut0s0";
	public static final String TESTING_SETS = "/Users/annietoderici/Documents/outputs/DataSets/Wings/CompareSet/";
	public static final String OPCODE_DATABASE = "/Users/annietoderici/Documents/outputs/DataSets/All_instructions.txt";
	
	/******************************************************************
	 * Training files paths.
	 * 1. The path to the morphed training files - cross-validation sets (not random order set)
	 * 2. The path to the grand normal training files - (stratified cross-validation)
	 *****************************************************************/
	public static final String TRAIN_FILES_PATH = "/Users/annietoderici/Documents/outputs/TrainFiles/";
	//"C:/Users/Annie/Desktop/School Work/Reverse Engineering/TrainFiles/";
	public static final String GRAND_INPUT_PATH = "/Users/annietoderici/Documents/outputs/GrandInputSets/";

	/****************************************************************
	 * HMM related folders.
	 * 1. The location for the HMM.exe
	 * 2. The path to the HMM model files
	 * 3. The path to the score files for HMM testing. 
	 ****************************************************************/
	public static final String HMM_EXE = "/Users/annietoderici/Documents/workspace/CreateInputFiles/src/HMM.cpp";
	//"C:/Users/Annie/Documents/Visual Studio 2010/Projects/hmm_modified/Release/hmm_modified.exe";
	public static final String MODEL_PATH = "/Users/annietoderici/Documents/outputs/Model Files/";
	public static final String SCORE_PATH = "/Users/annietoderici/Documents/outputs/ScoresMac/";
		//"C:/Users/Annie/Desktop/School Work/Reverse Engineering/Scores/";
	
	/****************************************************************
	 * The paths to indices files.
	 ***************************************************************/
	public static final String INPUT_PATH = "/Users/annietoderici/Documents/outputs/InputSets/";
	//"C:/Users/Annie/Desktop/School Work/Reverse Engineering/Input Data/";
	public static final String OUTPUT_PATH = "/Users/annietoderici/Documents/outputs/Input Data/";
		//"C:/Users/Annie/Desktop/School Work/Reverse Engineering/Input Data/";
	public static final String INDICES_PATH = "/Users/annietoderici/Documents/outputs/InputSets/CrossValidationLists/";
	
	/****************************************************************
	 * Various parameters.
	 ***************************************************************/
	public static final String INPUT_DATA = "Input Data";
	public static final String TRAINFILES = "TrainFiles";
	public static final String SUBFOLDER = "DataSetOut";
	public static final String SUBFOLDER_OTHERSETS = "CompareSet";
	public static final int FOLD = 5;
	// Max number of threads running for HMM
	public static final int MAX_THREADS = 4;
	// Max number of hidden states for this test
	public static final int N_HIDDEN_STATES = 3;
	// The max percentage of subroutine and junk code for the metamorphic worm
	public static final int PERCENT = 40;
	// the random SEED
	public static final int SEED = 888;
	// max iterations to be performed on the HMM
	public static final int MAX_ITERS = 800; 
	// Number of normal files available (IDAR0 to IDAR 40)
	public static final int NORMAL_FILE_SIZE = 41;
	// Number of virus files (IDAN0 to IDAN199)
	public static final int VIRUS_FILE_SIZE = 200;
	// Number of non-family virus files (IDAV0 to 24)
	public static final int OTHER_VIRUS_SIZE = 25;
	public static final int DEAD_CODE_STEP = 10;
	public static final int SUBROUTINE_STEP = 10;
}
