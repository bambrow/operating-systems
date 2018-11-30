
/**
 * Pager class that run the demand paging simulator.
 *
 */
public class Pager {
	
	private static final int Q = 3; // quantum
	
	private static int time = 0; // current time
	private static int reference = 0; // current reference number
	private static int F; // frame number
	private static Page[] frames; // frame array
	private static Process[] processes; // process array
	
	/**
	 * Run the demand paging simulator, using the replacement algorithm chose by user.
	 * @param machineSize
	 * @param pageSize
	 * @param processSize
	 * @param jobMix
	 * @param numReferences
	 * @param algo
	 */
	public static void run(int machineSize, int pageSize, int processSize, int jobMix, int numReferences, String algo) {
		
		// initialize frame and process array
		F = machineSize / pageSize;
		frames = new Page[F];
		processes = initializeSet(jobMix, processSize);
		
		// begin the loop
		while (reference < numReferences) {
			
			// calculate the next eligible quantum
			int q = Math.min((numReferences - reference), Q);
			
			// for each process
			for (int i = 1; i < processes.length; i++) {
				
				// for rach eligible reference
				for (int ref = 0; ref < q; ref++) {
					
					time++; // increment the time
					Process process = processes[i];
					int address = process.getAddress();
					int ID = calculatePageID(address, pageSize); // calculate the next page ID to be accessed
					
					// if the page is not in the frame array, try to find an empty frame
					// if there is an empty frame, use it
					// if there is no empty frame, using the replacement algorithm to choose a frame to be evicted
					// if the page is in the frame array, update the recent used time
					int frameIndex = containsPage(ID, process.getID(), frames);
					if (frameIndex < 0) {
						process.incrementFaults(); // page not in frame, increment the fault count
						Page newPage = new Page(ID, process.getID(), time, time); // create this new page
						int freeFrameIndex = freeFrame(frames); // see if there is a free frame
						if (freeFrameIndex < 0) {
							if (algo.equals("fifo")) {
								freeFrameIndex = FIFO();
							} else if (algo.equals("random")) {
								freeFrameIndex = random();
							} else {
								freeFrameIndex = LRU();
							}
							Page oldPage = frames[freeFrameIndex]; // no free frame, use the algorithm to choose the old page to be evicted
							processes[oldPage.getProcessID()].incrementEvictions(); // increment the eviction count
							processes[oldPage.getProcessID()].addResidencyTime(time - oldPage.getLoadedTime()); // add to the total residency time sum
							frames[freeFrameIndex] = newPage; // switch the page
							// System.out.println(process.getID() + " references word " + address + " (page " + ID + ") at time " + time + ": Fault, evicting page " + oldPage.getID() + " of " + oldPage.getProcessID() + " from frame " + freeFrameIndex);
						} else {
							frames[freeFrameIndex] = newPage; // there is a free frame, just use it
							// System.out.println(process.getID() + " references word " + address + " (page " + ID + ") at time " + time + ": Fault, using free frame " + freeFrameIndex);
						}
					} else {
						frames[frameIndex].setRecentUsedTime(time); // the page is in the frame array, just update the recent used time
						// System.out.println(process.getID() + " references word " + address + " (page " + ID + ") at time " + time + ": Hit in frame " + frameIndex);
					}
					
					process.nextAddress(); // and then calculate the next address
					
				}
				
			}
			
			reference += q; // finally, add the quantum to the reference count
			
		}
		
		printSummary(processes); // print summary before exit
		
	}
	
	/**
	 * Calculate the index of the page to be evicted using FIFO replacement algorithm.
	 * @return the index of the page to be evicted
	 */
	private static int FIFO() {
		int index = 0;
		int minLoadedTime = Integer.MAX_VALUE;
		for (int j = 0; j < frames.length; j++) {
			if (frames[j].getLoadedTime() < minLoadedTime) {
				minLoadedTime = frames[j].getLoadedTime();
				index = j;
			}
		}
		return index;
	}
	
	/**
	 * Calculate the index of the page to be evicted using random replacement algorithm.
	 * @return the index of the page to be evicted
	 */
	private static int random() {
		return RandomNumberReader.randomInteger(F);
	}
	
	/**
	 * Calculate the index of the page to be evicted using LRU replacement algorithm.
	 * @return the index of the page to be evicted
	 */
	private static int LRU() {
		int index = 0;
		int maxUnusedTime = Integer.MIN_VALUE;
		for (int j = 0; j < frames.length; j++) {
			if (time - frames[j].getRecentUsedTime() > maxUnusedTime) {
				maxUnusedTime = time - frames[j].getRecentUsedTime();
				index = j;
			}
		}
		return index;
	}
	
	/**
	 * Initialize the process array using the job mix.
	 * @param J
	 * @param S
	 * @return the process array
	 */
	private static Process[] initializeSet(int J, int S) {
		Process[] processes;
		if (J == 1) {
			processes = new Process[2];
			processes[1] = new Process(1, 1.0, 0.0, 0.0, S);
		} else if (J == 2) {
			processes = new Process[5];
			for (int i = 1; i <= 4; i++) {
				processes[i] = new Process(i, 1.0, 0.0, 0.0, S);
			}
		} else if (J == 3) {
			processes = new Process[5];
			for (int i = 1; i <= 4; i++) {
				processes[i] = new Process(i, 0.0, 0.0, 0.0, S);
			}
		} else if (J == 4) {
			processes = new Process[5];
			processes[1] = new Process(1, 0.75, 0.25, 0.0, S);
			processes[2] = new Process(2, 0.75, 0, 0.25, S);
			processes[3] = new Process(3, 0.75, 0.125, 0.125, S);
			processes[4] = new Process(4, 0.5, 0.125, 0.125, S);
		} else {
			throw new IllegalArgumentException("Error! Illegal command line arguments!");
		}
		return processes;
	}
	
	/**
	 * Calculate the index of free frame in the frame array, from highest to lowest.
	 * @param frames
	 * @return the index of free frame, or -1 if there is none
	 */
	private static int freeFrame(Page[] frames) {
		for (int i = frames.length - 1; i >= 0; i--) {
			if (frames[i] == null) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Calculate page ID according to the actual address and page size.
	 * @param address
	 * @param pageSize
	 * @return page ID
	 */
	private static int calculatePageID(int address, int pageSize) {
		return address / pageSize;
	}
	
	/**
	 * Calculate the index of the page if the page is in the frame array.
	 * @param ID
	 * @param processID
	 * @param frames
	 * @return the index of the corresponding page, or -1 if it is not in the frame array
	 */
	private static int containsPage(int ID, int processID, Page[] frames) {
		for (int i = 0; i < frames.length; i++) {
			Page page = frames[i];
			if (page != null && page.getID() == ID && page.getProcessID() == processID) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Print the summary of the whole run.
	 * @param processes
	 */
	private static void printSummary(Process[] processes) {
		int faults = 0;
		int evictions = 0;
		int residencyTime = 0;
		for (int i = 1; i < processes.length; i++) {
			System.out.println(processes[i]);
			faults += processes[i].getFaults();
			evictions += processes[i].getEvictions();
			residencyTime += processes[i].getResidencyTime();
		}
		System.out.println();
		System.out.print("The total number of faults is " + faults);
		if (evictions > 0) {
			System.out.println(" and the overall average residency is " + (1.0 * residencyTime / evictions) + ".");
		} else {
			System.out.println(".\n     With no evictions, the overall average residence is undefined.");
		}
	}

}
