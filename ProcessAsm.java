import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ProcessAsm {
	private int num_of_unique_opcode;
	private ArrayList<String> opcode  = new ArrayList<String>();

	public ProcessAsm() {
		FileManager fm = new FileManager();
		opcode = fm.readFile(Constants.OPCODE_DATABASE);
	}
	
	public ProcessAsm(ArrayList<String> db) {
		opcode = db;
	}
	
	public ArrayList<String> getPossibleOpcodeUsingDatabase(ArrayList<String> raw_asm) {
		ArrayList<String> filtered_asm = new ArrayList<String>();
		System.out.println(raw_asm.size());
		filtered_asm.ensureCapacity(raw_asm.size());
		for (String line: raw_asm) {
			line = line.replaceAll("\\s+", " ");
			String[] tokens = line.trim().split("\\s");
			// Sometimes instructions appear after the label
			//System.out.println(line + " tokens[0] is " + tokens[0]);
			if (tokens[0].trim().endsWith(":")) {
				if (tokens.length > 1) 
					tokens[0] = tokens[1];
			}
			if (opcode.contains(tokens[0]))
				filtered_asm.add(tokens[0]);
		}
		return filtered_asm;
	}

	public ArrayList<String> getPossibleOpcodeFromAsm(ArrayList<String> raw_asm) {
		ArrayList<String> filtered_asm = new ArrayList<String>();
        Pattern p = Pattern.compile("align||assume||db||dd||dw||dword||end||extrn||model||nop||public");

		filtered_asm.ensureCapacity(raw_asm.size());
		int raw_asm_idx = 0;
		for (String line: raw_asm) {
			++raw_asm_idx;
			// Some data in assembly files are separated by many whitespace chars
			line = line.replaceAll("\\s+", " ");
			// Separate the line with a whitespace character: \s = [ \t\n\x0B\f\r]s
			String[] tokens = line.split("\\s");
			/*if (filtered_asm.size() % 10000 == 0)
			  System.out.println("The line is " + tokens[0].trim() + " size=" + filtered_asm.size() + 
					  " raw_asm=" + raw_asm_idx); */
			// Filter out comments, labels, directives
			/*	if (!(tokens[0].startsWith(";") || tokens[0].endsWith(":")  
					//; means start of a comment, : indicate it is a label
					|| tokens[0].matches("align||assume||db||dd||dword||end||extrn||model||nop||public")
					|| tokens[0].matches("[A-Z]+?") //Get rid of DATA, CODE
					//|| tokens[0].contains("[A-Z]+?") 
					|| tokens[0].contains("_") )) */
			// Sometimes instructions appear after the label
			if (tokens[0].trim().endsWith(":")) {
				if (tokens.length > 1) 
					tokens[0] = tokens[1];
			}
			// Instructions in these assembly files are always in lowercase letters 
			if (tokens[0].matches("[a-z\\d]+")) {
				Matcher m = p.matcher(tokens[0]);
				if (!m.matches()) {
					filtered_asm.add(tokens[0]);
				}
			}
		}
		return filtered_asm;
	}

	public ArrayList<String> getOpcode() {
		return opcode;
	}

	public ArrayList<ArrayList<Integer>> createCrossValidationList(int total_size, int num_of_set) {
		int num_per_set = total_size / num_of_set;
		ArrayList<ArrayList<Integer>> file_indices = new ArrayList<ArrayList<Integer>>();

		for (int fold = 0; fold < num_of_set; fold++) {
			// false indicate for training, true for testing
			ArrayList<Boolean> current_fold = new ArrayList<Boolean>();
			// Initialize it to false.
			for (int i = 0; i < total_size; i++) {
				current_fold.add(false);		
			}

			// Setting the testing file indices to true
			int index = 0;
			for (int i = 0; i < num_per_set; i++) {
				index = fold * num_per_set + i;
				current_fold.add(index, true);
			}

			ArrayList<Integer> current_indices = new ArrayList<Integer>();
			ArrayList<Integer> testing_indices = new ArrayList<Integer>();
			// Save the indices to the arraylist
			for (int i = 0; i < total_size; i++) {
				if (current_fold.get(i))
					testing_indices.add(i);
				else {
					current_indices.add(i);
				}
			}
			current_indices.addAll(testing_indices);
			file_indices.add(fold, current_indices);
		}
		/*
		for (ArrayList<Integer> data: file_indices) {
			for (int line: data) {
				System.out.print(line + " ");
			}
			System.out.println();
		} */
		return file_indices;
	}

	public ArrayList<String> getUniqueOpcode(ArrayList<String> instructions) {
		HashSet<String> unique_opcode = new HashSet<String>();
		ArrayList<String> data = new ArrayList<String>();

		for (String e: instructions) {
			if (!unique_opcode.contains(e)) {
				unique_opcode.add(e);
				data.add(e);
			}
		}
		return data;
	}

	/**
	 * Given the alphabet, this will build the dictionary for quick lookup 
	 * of opcode with the corresponding indices.
	 * @param alphabet Unique alphabet (instructions)
	 * @return dictionary of instructions with indices
	 */
	public HashMap<String, Integer> getIndicesDictionary(ArrayList<String> alphabet) {
		HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
		int index = 0;

		for (String current: alphabet) {
			dictionary.put(current, index);
			index++;
		}
		/*
		for (Map.Entry<String, Integer> pair: dictionary.entrySet()) {
			System.out.println(pair);
		} */
		return dictionary;
	}

	public ArrayList<Integer> getInFiles(ArrayList<String> instructions, HashMap<String, Integer> indices_dictionary) {
		ArrayList<Integer> indices = new ArrayList<Integer>();

		for (String current: instructions) {
			if (indices_dictionary.containsKey(current)) { 
				//int index = indices_dictionary.indexOf(current);
				indices.add(indices_dictionary.get(current));
			} else {
				indices.add(indices_dictionary.size());
			}
		}
		return indices;
	}


}
