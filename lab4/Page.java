
/**
 * This class encapsulates a single page in frame table.
 *
 */
public class Page {
	
	private final int ID; // page ID
	private final int processID; // process ID that accesses this page
	private final int loadedTime; // page loaded time
	private int recentUsedTime; // last time this page is used
	
	public Page(int ID, int processID, int loadedTime, int recentUsedTime) {
		this.ID = ID;
		this.processID = processID;
		this.loadedTime = loadedTime;
		this.recentUsedTime = recentUsedTime;
	}

	/**
	 * Get ID for this page.
	 * @return ID
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Get the process ID that accesses this page.
	 * @return processID
	 */
	public int getProcessID() {
		return processID;
	}

	/**
	 * Get the loaded time of this page.
	 * @return loadedTime
	 */
	public int getLoadedTime() {
		return loadedTime;
	}

	/**
	 * Get the recent used time of this page.
	 * @return recentUsedTime
	 */
	public int getRecentUsedTime() {
		return recentUsedTime;
	}

	/**
	 * Set the recent used time.
	 * @param recentUsedTime
	 */
	public void setRecentUsedTime(int recentUsedTime) {
		this.recentUsedTime = recentUsedTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		result = prime * result + processID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Page other = (Page) obj;
		if (ID != other.ID)
			return false;
		if (processID != other.processID)
			return false;
		return true;
	}

}
