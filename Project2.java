// Hansen Li
// CS 4348 
// Project 2
// Notes: Print statement wording taken from Project 2 document and examples from textbook used.
// Assitance from stackoverflow examples and geeksforgeeks for semaphores and equivalent mutexes

import java.util.concurrent.Semaphore;
import java.util.Scanner;
import java.io.BufferedInputStream;
import java.util.Queue;
import java.util.LinkedList;

public class Project2 {

	private static Semaphore receptionistReady = new Semaphore(1);

	private static Semaphore patientRegister = new Semaphore(0);
	private static Semaphore patientWait = new Semaphore(0);
	private static Semaphore patientReady = new Semaphore(0);
	private static Semaphore patientCalled = new Semaphore(0);
	private static Semaphore[] patientFinished;

	private static Semaphore registerReady = new Semaphore(0);

	private static Semaphore nurseCall = new Semaphore(0);
	private static Semaphore doctorAdvises = new Semaphore(0);

	// help received from CS tutor for semaphores for queues
	private static Semaphore registerMutex = new Semaphore(1);
	private static Semaphore waitMutex = new Semaphore(1);
	private static Queue<Patient> registerQueue = new LinkedList<>();
	private static Queue<Patient> waitQueue = new LinkedList<>();

	public static void main(String[] args) {
		int patientNumber = 0;
		int doctorNumber = 0;
		// int input;

		// user input block
		Scanner sc = new Scanner(System.in);

		while (patientNumber > 30 || patientNumber < 1) {
			System.out.println("Input number of patients between 1 and 30 inclusive: ");
			patientNumber = sc.nextInt();
			if (patientNumber > 30 || patientNumber < 1)
				System.out.println("Invalid number of patients.");

		}

		while (doctorNumber > 3 || doctorNumber < 1) {
			System.out.println("Input number of doctors between 1 and 3 inclusive: ");
			doctorNumber = sc.nextInt();
			if (doctorNumber > 3 || doctorNumber < 1) {
				System.out.println("Invalid number of doctors.");
			}

			sc.close();

			System.out.println("Run with " + patientNumber + " patients, " + doctorNumber + " nurses, " + doctorNumber
					+ " doctors\n");

		}

		// create receptionist thread and begin
		Receptionist receptionist = new Receptionist(patientRegister, registerReady, patientWait, patientReady, registerMutex,
				waitMutex, registerQueue, waitQueue);

		Thread receptionThread = new Thread(receptionist);

		receptionThread.start();

		// create patients
		Patient[] patients = new Patient[patientNumber];

		// create finished semaphores for each patient
		patientFinished = new Semaphore[patientNumber];

		for (int i = 0; i < patientNumber; i++)
			patientFinished[i] = new Semaphore(0);

		// create patient threads and begin
		Thread[] patientThread = new Thread[patientNumber];
		
		for (int i = 0; i < patientNumber; i++) {

			patients[i] = new Patient(i, patientRegister, registerReady, patientWait, nurseCall, patientCalled, doctorAdvises,
					registerMutex, receptionistReady, registerQueue, patientFinished);

			patientThread[i] = new Thread(patients[i]);

			patientThread[i].start();
		}

		// create semaphores for each doctor
		Semaphore maxDoctors = new Semaphore(doctorNumber);

		// create doctors and nurses
		DoctorNursePair[] doctorNursePair = new DoctorNursePair[doctorNumber];

		// create doctor and nurse threads and begin
		Thread[] doctorNurseThread = new Thread[doctorNumber];

		for (int i = 0; i < doctorNumber; i++) {

			doctorNursePair[i] = new DoctorNursePair(i, maxDoctors, patientReady, nurseCall, patientCalled, doctorAdvises,
					receptionistReady, waitMutex, waitQueue, patientFinished);

			doctorNurseThread[i] = new Thread(doctorNursePair[i]);

			doctorNurseThread[i].start();
		}

		for (int i = 0; i < patientNumber; i++) {

			try {
				patientThread[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		System.exit(0);

	}

	// semaphore wait and signal functions
	public static class SemCommand {

		public static void wait(Semaphore sem) {
			try {
				sem.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public static void signal(Semaphore sem) {
			sem.release();

		}

//		public static void status(Semaphore sem) {
//			System.out.println("Sem status: " + sem);
//		}

	}

}
