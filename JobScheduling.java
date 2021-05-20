// Hansen Li
// main class for Project 3
// First Come First Server
// Shortest Process Next
// Highest Response Ratio Next
// Notes: advice received to use HashMaps instead of just arrays and lists to organize jobs, 


import java.io.*;
import java.util.*;

public class JobScheduling {

	List<String> names;
	int numberOfJobs;
	int[][] jobArray;

	// organize jobs and time intervals
	public JobScheduling(List<String> names, List<Integer> arrivals, List<Integer> durations) {
		this.names = names;
		this.numberOfJobs = names.size();

		jobArray = new int[numberOfJobs][2];

		for (int i = 0; i < numberOfJobs; i++) {
			jobArray[i][0] = arrivals.get(i);
			jobArray[i][1] = durations.get(i);

		}
	}

	public static void main(String[] args) {

		List<String> jobNames = new ArrayList<>();
		List<Integer> arrivalTimes = new ArrayList<>();
		List<Integer> durationTimes = new ArrayList<>();

		System.out.println("Ensure that file is in same folder as project.\n");

		String filename = "jobs.txt";

		Scanner filesc;
		try {
			filesc = new Scanner(new File(filename));

			// iterate file and get all job info
			while (filesc.hasNextLine()) {
				String[] lineSplit = filesc.nextLine().split("\\t");

				jobNames.add(lineSplit[0]);
				arrivalTimes.add(Integer.valueOf(lineSplit[1]));
				durationTimes.add(Integer.valueOf(lineSplit[2]));

			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}

		// create class to organize jobs and times
		JobScheduling jobComp = new JobScheduling(jobNames, arrivalTimes, durationTimes);

		// create map for FCFS alg
		HashMap<String, List<Integer>> mappedFCFS = jobComp.FCFS(jobNames, arrivalTimes, durationTimes);
		
		System.out.println("FCFS\n-----");

		// print block for FCFS
		for (int j = 0; j < mappedFCFS.size(); j++) {

			String currJobName = jobNames.get(j);
			
			System.out.print(currJobName + " ");
			
			List<Integer> jobTimes = mappedFCFS.get(currJobName);
			
			int time = 0;
			int start = jobTimes.get(0);
			int end = jobTimes.get(1);
			
			for (int k = 0; k < start - time; k++) {
				System.out.print(" ");
			}
			
			for (int k = 0; k < end - start; k++) {
				System.out.print("X");
			}
			
			time = end;
			
			System.out.println();
		}
		
		System.out.println("=====");
		
		// create hashmap for SPN alg
		HashMap<String, List<Integer>>  mappedSPN = jobComp.SPN(jobNames, arrivalTimes, durationTimes);
		
		System.out.println("SPN\n-----");

		// print block for SPN
		for (int j = 0; j < mappedSPN.size(); j++) {
			
			String currJobName = jobNames.get(j);
			
			System.out.print(currJobName + " ");
			
			List<Integer> jobTimes = mappedSPN.get(currJobName);
			
			int time = 0;	
			int start = jobTimes.get(0);
			int end = jobTimes.get(1);
			
			for (int k = 0; k < start - time; k++) {
				System.out.print(" ");
			}
			
			for (int k = 0; k < end - start; k++) {
				System.out.print("X");
			}
			
			time = end;
			
			System.out.println();
		}
		
		System.out.println("=====");
		
		// create hashmap for HRRN alg
		HashMap<String, List<Integer>>  mappedHRRN = jobComp.HRRN(jobNames, arrivalTimes, durationTimes);
		
		System.out.println("HRRN\n-----");

		// print block for HRRN
		for (int j = 0; j < mappedHRRN.size(); j++) {
			
			String currJobName = jobNames.get(j);
			
			System.out.print(currJobName + " ");
			
			List<Integer> jobTimes = mappedHRRN.get(currJobName);
			
			int time = 0;
			int start = jobTimes.get(0);
			int end = jobTimes.get(1);
			
			for (int k = 0; k < start - time; k++) {
				System.out.print(" ");
			}
			
			for (int k = 0; k < end - start; k++) {
				System.out.print("X");
			}
			
			time = end;
			
			System.out.println();
		}
	}

	// method for FCFS alg
	public HashMap<String, List<Integer>> FCFS(List<String> jobs, List<Integer> arrivals, List<Integer> durations) {

		// initialize return map
		HashMap<String, List<Integer>> jobIntervals = new HashMap<>();

		// create 2d array to organize job times
		int currJob = jobArray[0][0];

		// iterate through each job
		for (int i = 0; i < numberOfJobs; i++) {

			String jobName = jobs.get(i);
			
			// create array, store timestamps
			List<Integer> jobTimes = new ArrayList<>();
			jobTimes.add(currJob);
			jobTimes.add(currJob + jobArray[i][1]);

			jobIntervals.put(jobName, jobTimes);

			// check to avoid out of bounds index
			if (i != jobArray.length - 1) {

				// assign currJob comparing index and times
				if (jobArray[i + 1][0] > currJob + jobArray[i][1])
					currJob = jobArray[i + 1][0];
				else
					currJob += jobArray[i][1];
			}
		}

		return jobIntervals;

	}

	// method for SPN alg
	public HashMap<String, List<Integer>> SPN(List<String> jobs, List<Integer> arrivals, List<Integer> durations) {
		
		// initialize return map
		HashMap<String, List<Integer>> jobIntervals = new HashMap<>();
		
		// create array to keep track of job status
		int[] jobComplete = new int[numberOfJobs];
		
		// initialize currJob
		int currJob = jobArray[0][0] + jobArray[0][1];
		
		List<Integer> jobTimes = new LinkedList<>();
		
		jobTimes.add(jobArray[0][0]);
		jobTimes.add(jobArray[0][0] + jobArray[0][1]);
		
		jobIntervals.put(jobs.get(0), jobTimes);
		
		// mark job status
		jobComplete[0] = 1;
		
		int jobFinCount = 1;
		
		// loop until jobs are done
		while (jobFinCount < numberOfJobs) {
			
			// get next job
			String nextSPNJob = jobSelectSPN(jobComplete, currJob);
			
			// fetch index position of chosen job
			int index = names.indexOf(nextSPNJob);
			
			// check if job arrival time greater than current job; current job time to next job
			if (jobArray[index][0] > currJob) {
				currJob = jobArray[index][0];
			}
			
			List<Integer> jobTimeOrder = new LinkedList<>();
			
			jobTimeOrder.add(currJob);
			jobTimeOrder.add(currJob + jobArray[index][1]);
			
			jobIntervals.put(nextSPNJob, jobTimeOrder);
			
			currJob += jobArray[index][1];
			
			// increment completion count
			jobFinCount++;
			// mark job complete 
			jobComplete[index] = 1;
			
		}

		return jobIntervals;
	}

	// method for SPN next job calc
	public String jobSelectSPN(int[] jobStatus, int currJob) {
		
		String nextJob = "";
		int maxTime = 0;
		
		// get max possible time by summing all arrival and duration times
		for (int i = 0; i < numberOfJobs; i++) {
			maxTime += jobArray[i][0] + jobArray[i][1];
		}
		
		// iterate through jobs to check time, status
		for (int i = 0; i < numberOfJobs; i++) {
			
			// check job completion status, check job time iterval
			if (jobStatus[i] != 1 && currJob >= jobArray[i][0]) {
				
				// check job duration vs max time
				if (jobArray[i][1] < maxTime) {
					// set max time to duration
					maxTime = jobArray[i][1];
					nextJob = names.get(i);
				}
			// else check job complete, job preceding time	
			} else if (jobStatus[i] != 1 && currJob < jobArray[i][0]) {
				
				// set next job available
				if (nextJob.isEmpty())
					nextJob = names.get(i);
				
				break;
			}
		}

		return nextJob;
	}
	
	
	// method for HRRN alg
	public HashMap<String, List<Integer>> HRRN(List<String> jobs, List<Integer> arrivals, List<Integer> durations) {

		// initialize return map
		HashMap<String, List<Integer>> jobIntervals = new HashMap<>();
		
		// create array to keep track of job status
		int[] jobComplete = new int[numberOfJobs];
		
		List<Integer> jobTimes = new LinkedList<>();
		
		// initialize currJob
		int currJob = jobArray[0][0] + jobArray[0][1];
		
		jobTimes.add(jobArray[0][0]);
		jobTimes.add(jobArray[0][0] + jobArray[0][1]);
		
		jobIntervals.put(jobs.get(0), jobTimes);
		
		// mark job status
		jobComplete[0] = 1;
		
		int jobFinCount = 1;
		
		// loop until jobs are done
		while (jobFinCount < numberOfJobs) {
			
			// get next job
			String nextHRRNJob = jobSelectHRRN(jobComplete, currJob);
			
			// fetch index position of chosen job
			int index = names.indexOf(nextHRRNJob);		
			
			// check if job arrival time greater than current job; current job time to next job
			if (jobArray[index][0] > currJob) {
				currJob = jobArray[index][0];
			}
			
			List<Integer> jobTimeOrder = new LinkedList<>();
			
			jobTimeOrder.add(currJob);
			jobTimeOrder.add(currJob + jobArray[index][1]);
			
			jobIntervals.put(nextHRRNJob, jobTimeOrder);
			
			currJob += jobArray[index][1];
			
			// increment completion count
			jobFinCount++;
			// mark job complete 
			jobComplete[index] = 1;
		}

		return jobIntervals;
	}

	//method for HRRN next job calc
	private String jobSelectHRRN(int[] jobStatus, int currJob) {
		
		String nextJob = "";
		double currHighRatio = 0;
		
		// iterate through jobs to check time, status
		for (int i = 0; i < numberOfJobs; i++) {
			
			// check job completion status, check job time iterval
			if (jobStatus[i] != 1 && currJob >= jobArray[i][0]) {
				
				// calculate ratio for current job
				double num = (currJob - jobArray[i][0] + jobArray[i][1]);
				double den = (double) jobArray[i][1];
				double ratio =  num / den;
								
				
				// if ratio is highest, replace
				if (ratio > currHighRatio) {
					nextJob = names.get(i);
					currHighRatio = ratio;
				}
				
			// else check job complete, job preceding time	
			} else if (jobStatus[i] != 1 && currJob < jobArray[i][0]) {
				
				// set next job available
				if (nextJob.isEmpty())
					nextJob = names.get(i);
				
				break;
			}
		}

		return nextJob;
	}
}
