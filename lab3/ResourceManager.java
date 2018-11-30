import java.util.Comparator;
import java.util.List;

/**
 * This is the base class for FIFO and Banker manager classes.
 *
 */
public abstract class ResourceManager {
	
	/**
	 * This is the Comparator for task according to ID.
	 *
	 */
	protected static class TaskIDComparator implements Comparator<Task> {
		@Override
		public int compare(Task a, Task b) {
			return a.getID() - b.getID();
		}
	}
	
	/**
	 * Copy all tasks from the task array to the list.
	 * @param taskArray
	 * @param tasks
	 */
	protected static void addTasks(Task[] taskArray, List<Task> tasks) {
		for (int i = 1; i < taskArray.length; i++) {
			tasks.add(taskArray[i]);
		}
	}
	
	/**
	 * Copy all resources from the resource array to the new array.
	 * @param resourceArray
	 * @param resources
	 */
	protected static void addResources(int[] resourceArray, int[] resources) {
		for (int i = 1; i < resourceArray.length; i++) {
			resources[i] = resourceArray[i];
		}
	}
	
	/**
	 * Whether the next activity for the task is initiate.
	 * @param task
	 * @return true if initiate is the next activity
	 */
	protected static boolean initiateNext(Task task) {
		return task.hasNextActivity() && task.peekNextActivity().getHeader() == ActivityHeader.INITIATE;
	}
	
	/**
	 * Whether the next activity for the task is request.
	 * @param task
	 * @return true if request is the next activity
	 */
	protected static boolean requestNext(Task task) {
		return task.hasNextActivity() && task.peekNextActivity().getHeader() == ActivityHeader.REQUEST;
	}
	
	/**
	 * Whether the next activity for the task is release.
	 * @param task
	 * @return true if release is the next activity
	 */
	protected static boolean releaseNext(Task task) {
		return task.hasNextActivity() && task.peekNextActivity().getHeader() == ActivityHeader.RELEASE;
	}
	
	/**
	 * Whether the next activity for the task is terminate.
	 * @param task
	 * @return true if terminate is the next activity
	 */
	protected static boolean terminateNext(Task task) {
		return task.hasNextActivity() && task.peekNextActivity().getHeader() == ActivityHeader.TERMINATE;
	}
	
	/**
	 * Recycle allocated resources to the resource pool.
	 * @param resources
	 * @param released
	 */
	protected static void addReleasedResources(int[] resources, int[] released) {
		for (int i = 1; i < resources.length; i++) {
			resources[i] += released[i];
		}
	}
	
}
