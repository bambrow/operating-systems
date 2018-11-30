import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public abstract class SchedulingAlgorithm {
	
	protected static class ProcessSortComparator implements Comparator<Process> {
		@Override
		public int compare(Process a, Process b) {
			return (a.A != b.A) ? (a.A - b.A) : (a.ID - b.ID);
		}
	}
	
	protected static class BlockedProcessComparator implements Comparator<Process> {
		@Override
		public int compare(Process a, Process b) {
			return (a.IOBurstTime != b.IOBurstTime) ? (a.IOBurstTime - b.IOBurstTime) : (a.sortedID - b.sortedID);
		}
	}
	
	protected static class TerminatedProcessComparator implements Comparator<Process> {
		@Override
		public int compare(Process a, Process b) {
			return a.sortedID - b.sortedID;
		}
	}
	
	protected static void printOriginal(int numProcesses, List<Process> processes) {
		System.out.print("The original input was: ");
		System.out.print(numProcesses + "  ");
		for (int i = 0; i < numProcesses; i++) {
			processes.get(i).printOverview();
			if (i < numProcesses - 1) {
				System.out.print(" ");
			}
		}
		System.out.println();
	}
	
	protected static void printSorted(int numProcesses, List<Process> processes) {
		System.out.print("The (sorted) input is:  ");
		System.out.print(numProcesses + "  ");
		for (int i = 0; i < numProcesses; i++) {
			processes.get(i).printOverview();
			if (i < numProcesses - 1) {
				System.out.print(" ");
			}
		}
		System.out.println();
	}
	
	protected static void assignSortedID(int numProcesses, List<Process> processes) {
		for (int i = 0; i < numProcesses; i++) {
			processes.get(i).sortedID = i;
		}
	}
	
	protected static void printCycleZero(int numProcesses) {
		if (numProcesses > 0) {
			System.out.print("Before cycle");
			System.out.printf("%5d: ", 0);
			for (int i = 0; i < numProcesses; i++) {
				System.out.print("  unstarted");
				System.out.printf("%3d", 0);
			}
			System.out.println(".");
		}
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
				counter = process.CPUBurstTime;
			} else if (terminatedProcesses.contains(process)) {
				System.out.print(" terminated");
			} else if (blockedProcesses.contains(process)) {
				System.out.print("    blocked");
				counter = process.IOBurstTime;
			} else if (runningProcess == process) {
				System.out.print("    running");
				counter = process.CPUBurstTime;
			}
			System.out.printf("%3d", counter);
		}
		System.out.println(".");
	}
	
	protected static void printProcesses(List<Process> terminatedProcesses) {
		for (Process process : terminatedProcesses) {
			process.printDetail();
			System.out.println();
		}
	}
	
	protected static int sumTurnaround(List<Process> terminatedProcesses) {
		int sum = 0;
		for (Process process : terminatedProcesses) {
			sum += process.turnaroundTime;
		}
		return sum;
	}
	
	protected static int sumWaiting(List<Process> terminatedProcesses) {
		int sum = 0;
		for (Process process : terminatedProcesses) {
			sum += process.waitingTime;
		}
		return sum;
	}
	
	protected static void printSummary(int cycle, int CPUTime, int IOTime, int sumTurnaroundTime, int sumWaitingTime, List<Process> terminatedProcesses) {
		System.out.println("Summary Data:");
		System.out.println("\tFinishing time: " + cycle);
		System.out.printf("\tCPU Utilization: %.6f", 1.0 * CPUTime / cycle);
		System.out.println();
		System.out.printf("\tI/O Utilization: %.6f", 1.0 * IOTime / cycle);
		System.out.println();
		System.out.printf("\tThroughput: %.6f processes per hundred cycles", 1.0 * terminatedProcesses.size() * 100 / cycle);
		System.out.println();
		System.out.printf("\tAverage turnaround time: %.6f", 1.0 * sumTurnaroundTime / terminatedProcesses.size());
		System.out.println();
		System.out.printf("\tAverage waiting time: %.6f", 1.0 * sumWaitingTime / terminatedProcesses.size());
		System.out.println();
	}
	
}
