import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class PSJF extends SchedulingAlgorithm {
	
	private static class ReadyProcessComparator implements Comparator<Process> {
		@Override
		public int compare(Process a, Process b) {
			return (a.remainingCPUTime != b.remainingCPUTime) ? (a.remainingCPUTime - b.remainingCPUTime) : ((a.A != b.A) ? (a.A - b.A) : (a.sortedID - b.sortedID));
		}
	}

	private static List<Process> processes = new ArrayList<Process>();
	private static Queue<Process> unstartedProcesses = new LinkedList<Process>();
	private static Queue<Process> readyProcesses;
	private static PriorityQueue<Process> blockedProcesses;
	private static List<Process> terminatedProcesses = new ArrayList<Process>();
	private static Process runningProcess = null;
	
	private static int numProcesses;
	private static int cycle = 0;
	private static int CPUTime = 0;
	private static int IOTime = 0;
	
	private static int sumTurnaroundTime = 0;
	private static int sumWaitingTime = 0;
	
	public static void run(List<Process> processList, boolean verbose) {
		
		numProcesses = processList.size();
		readyProcesses = new PriorityQueue<Process>(numProcesses, new ReadyProcessComparator());
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
			} else if (runningProcess != null) {
				if (!readyProcesses.isEmpty() && (readyProcesses.peek().remainingCPUTime < runningProcess.remainingCPUTime || (readyProcesses.peek().remainingCPUTime == runningProcess.remainingCPUTime && readyProcesses.peek().A < runningProcess.A) || (readyProcesses.peek().remainingCPUTime == runningProcess.remainingCPUTime && readyProcesses.peek().A == runningProcess.A && readyProcesses.peek().sortedID < runningProcess.sortedID))) {
					readyProcesses.offer(runningProcess);
					runningProcess = readyProcesses.poll();
					if (runningProcess.CPUBurstTime == 0) {
						int assignedCPUBurstTime = RandomNumberReader.randomOS(runningProcess.B);
						runningProcess.CPUBurstTime = assignedCPUBurstTime;
					}
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
			while (!blockedProcesses.isEmpty() && blockedProcesses.peek().IOBurstTime == 0) {
				readyProcesses.offer(blockedProcesses.poll());
			}
			// run the process and handle it
			if (runningProcess != null) {
				runningProcess.CPUBurstTime--;
				runningProcess.remainingCPUTime--;
				if (runningProcess.remainingCPUTime == 0) {
					runningProcess.finishingTime = cycle;
					runningProcess.turnaroundTime = cycle - runningProcess.A;
					terminatedProcesses.add(runningProcess);
					runningProcess = null;
				} else if (runningProcess.CPUBurstTime == 0) {
					int assignedIOBurstTime = RandomNumberReader.randomOS(runningProcess.IO);
					runningProcess.IOBurstTime = assignedIOBurstTime;
					blockedProcesses.offer(runningProcess);
					runningProcess = null;
				}
				CPUTime++;
			}
		}
		
		sumTurnaroundTime = sumTurnaround(terminatedProcesses);
		sumWaitingTime = sumWaiting(terminatedProcesses);
		
		System.out.println("The scheduling algorithm used was Preemptive Shortest Job First");
		System.out.println();
		
		Collections.sort(terminatedProcesses, new TerminatedProcessComparator());
		printProcesses(terminatedProcesses);
		printSummary(cycle, CPUTime, IOTime, sumTurnaroundTime, sumWaitingTime, terminatedProcesses);
	}
	
}
