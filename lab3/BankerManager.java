import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Banker manager class.
 *
 */
public class BankerManager extends ResourceManager {

	private static List<Task> tasks; // list of unblocked tasks
	private static int[] resources; // array of resources
	private static List<Task> blocked; // list of blocked tasks
	private static int cycle; // current cycle

	/**
	 * Run the BankerManager, using the banker's algorithm of Dijkstra.
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
					
					// if the request is possible, pretend to grant it
					// and check if the resulting state is safe
					if (resources[resourceType] >= number) {
						
						task.assign(resourceType, number);
						resources[resourceType] -= number;
						ArrayList<Task> processes = new ArrayList<Task>();
						processes.addAll(blocked);
						processes.addAll(tasks);
						processes.addAll(toTasks);
						int[] available = Arrays.copyOf(resources, resources.length);
						
						// check the resulting state, if it is safe
						// then truly grant it
						// otherwise return the resources back and block the task
						if (isSafeState(processes, available)) {
							task.jumpNextActivity();
							task.setDelay();
							toTasks.add(task);
							taskIter.remove();
							// System.out.println("unblocked! " + task.getID());
						} else {
							task.release(resourceType, number);
							resources[resourceType] += number;
							task.addWait();
							// System.out.println("not safe! " + task.getID());
						}
						
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
						
						// if next activity is initiate, check if it is valid, not valid initialization will be aborted
						// if next activity is terminate, remove
						// if next activity is request, try to satisfy the request
						// if next activity is release, do the release
						if (initiateNext(task)) {
							
							Activity activity = task.getNextActivity();
							int resourceType = activity.getResourceType();
							int number = activity.getNumber();
							
							// if the initial claims exceeds the resources present, abort the task
							// otherwise make the claim
							if (number > resourceArray[resourceType]) {
								addReleasedResources(released, task.releaseAll());
								taskIter.remove();
								task.abort();
								task.terminate();
								System.out.println("During cycle " + cycle + "-" + (cycle + 1) + " of Banker's algorithm");
								System.out.print("    ");
								System.out.println("Banker aborts task " + task.getID() + " before run begins:");
								System.out.print("    ");
								System.out.println("claim for resource " + resourceType + " (" + number + ")" + " exceeds number of units present (" + resourceArray[resourceType] + ")");
								// System.out.println("abort task! " + toDelete.getID());
							} else {
								task.claim(resourceType, number);
								task.setDelay();
								task.addTime();
								// System.out.println("initiated! " + task.getID());
							}
							
						} else if (terminateNext(task)) {
							// System.out.println("terminated! " + task.getID());
							task.terminate();
							taskIter.remove();
						} else if (requestNext(task)) {
							
							Activity activity = task.peekNextActivity();
							int resourceType = activity.getResourceType();
							int number = activity.getNumber();
							
							// if the request exceed its claims, abort the task
							// otherwise continue to check safe state
							if (task.exceedClaim(resourceType, number)) {
								addReleasedResources(released, task.releaseAll());
								taskIter.remove();
								task.abort();
								task.terminate();
								System.out.println("During cycle " + cycle + "-" + (cycle + 1) + " of Banker's algorithm");
								System.out.print("    ");
								System.out.println("Task " + task.getID() + "'s request exceeds its claim; aborted");
								System.out.print("    ");
								System.out.println("request for resource " + resourceType + " (" + (task.releaseAll()[resourceType] + number) + ")" + " exceeds its claim (" + task.getClaimArray()[resourceType] + ")");
								// System.out.println("abort task! " + toDelete.getID());
							} else {
								
								// if the request is possible, pretend to grant it
								// and check if the resulting state is safe
								if (resources[resourceType] >= number) {
									
									task.assign(resourceType, number);
									resources[resourceType] -= number;
									ArrayList<Task> processes = new ArrayList<Task>();
									processes.addAll(blocked);
									processes.addAll(tasks);
									processes.addAll(toTasks);
									int[] available = Arrays.copyOf(resources, resources.length);
									
									// check the resulting state, if it is safe
									// then truly grant it
									// otherwise return the resources back and block the task
									if (isSafeState(processes, available)) {
										task.jumpNextActivity();
										task.setDelay();
										// System.out.println("requested! " + task.getID());
									} else {
										task.release(resourceType, number);
										resources[resourceType] += number;
										blocked.add(task);
										taskIter.remove();
										task.addWait();
										// System.out.println("not safe! " + task.getID());
									}
									
								} else {
									blocked.add(task);
									taskIter.remove();
									task.addWait();
									// System.out.println("blocked! " + task.getID());
								}
								
								task.addTime();
								
							}
							
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
	 * Check if the current state is safe using banker's algorithm.
	 * @param processes
	 * @param available
	 * @return true if the current state is safe
	 */
	private static boolean isSafeState(ArrayList<Task> processes, int[] available) {
		// if there is no processes remaining, the state is safe
		if (processes.size() == 0) {
			return true;
		}
		// try to find a process that can be terminated
		for (int i = 0; i < processes.size(); i++) {
			Task task = processes.get(i);
			// if we find this process, pretend that it has been terminated and release the resources
			// and continue to check if the resulting state is safe
			// if all processes can terminate, then the state is truly safe
			if (canTerminate(task, available)) {
				// remove the process from the process list and release the resources
				processes.remove(i);
				addReleasedResources(available, task.releaseAll());
				// recursive check
				if (isSafeState(processes, available)) {
					return true;
				}
				// backtracking; restoring the state
				removeReleasedResources(available, task.releaseAll());
				processes.add(i, task);
			}
		}
		// no safe state possible
		return false;
	}

	/**
	 * Check if the task can terminate given available resources and additional resources that may be requested.
	 * @param available
	 * @param additional
	 * @return true if the task can terminate
	 */
	private static boolean canTerminate(int[] available, int[] additional) {
		for (int i = 1; i < available.length; i++) {
			if (additional[i] > available[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the task can terminate given the task and the available resources.
	 * @param task
	 * @param available
	 * @return true if the task can terminate.
	 */
	private static boolean canTerminate(Task task, int[] available) {
		return canTerminate(available, task.maxAdditionalRequests());
	}

	/**
	 * Undo the release of resources.
	 * @param resources
	 * @param released
	 */
	private static void removeReleasedResources(int[] resources, int[] released) {
		for (int i = 1; i < resources.length; i++) {
			resources[i] -= released[i];
		}
	}

}
