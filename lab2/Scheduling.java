import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Scheduling {
	
	private static boolean verbose;
	private static String path;
	private static BufferedReader reader;
	private static LinkedList<String> buffer;
	
	private static int numProcesses;
	
	private static List<Process> processList;
	
	private static void readPath(String[] args) throws IllegalArgumentException {
		if (args.length == 1) {
			verbose = false;
			path = args[0];
		} else if (args.length == 2 && args[0].equals("--verbose")) {
			verbose = true;
			path = args[1];
		} else {
			throw new IllegalArgumentException("Error! Illegal arguments!");
		}
	}
	
	private static void openFile() throws FileNotFoundException {
		File file = new File(path);
		if (!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException("Error! File does not exist!");
		}
		reader = new BufferedReader(new FileReader(path));
	}
	
	private static boolean isEmptyLine(String line){
		return line.trim().length() == 0;
	}
	
	private static String getNextToken() throws IOException {
		if (!buffer.isEmpty()) {
			return buffer.removeFirst();
		}
		String line = reader.readLine();
		if (line == null) {
			throw new IOException("Error! The file is not a valid input file!");
		}
		while (isEmptyLine(line) || buffer.isEmpty()) {
			if (!isEmptyLine(line)) {
				for (String token : line.split("\\s+")) {
					if (!isEmptyLine(token)) {
						buffer.addLast(token);
					}
				}
			} else {
				line = reader.readLine();
			}
		}
		return buffer.removeFirst();
	}
	
	private static void readProcessesNum() throws NumberFormatException, IOException {
		numProcesses = Integer.parseInt(getNextToken());
	}
	
	private static void readProcesses() throws NumberFormatException, IOException {
		for (int i = 0; i < numProcesses; i++) {
			int a = Integer.parseInt(getNextToken());
			int b = Integer.parseInt(getNextToken());
			int c = Integer.parseInt(getNextToken());
			int io = Integer.parseInt(getNextToken());
			Process process = new Process(i, a, b, c, io);
			processList.add(process);
		}
	}
	
	private static void resetProcesses() {
		for (Process process : processList) {
			process.reset();
		}
	}
	
	private static void start() {
		verbose = false;
		path = null;
		reader = null;
		numProcesses = 0;
		buffer = new LinkedList<String>();
		processList = new ArrayList<Process>();
	}
	
	public static void run(String[] args) throws NumberFormatException, IOException {
		start();
		readPath(args);
		openFile();
		readProcessesNum();
		readProcesses();
		RandomNumberReader.initializeReader();
		// FCFS
		FCFS.run(processList, verbose);
		
		System.out.println();
		resetProcesses();
		RandomNumberReader.resetReader();
		// RR
		RR.run(processList, verbose);
		
		System.out.println();
		resetProcesses();
		RandomNumberReader.resetReader();
		// Uniprogrammed
		Uniprogrammed.run(processList, verbose);
		
		System.out.println();
		resetProcesses();
		RandomNumberReader.resetReader();
		// PSJF
		PSJF.run(processList, verbose);
		
		RandomNumberReader.closeReader();
	}
	
	public static void main(String[] args) {
		try {
			Scheduling.run(args);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
