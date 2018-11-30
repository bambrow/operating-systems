import java.util.ArrayList;

/**
 * This class provides information and saves all data of a single task.
 *
 */
public class Task {

	private final int ID; // task-id
	private int[] initialClaims; // initial claims, used in banker
	private int[] resources; // resources hold
	private ArrayList<Activity> activityList; // list for activities
	private int index; // current index in activity list
	private int delay; // current delay
	private int time; // current time used
	private int wait; // current wait time
	private boolean aborted; // if it's aborted
	private boolean terminated; // if it's terminated
	
	public Task(int ID, int numTasks, int numResources) {
		this.ID = ID;
		initialClaims = new int[numResources + 1];
		resources = new int[numResources + 1];
		activityList = new ArrayList<Activity>(numTasks);
		index = -1;
		delay = 0;
		time = 0;
		wait = 0;
		aborted = false;
		terminated = false;
	}
	
	/**
	 * Get ID for this task.
	 * @return ID
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Add an activity to activity list.
	 * @param header
	 * @param delay
	 * @param resourceType
	 * @param number
	 */
	public void addActivity(ActivityHeader header, int delay, int resourceType, int number) {
		activityList.add(new Activity(header, delay, resourceType, number));
	}
	
	/**
	 * Save an initial claim, used in banker.
	 * @param resourceType
	 * @param claim
	 */
	public void claim(int resourceType, int claim) {
		initialClaims[resourceType] = claim;
	}
	
	/**
	 * Check if the request exceeds the initial claim.
	 * @param resourceType
	 * @param requested
	 * @return true if the requested resources exceed the claim
	 */
	public boolean exceedClaim(int resourceType, int requested) {
		return resources[resourceType] + requested > initialClaims[resourceType];
	}
	
	/**
	 * Whether this tasks has activity left.
	 * @return true if there is activity left
	 */
	public boolean hasNextActivity() {
		return index + 1 < activityList.size();
	}
	
	/**
	 * Peek the next activity, but do not increment the counter.
	 * @return the next activity in list
	 */
	public Activity peekNextActivity() {
		return activityList.get(index + 1);
	}
	
	/**
	 * Get the next activity and increment the counter.
	 * @return the next activity in list
	 */
	public Activity getNextActivity() {
		return activityList.get(++index);
	}
	
	/**
	 * Jump over next activity.
	 */
	public void jumpNextActivity() {
		index++;
	}
	
	/**
	 * Whether this task is in delayed state.
	 * @return true if it's delayed
	 */
	public boolean isDelayed() {
		return delay > 0;
	}
	
	/**
	 * Decrement the delay.
	 */
	public void addDelay() {
		delay--;
	}
	
	/**
	 * Set the delay time before the next activity.
	 */
	public void setDelay() {
		Activity activity = peekNextActivity();
		if (activity != null) {
			delay = activity.getDelay();
		}
	}
	
	/**
	 * Increment time cost.
	 */
	public void addTime() {
		time++;
	}
	
	/**
	 * Get the time cost.
	 * @return time
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * Increment wait time.
	 */
	public void addWait() {
		wait++;
	}
	
	/**
	 * Get the wait time.
	 * @return wait
	 */
	public int getWait() {
		return wait;
	}
	
	/**
	 * Set abort flag.
	 */
	public void abort() {
		aborted = true;
	}
	
	/**
	 * Check if this task is aborted.
	 * @return true if aborted
	 */
	public boolean isAborted() {
		return aborted;
	}
	
	/**
	 * Set terminate flag.
	 */
	public void terminate() {
		terminated = true;
	}
	
	/**
	 * Check if this task is terminated.
	 * @return true if terminated
	 */
	public boolean isTerminated() {
		return terminated;
	}
	
	/**
	 * Release all the resources hold.
	 * @return array indicating all the resources hold
	 */
	public int[] releaseAll() {
		return resources;
	}
	
	/**
	 * Get initial claim array.
	 * @return initial claim array
	 */
	public int[] getClaimArray() {
		return initialClaims;
	}
	
	/**
	 * Assign resource to this task.
	 * @param resourceType
	 * @param number
	 */
	public void assign(int resourceType, int number) {
		resources[resourceType] += number;
	}
	
	/**
	 * Release resource to this task.
	 * @param resourceType
	 * @param number
	 */
	public void release(int resourceType, int number) {
		resources[resourceType] -= number;
	}
	
	/**
	 * Calculate and return the maximum additional requests for the task.
	 * @return an array for maximum additional requests
	 */
	public int[] maxAdditionalRequests() {
		int[] additional = new int[initialClaims.length];
		for (int i = 1; i < initialClaims.length; i++) {
			additional[i] = initialClaims[i] - resources[i];
		}
		return additional;
	}
	
	/**
	 * Reset this task for future use.
	 */
	public void reset() {
		int length = initialClaims.length;
		initialClaims = new int[length];
		resources = new int[length];
		index = -1;
		delay = 0;
		time = 0;
		wait = 0;
		aborted = false;
		terminated = false;
	}
	
	@Override
	public String toString() {
		String idString = String.format("%-9s", "Task " + ID);
		String timeString = String.format("%4d", time);
		String waitString = String.format("%4d", wait);
		String percentageString = String.format("%4d", Math.round(1.0 * 100 * wait / time));
		if (aborted) {
			return "    " + idString + "   " + "aborted   ";
		}
		return "    " + idString + timeString + waitString + percentageString + "%";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
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
		Task other = (Task) obj;
		if (ID != other.ID)
			return false;
		return true;
	}
		
}
