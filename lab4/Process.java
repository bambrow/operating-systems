
/**
 * This class encapsulates a single process.
 *
 */
public class Process {
	
	private final int ID; // ID
	private int address; // next address
	private int faults; // fault count
	private int evictions; // eviction count
	private int residencyTime; // residency time sum
	
	private final double A; // A probability
	private final double B; // B probability
	private final double C; // C probability
	private final int S; // size of process, i.e. the references are to virtual addresses 0..S-1
	
	public Process(int ID, double A, double B, double C, int S) {
		this.ID = ID;
		this.A = A;
		this.B = B;
		this.C = C;
		this.S = S;
		this.address = (111 * ID) % S; // initial address
		this.faults = 0;
		this.evictions = 0;
		this.residencyTime = 0;
	}

	/**
	 * Get the ID of process.
	 * @return ID
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Get the next address.
	 * @return address
	 */
	public int getAddress() {
		return address;
	}

	/**
	 * Get the fault count.
	 * @return faults
	 */
	public int getFaults() {
		return faults;
	}

	/**
	 * Get the eviction count.
	 * @return evictions
	 */
	public int getEvictions() {
		return evictions;
	}

	/**
	 * Get the total residency time.
	 * @return residencyTime
	 */
	public int getResidencyTime() {
		return residencyTime;
	}
	
	/**
	 * Increment the fault count.
	 */
	public void incrementFaults() {
		faults++;
	}
	
	/**
	 * Increment the eviction count.
	 */
	public void incrementEvictions() {
		evictions++;
	}
	
	/**
	 * Add residency time to the total sum.
	 * @param residencyTime
	 */
	public void addResidencyTime(int residencyTime) {
		this.residencyTime += residencyTime;
	}
	
	/**
	 * Calculate the next address.
	 */
	public void nextAddress() {
		Address addr = RandomNumberReader.randomNextWord(A, B, C);
		if (addr == Address.HIGHER_ONE) {
			address = (address + 1) % S;
		} else if (addr == Address.LOWER_NEARBY) {
			address = (address - 5 + S) % S;
		} else if (addr == Address.HIGHER_NEARBY) {
			address = (address + 4) % S;
		} else {
			address = RandomNumberReader.randomInteger(S);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Process " + ID + " had " + faults + " faults");
		if (evictions > 0) {
			str.append(" and " + (1.0 * residencyTime / evictions) + " average residency.");
		} else {
			str.append(".\n     With no evictions, the average residence is undefined.");
		}
		return str.toString();
	}

}
