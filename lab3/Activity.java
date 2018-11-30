
/**
 * This class is used to save the information of activities.
 *
 */
public class Activity {
	
	private final ActivityHeader header; // header
	private final int delay; // delay
	private final int resourceType; // resource-type
	private final int number; // initial-claim, number-requested or number-released
	public Activity(ActivityHeader header, int delay, int resourceType, int number) {
		this.header = header;
		this.delay = delay;
		this.resourceType = resourceType;
		this.number = number;
	}
	// getters
	public ActivityHeader getHeader() {
		return header;
	}
	public int getDelay() {
		return delay;
	}
	public int getResourceType() {
		return resourceType;
	}
	public int getNumber() {
		return number;
	}

}
