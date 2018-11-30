import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * FIFO manager class.
 * 
 */
public class FIFOManager extends ResourceManager {
	
	private static List<Task> tasks; // list of unblocked tasks
	private static int[] resources; // array of resources
	private static List<Task> blocked; // list of blocked tasks
	private static int cycle; // current cycle
	
	/**
	 * Run the FIFOManager, satisfying pending requests in a FIFO manner.
	 * @param taskArray
	 * @param resourceArray
	 */
	public static void run(Task[] taskArray, int[] resourceArray) {
		
		// initialization
		tasks = new LinkedList<Task>();
		resources = new int[resourceArray.length];
		blocked = new LinkedList<Task>();
		
		// copies
		addTasks(taskArray, tasks);
		addResources(resourceArray, resources);
		
		// initialize cycle
		cycle = 0;
		
		// while not all terminated
		while (tasks.size() + blocked.size() > 0) {
			
			// System.out.println("cycle " + cycle + "-" + (cycle + 1));
			
			// initialize release array for next cycle
			int[] released = new int[resources.length];
			
			// detect deadlock
			while (detectDeadlock(released)) {
				
				// find the task with smallest ID
				Task toDelete = new Task(taskArray.length, 0, 0);
				for (Task task : tasks) {
					if (task.getID() < toDelete.getID()) {
						toDelete = task;
					}
				}
				for (Task task : blocked) {
					if (task.getID() < toDelete.getID()) {
						toDelete = task;
					}
				}
				
				// abort that task and release resources
				addReleasedResources(released, toDelete.releaseAll());
				tasks.remove(toDelete);
				blocked.remove(toDelete);
				toDelete.abort();
				toDelete.terminate();
				System.out.println("During cycle " + cycle + "-" + (cycle + 1) + " of FIFO algorithm");
				System.out.print("    ");
				System.out.println("Deadlock occurs; abort task " + toDelete.getID());
				// System.out.println("abort task! " + toDelete.getID());
				
			}
			
			// try to satisfy blocked tasks first
			List<Task> toTasks = new LinkedList<Task>();
			{
				
				Iterator<Task> taskIter = blocked.iterator();
				
				while (taskIter.hasNext()) {
					
					// go through every task
					Task task = taskIter.next();
					Activity activity = task.peekNextActivity();
					int resourceType = activity.getResourceType();
					int number = activity.getNumber();
					
					// if the request can be granted, do it; otherwise continue waiting
					if (resources[resourceType] >= number) {
						task.jumpNextActivity();
						task.assign(resourceType, number);
						resources[resourceType] -= number;
						task.setDelay();
						toTasks.add(task);
						taskIter.remove();
						// System.out.println("unblocked! " + task.getID());
					} else {
						task.addWait();
						// System.out.println("continue blocked! " + task.getID());
					}
					
					task.addTime();
					
				}
				
			}
			
			// handle unblocked tasks
			{
				
				Iterator<Task> taskIter = tasks.iterator();
				
				while (taskIter.hasNext()) {
					
					// go through every task
					Task task = taskIter.next();
					
					// if the task is not delayed, read the next activity; otherwise reduce the delay time
					if (!task.isDelayed()) {
						
						// if next activity is initiate, ignore
						// if next activity is terminate, remove
						// if next activity is request, try to satisfy the request
						// if next activity is release, do the release
						if (initiateNext(task)) {
							// System.out.println("initiated! " + task.getID());
							task.jumpNextActivity();
							task.setDelay();
							task.addTime();
						} else if (terminateNext(task)) {
							// System.out.println("terminated! " + task.getID());
							task.terminate();
							taskIter.remove();
						} else if (requestNext(task)) {
							
							Activity activity = task.peekNextActivity();
							int resourceType = activity.getResourceType();
							int number = activity.getNumber();
							
							// if the request can be granted, do it; otherwise add to blocked list
							if (resources[resourceType] >= number) {
								task.jumpNextActivity();
								task.assign(resourceType, number);
								resources[resourceType] -= number;
								task.setDelay();
								// System.out.println("requested! " + task.getID());
							} else {
								blocked.add(task);
								taskIter.remove();
								task.addWait();
								// System.out.println("blocked! " + task.getID());
							}
							
							task.addTime();
							
						} else if (releaseNext(task)) {
							// System.out.println("released! " + task.getID());
							Activity activity = task.getNextActivity();
							int resourceType = activity.getResourceType();
							int number = activity.getNumber();
							task.release(resourceType, number);
							released[resourceType] += number;
							task.setDelay();
							task.addTime();
						}
						
					} else {
						// System.out.println("delayed! " + task.getID());
						task.addDelay();
						task.addTime();
					}
					
				}
				
			}
			
			// add newly unblocked tasks to task list and do the sorting
			tasks.addAll(toTasks);
			Collections.sort(tasks, new TaskIDComparator());
			
			// add released recourses back
			addReleasedResources(resources, released);
			
			// add cycle
			cycle++;
			
		}
		
	}
	
	/**
	 * Detect if there is deadlock for the current state.
	 * @param released
	 * @return true if deadlock exists
	 */
	private static boolean detectDeadlock(int[] released) {
		// prepare for the available resources
		int[] total = new int[resources.length];
		addReleasedResources(total, resources);
		addReleasedResources(total, released);
		// for every task in task list
		for (Task task : tasks) {
			// if it is delayed, then no deadlock
			if (task.isDelayed()) {
				return false;
			}
			// if next activity is not request, then no deadlock
			if (!requestNext(task)) {
				return false;
			}
			// if the request can be granted, then no deadlock
			Activity activity = task.peekNextActivity();
			int resourceType = activity.getResourceType();
			int number = activity.getNumber();
			if (total[resourceType] >= number) {
				return false;
			}
		}
		// for every task in blocked list
		for (Task task : blocked) {
			// if the request can be granted, then no deadlock
			Activity activity = task.peekNextActivity();
			int resourceType = activity.getResourceType();
			int number = activity.getNumber();
			if (total[resourceType] >= number) {
				return false;
			}
		}
		// if non of the above satisfies, then there is a deadlock!
		// System.out.println("deadlock!");
		return true;
	}
	
}
