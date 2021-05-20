// Hansen Li
// CS 4348 
// Project 2
// Class for the Doctor and Nurse pair

import java.util.concurrent.Semaphore;
import java.util.Queue;

public class DoctorNursePair implements Runnable {
	
	private Semaphore 	maxDoctors, 
						patientReady, 
						nurseCall, 
						patientCalled, 
						doctorAdvises, 
						receptionistReady,
						waitMutex;
	
	private Semaphore[] patientFinished;
	
	private int doctorNumber;
		
	private Queue<Patient> waitQueue;
	
	boolean runLoop = true;

	// constructor
	public DoctorNursePair(	int doctorNumber, 
							Semaphore maxDoctors, 
							Semaphore patientReady, 
							Semaphore nurseCall,
							Semaphore patientCalled, 
							Semaphore doctorAdvises, 
							Semaphore receptionistReady, 
							Semaphore waitMutex,
							Queue<Patient> waitQueue, 
							Semaphore[] patientFinished) {
		
		this.doctorNumber = doctorNumber;
		this.maxDoctors = maxDoctors;
		this.patientReady = patientReady;
		this.nurseCall = nurseCall;
		this.patientCalled = patientCalled;
		this.doctorAdvises = doctorAdvises;
		this.receptionistReady = receptionistReady;
		this.waitMutex = waitMutex;
		this.waitQueue = waitQueue;
		this.patientFinished = patientFinished;
	}

	// loop
	public void run() {
		
		while (runLoop) {

			// doctor and nurse waiting for patient to be ready
			Project2.SemCommand.wait(patientReady);
			// wait until not all doctors are occupied
			Project2.SemCommand.wait(maxDoctors);
			// signal receptionist
			Project2.SemCommand.signal(receptionistReady);

			// check patient waiting queue
			Project2.SemCommand.wait(waitMutex);
			Patient patient = waitQueue.poll();
			
			int patientNum = patient.getPatientNumber();
			patient.assignDoctor(doctorNumber);
			
			System.out.println("Nurse " + doctorNumber + " takes patient " + patientNum + " to doctor's office");
			Project2.SemCommand.signal(waitMutex);

			// signals nurse to call patient
			Project2.SemCommand.signal(nurseCall);
			
			// wait for patient to arrive
			Project2.SemCommand.wait(patientCalled);
			
			// hears symptoms and advises patient
			System.out.println("Doctor " + doctorNumber + " listens to symptoms from patient " + patientNum);
			Project2.SemCommand.signal(doctorAdvises);

			// waits for patient to finish and leave
			Project2.SemCommand.wait(patientFinished[patientNum]);
			// doctor available
			Project2.SemCommand.signal(maxDoctors);

		}

	}
}
