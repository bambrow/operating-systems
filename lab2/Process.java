
public class Process {
	
	public final int ID;

	public final int A;
	public final int B;
	public final int C;
	public final int IO;
	
	public int sortedID = 0;
	
	public int CPUBurstTime = 0;
	public int IOBurstTime = 0;
	public int remainingCPUTime;
	
	public int finishingTime = 0;
	public int turnaroundTime = 0;
	public int IOTime = 0;
	public int waitingTime = 0;
		
	public Process(int id, int a, int b, int c, int io) {
		ID = id;
		A = a;
		B = b;
		C = c;
		IO = io;
		remainingCPUTime = C;
	}
	
	public void reset() {
		sortedID = 0;
		CPUBurstTime = 0;
		IOBurstTime = 0;
		remainingCPUTime = C;
		finishingTime = 0;
		turnaroundTime = 0;
		IOTime = 0;
		waitingTime = 0;
	}
	
	public void printOverview() {
		System.out.print(A + " ");
		System.out.print(B + " ");
		System.out.print(C + " ");
		System.out.print(IO + " ");
	}
	
	public void printDetail() {
		System.out.println("Process " + sortedID + ":");
		System.out.println("\t(A,B,C,IO) = (" + A + "," + B + "," + C + "," + IO + ")");
		System.out.println("\tFinishing time: " + finishingTime);
		System.out.println("\tTurnaround time: " + turnaroundTime);
		System.out.println("\tI/O time: " + IOTime);
		System.out.println("\tWaiting time: " + waitingTime);
	}
	
}
