import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class RR extends SchedulingAlgorithm {
	
	private static final int DEFAULT_QUANTUM = 2;

	private static List<Process> processes = new ArrayList<Process>();
	private static Queue<Process> unstartedProcesses = new LinkedList<Process>();
	private static Queue<Process> readyProcesses = new LinkedList<Process>();
	private static PriorityQueue<Process> blockedProcesses;
	private static List<Process> terminatedProcesses = new ArrayList<Process>();
	private static Process runningProcess = null;
	
	private static Process steadyProcess = null;
	
	private static int numProcesses;
	private static int cycle = 0;
	private static int CPUTime = 0;
	private static int IOTime = 0;
	
	private static int sumTurnaroundTime = 0;
	private static int sumWaitingTime = 0;
	
	private static int quantum = DEFAULT_QUANTUM;
	
	public static void run(List<Process> processList, boolean verbose) {
		
		numProcesses = processList.size();
		blockedProcesses = new PriorityQueue<Process>(numProcesses, new BlockedProcessComparator());
		
		processes.addAll(processList);
		printOriginal(numProcesses, processes);
		Collections.sort(processes, new ProcessSortComparator());
		assignSortedID(numProcesses, processes);
		printSorted(numProcesses, processes);
		
		System.out.println();
		if (verbose) {
			System.out.println("This detailed printout gives the state and remaining burst for each process");
			System.out.println();
		}
		
		for (Process process : processes) {
			unstartedProcesses.offer(process);
		}
		
		if (verbose) {
			printCycleZero(numProcesses);
		}
		
		while (terminatedProcesses.size() < numProcesses) {
			// add newly ready processes from unstarted
			while (!unstartedProcesses.isEmpty() && unstartedProcesses.peek().A == cycle) {
				readyProcesses.offer(unstartedProcesses.poll());
			}
			// add cycle
			cycle++;
			// if no running process, pick one
			if (runningProcess == null && !readyProcesses.isEmpty()) {
				runningProcess = readyProcesses.poll();
				if (runningProcess.CPUBurstTime == 0) {
					int assignedCPUBurstTime = RandomNumberReader.randomOS(runningProcess.B);
					runningProcess.CPUBurstTime = assignedCPUBurstTime;
				}
			}
			// print verbose
			if (verbose) {
				printCurrentCycle(cycle, processes, unstartedProcesses, readyProcesses, blockedProcesses, terminatedProcesses, runningProcess);
			}
			// handle waiting processes
			for (Process process : readyProcesses) {
				process.waitingTime++;
			}
			// handle blocked processes
			if (!blockedProcesses.isEmpty()) {
				IOTime++;
			}
			for (Process process : blockedProcesses) {
				process.IOBurstTime--;
				process.IOTime++;
			}
			// run the process and handle it
			if (runningProcess != null) {
				runningProcess.CPUBurstTime--;
				runningProcess.remainingCPUTime--;
				quantum--;
				if (runningProcess.remainingCPUTime == 0) {
					runningProcess.finishingTime = cycle;
					runningProcess.turnaroundTime = cycle - runningProcess.A;
					terminatedProcesses.add(runningProcess);
					runningProcess = null;
					quantum = DEFAULT_QUANTUM;
				} else if (runningProcess.CPUBurstTime == 0) {
					int assignedIOBurstTime = RandomNumberReader.randomOS(runningProcess.IO);
					runningProcess.IOBurstTime = assignedIOBurstTime;
					blockedProcesses.offer(runningProcess);
					runningProcess = null;
					quantum = DEFAULT_QUANTUM;
				} else if (quantum == 0) {
					steadyProcess = runningProcess;
					runningProcess = null;
					quantum = DEFAULT_QUANTUM;
				}
				CPUTime++;
			}
			// handle blocked process and tie breaking
			if (steadyProcess != null) {
				while (!blockedProcesses.isEmpty() && blockedProcesses.peek().IOBurstTime == 0 && (blockedProcesses.peek().A < steadyProcess.A || (blockedProcesses.peek().A == steadyProcess.A && blockedProcesses.peek().sortedID < steadyProcess.sortedID))) {
					readyProcesses.offer(blockedProcesses.poll());
				}
				readyProcesses.offer(steadyProcess);
				steadyProcess = null;
			}
			while (!blockedProcesses.isEmpty() && blockedProcesses.peek().IOBurstTime == 0) {
				readyProcesses.offer(blockedProcesses.poll());
			}
		}
		
		sumTurnaroundTime = sumTurnaround(terminatedProcesses);
		sumWaitingTime = sumWaiting(terminatedProcesses);
		
		System.out.println("The scheduling algorithm used was Round Robbin");
		System.out.println();
		
		Collections.sort(terminatedProcesses, new TerminatedProcessComparator());
		printProcesses(terminatedProcesses);
		printSummary(cycle, CPUTime, IOTime, sumTurnaroundTime, sumWaitingTime, terminatedProcesses);
	}
	
	protected static void printCurrentCycle(int cycle, List<Process> processes, Queue<Process> unstartedProcesses, Queue<Process> readyProcesses, PriorityQueue<Process> blockedProcesses, List<Process> terminatedProcesses, Process runningProcess) {
		System.out.print("Before cycle");
		System.out.printf("%5d: ", cycle);
		for (Process process : processes) {
			int counter = 0;
			if (unstartedProcesses.contains(process)) {
				System.out.print("  unstarted");
			} else if (readyProcesses.contains(process)) {
				System.out.print("      ready");
				// counter = process.CPUBurstTime;
			} else if (terminatedProcesses.contains(process)) {
				System.out.print(" terminated");
			} else if (blockedProcesses.contains(process)) {
				System.out.print("    blocked");
				counter = process.IOBurstTime;
			} else if (runningProcess == process) {
				System.out.print("    running");
				counter = process.CPUBurstTime < quantum ? process.CPUBurstTime : quantum;
			}
			System.out.printf("%3d", counter);
		}
		System.out.println(".");
	}
	
}
