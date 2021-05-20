// Hansen Li
// class for jobs

import java.util.List;
import java.util.Queue;

public class Jobs {
	
	private int[][] taskTimes; // arrival and service time array
	private int size; // total number of tasks
	private List<String> taskNames; // task names

	public Jobs(Queue<Integer> queue, List<String> taskNames) {
		
		this.size = queue.size();
		this.taskNames = taskNames;

		initTaskTimes(queue);
	}

	/**
	 * initialize the arrival and service time array
	 * 
	 * @param queue the arrival and service time queue
	 * @return task time array
	 */
	private void initTaskTimes(Queue<Integer> queue) {
		taskTimes = new int[size / 2][2];

		for (int i = 0; i < size / 2; i++) {
			taskTimes[i][0] = queue.poll();
			taskTimes[i][1] = queue.poll();
		}

	}
}