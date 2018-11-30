
/**
 * Paging main class.
 *
 */
public class Paging {
	
	private static int machineSize;   // M
	private static int pageSize;      // P
	private static int processSize;   // S
	private static int jobMix;        // J
	private static int numReferences; // N
	private static String algo;       // R
	
	/**
	 * Parse the command line arguments and throws error if they are not eligible.
	 * @param args
	 */
	private static void parseArguments(String[] args) {
		if (args.length != 6) {
			throw new IllegalArgumentException("Error! Illegal command line arguments!");
		}
		machineSize = parseInteger(args[0]);
		pageSize = parseInteger(args[1]);
		processSize = parseInteger(args[2]);
		jobMix = parseInteger(args[3]);
		numReferences = parseInteger(args[4]);
		algo = args[5].toLowerCase();
	}
	
	/**
	 * Parse integer from a single argument string and throws error if conversion fails.
	 * @param arg
	 * @return the integer in the string
	 */
	private static int parseInteger(String arg) {
		try {
			return Integer.parseInt(arg);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Error! Illegal command line arguments!");
		}
	}
	
	/**
	 * Print the header including the basic information of this run.
	 */
	private static void printHeader() {
		System.out.println("The machine size is " + machineSize + ".");
		System.out.println("The page size is " + pageSize + ".");
		System.out.println("The process size is " + processSize + ".");
		System.out.println("The job mix number is " + jobMix + ".");
		System.out.println("The number of references per process is " + numReferences + ".");
		System.out.println("The replacement algorithm is " + algo + ".");
		System.out.println();
	}
	
	private static void runAlgorithm() {
		if (algo.equals("fifo") || algo.equals("random") || algo.equals("lru")) {
			Pager.run(machineSize, pageSize, processSize, jobMix, numReferences, algo);
		} else {
			throw new IllegalArgumentException("Error! Illegal command line arguments!");
		}
	}	
	
	/**
	 * Do preparation before starting.
	 */
	private static void start() {
		machineSize = 0;
		pageSize = 0;
		processSize = 0;
		jobMix = 0;
		numReferences = 0;
		algo = null;
	}
	
	/**
	 * Get everything started!
	 * @param args
	 */
	public static void run(String[] args) {
		start();
		parseArguments(args);
		printHeader();
		RandomNumberReader.initializeReader();
		runAlgorithm();
		RandomNumberReader.closeReader();
	}
	
	/**
	 * Main function.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Paging.run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
}
