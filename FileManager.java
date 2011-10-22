import java.io.*;
import java.util.ArrayList;

public class FileManager {

	/**
	 * Read a given file and then return the content of the file.
	 * @param filename The file to be read.
	 * @return The liness read from the given file.
	 */
	public ArrayList<String> readFile(String filename) {
		ArrayList<String> data = new ArrayList<String>();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			while ((line = reader.readLine())!= null) {
				if (!line.isEmpty()) {
					data.add(line.trim()); 
					//.replaceAll("\\s", "").replaceAll("\n", "").replaceAll("\r", "")
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error in reading the given file! Msg: " + e);
			System.exit(-1);
		}
		return data;
	}

	/**
	 * Create a file with the given name and write given data to it. 
	 * @param data_to_be_written Data that need to be written.
	 * @param filename The name of the file to be created with the given data.
	 */
	public void writeFile(ArrayList<String> data_to_be_written, String filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			if (data_to_be_written.size() < 1) {
				System.out.println("No data to be written to file " + filename);
				return;
			}
			for (String line: data_to_be_written) {
				writer.write(line + "\r\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error in writing the given file! Msg: " + e);
			System.exit(-1);
		}
	}

	public void writeInFile(ArrayList<Integer> data_to_be_written, String filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			if (data_to_be_written.size() < 1) {
				System.out.println("No data to be written to file " + filename);
				return;
			}
			for (Integer line: data_to_be_written) {
				writer.write(line + "\r\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error in writing the given file! Msg: " + e);
			System.exit(-1);
		}
	}

	//public void writeAlphabetFile() 
	public void writeFile(int size, ArrayList<String> data_to_be_written, String filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			if (data_to_be_written.size() < 1) {
				System.out.println("No data to be written to file " + filename);
				return;
			}
			writer.write(String.valueOf(size) + "\r\n");
			for (String line: data_to_be_written) {
				writer.write(line + "\r\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error in writing the given file! Msg: " + e);
			System.exit(-1);
		}
	}

	/**
	 * Create a directory.
	 * @param name_of_dir Name of the directory to be created.
	 * @return true if the folder is created.
	 */
	public boolean createDir(String name_of_dir) {
		if (new File(name_of_dir).exists()) {
			return true;
		} 
		return (new File(name_of_dir).mkdirs());
	}

	/**
	 * List all the names of the files and directories under this folder.
	 * @param folder_name The current folder.
	 * @return The list of files and folders in this folder.
	 */
	public ArrayList<String> getListOfFilenamesFromDir(String folder_name) {
		ArrayList<String> all_files = new ArrayList<String>();
		if (new File(folder_name).isDirectory()) {
			String[] files = new File(folder_name).list();
			for (String afile: files)
				all_files.add(afile);
		}
		return all_files;
	}

	/**
	 * Get the full path to the list of files and folders under 
	 * this given folder.
	 * @param folder_name The current folder.
	 * @return The list of full paths to the files and folders.
	 */
	public File[] getListOfFilesFromDir(String folder_name) {
		if (new File(folder_name).isDirectory()) {
			return new File(folder_name).listFiles();
		}
		System.out.println("Nothing to show in " + folder_name);
		return null;
	}
}
