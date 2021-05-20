// Hansen Li
// CS 4348 
// Project 2
// Class for the Receptionist

import java.util.concurrent.Semaphore;
import java.util.Queue;

public class Receptionist implements Runnable {
	
	private Semaphore 	patientRegister, 
						registerReady, 
						patientWait, 
						patientReady,
						registerMutex, 
						waitMutex;

	private Queue<Patient> registerQueue;
	private Queue<Patient> waitQueue;
	
	
	boolean runLoop = true;

	public Receptionist(Semaphore patientRegister, 
						Semaphore registerReady, 
						Semaphore patientWait, 
						Semaphore patientReady,
						Semaphore registerMutex, 
						Semaphore waitMutex, 
						Queue<Patient> registerQueue, 
						Queue<Patient> waitQueue) {
		
		this.patientRegister = patientRegister;
		this.registerReady = registerReady;
		this.patientWait = patientWait;
		this.patientReady = patientReady;
		this.registerMutex = registerMutex;
		this.waitMutex = waitMutex;
		this.registerQueue = registerQueue;
		this.waitQueue = waitQueue;
	}

	public void run() {
		while (runLoop) {
			
			// receptionist waits for patient to be registered
			Project2.SemCommand.wait(patientRegister);
			Project2.SemCommand.wait(registerMutex);
			
			// checks front of queue
			Patient patient = registerQueue.poll();			
			int patientNum = patient.getPatientNumber();
			
			// registers patient
			System.out.println("Receptionist registers patient " + patientNum);
			Project2.SemCommand.signal(registerMutex);
			
			Project2.SemCommand.signal(registerReady);
			
			Project2.SemCommand.wait(patientWait);
			
			// adds patient to ready waiting queue
			Project2.SemCommand.wait(waitMutex);
			waitQueue.add(patient);
			Project2.SemCommand.signal(waitMutex);
			
			// signals patient ready to see doctor
			Project2.SemCommand.signal(patientReady);

		}

	}
}
