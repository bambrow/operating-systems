import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Banker main class.
 * 
 */
public class Banker {

	private static String path; // file path
	private static Scanner scanner; // scanner
	
	private static int numTasks; // number of tasks
	private static Task[] tasks; // task array
	private static int numResources; // number of resources
	private static int[] resources; // resource array
	
	private static ArrayList<StringBuilder> output; // output strings
	
	/**
	 * Read path from command line argument.
	 * @param args
	 * @throws IllegalArgumentException
	 */
	private static void readPath(String[] args) throws IllegalArgumentException {
		if (args.length < 1) {
			throw new IllegalArgumentException("Illegal command line arguments!");
		}
		path = args[0];
	}
	
	/**
	 * Open the file using the given path.
	 * @throws FileNotFoundException
	 */
	private static void openFile() throws FileNotFoundException {
		File file = new File(path);
		if (!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException("Error! File does not exist!");
		}
		scanner = new Scanner(file);
	}
	
	/**
	 * Guarantee that there are remaining tokens in the file.
	 * @throws IOException
	 */
	private static void confirmNextToken() throws IOException {
		if (!scanner.hasNextLine()) {
			throw new IOException("Error! The file is not a valid input file!");
		}
	}
	
	/**
	 * Get the next int from file.
	 * @return next int
	 * @throws IOException
	 */
	private static int getNextInt() throws IOException {
		confirmNextToken();
		return scanner.nextInt();
	}
	
	/**
	 * Read the number of tasks from file.
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	private static void readNumTasks() throws IOException, IllegalArgumentException {
		numTasks = getNextInt();
		if (numTasks < 0) {
			throw new IllegalArgumentException("Illegal number of tasks! It must be non-negative!");
		}
		initializeTasks();
	}
	
	/**
	 * Read the number of resources from file.
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	private static void readNumResources() throws IOException, IllegalArgumentException {
		numResources = getNextInt();
		if (numResources < 0) {
			throw new IllegalArgumentException("Illegal number of resource types! It must be non-negative!");
		}
		initializeResources();
	}
	
	/**
	 * Initialize resource array.
	 */
	private static void initializeResources() {
		resources = new int[numResources + 1];
	}
	
	/**
	 * Read resources into resource array.
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	private static void readResources() throws IOException, IllegalArgumentException {
		for (int i = 1; i <= numResources; i++) {
			int r = getNextInt();
			if (r < 0) {
				throw new IllegalArgumentException("Illegal number of units for resources! It must be non-negative!");
			}
			resources[i] = r;
		}
	}
	
	/**
	 * Initialize task array.
	 */
	private static void initializeTasks() {
		tasks = new Task[numTasks + 1];
	}
	
	/**
	 * Initialize outputs.
	 */
	private static void initializeOutputs() {
		output = new ArrayList<StringBuilder>(numTasks + 1);
		for (int i = 0; i <= numTasks; i++) {
			output.add(new StringBuilder());
		}
	}
	
	/**
	 * Read tasks information into task array.
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	private static void readTasks() throws IOException, IllegalArgumentException {
		while (scanner.hasNextLine()) {
			ActivityHeader header = readActivityHeader();
			int taskID = getNextInt();
			int delay = getNextInt();
			int resourceType = getNextInt();
			int number = getNextInt();
			if (taskID <= 0 || taskID > numTasks) {
				throw new IllegalArgumentException("Illegal task-number! It must be positive and no greater than number of all tasks!");
			}
			if ((resourceType <= 0 || resourceType > numResources) && header != ActivityHeader.TERMINATE) {
				throw new IllegalArgumentException("Illegal resource-type! It must be positive and no greater than number of all resources!");
			}
			if (delay < 0 || number < 0) {
				throw new IllegalArgumentException("Illegal task arguments! Arguments delay and number (claimed, requested or released) must be non-negative!");
			}
			if (tasks[taskID] == null) {
				tasks[taskID] = new Task(taskID, numTasks, numResources);
			}
			tasks[taskID].addActivity(header, delay, resourceType, number);
		}
	}
	
	/**
	 * Read the activity header (type of activity) from the file.
	 * @return corresponding ActivityHeader object
	 * @throws IllegalArgumentException
	 */
	private static ActivityHeader readActivityHeader() throws IllegalArgumentException {
		String header = scanner.next().toLowerCase().trim();
		if (header.equals("initiate")) {
			return ActivityHeader.INITIATE;
		} else if (header.equals("request")) {
			return ActivityHeader.REQUEST;
		} else if (header.equals("release")) {
			return ActivityHeader.RELEASE;
		} else if (header.equals("terminate")) {
			return ActivityHeader.TERMINATE;
		} else {
			throw new IllegalArgumentException("Illegal task activity! Activity must be one of the following: initiate, request, release, terminate!");
		}
	}
	
	/**
	 * Reset all the tasks in the task array.
	 */
	private static void resetTasks() {
		for (int i = 1; i <= numTasks; i++) {
			tasks[i].reset();
		}
	}
	
	/**
	 * Save summary into summary string list.
	 */
	private static void savePrintSummary() {
		int time = 0;
		int wait = 0;
		for (int i = 1; i <= numTasks; i++) {
			output.get(i - 1).append(tasks[i].toString());
			if (!tasks[i].isAborted()) {
				time += tasks[i].getTime();
				wait += tasks[i].getWait();
			}
		}
		String timeString = String.format("%4d", time);
		String waitString = String.format("%4d", wait);
		String percentageString = String.format("%4d", Math.round(1.0 * 100 * wait / time));
		output.get(numTasks).append("    " + "total    " + timeString + waitString + percentageString + "%");
	}
	
	/**
	 * Add spaces to final output between algorithms.
	 */
	private static void addSpacesToOutput() {
		for (StringBuilder str : output) {
			str.append("       ");
		}
	}
	
	/**
	 * Print the final summary.
	 */
	private static void printSummary() {
		System.out.print("             FIFO         ");
		System.out.print("       ");
		System.out.print("           BANKER'S       ");
		System.out.println();
		for (StringBuilder str : output) {
			System.out.println(str.toString());
		}
	}
	
	/**
	 * Do preparation before starting.
	 */
	private static void start() {
		path = null;
		scanner = null;
		numTasks = 0;
		tasks = null;
		numResources = 0;
		resources = null;
		output = null;
	}
	
	/**
	 * Do cleaning after ending.
	 * @throws IOException
	 */
	private static void end() throws IOException {
		scanner.close();
	}
	
	/**
	 * Get everything started!
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static void run(String[] args) throws FileNotFoundException, IOException, IllegalArgumentException {
		start();
		readPath(args);
		openFile();
		readNumTasks();
		readNumResources();
		readResources();
		readTasks();
		initializeOutputs();
		FIFOManager.run(tasks, resources);
		savePrintSummary();
		addSpacesToOutput();
		resetTasks();
		BankerManager.run(tasks, resources);
		savePrintSummary();
		printSummary();
		end();
	}
	
	/**
	 * Main function.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Banker.run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
