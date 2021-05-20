// Hansen Li
// CS 4348 
// Project 2
// Class for the Patient

import java.util.concurrent.Semaphore;
import java.util.Queue;

public class Patient implements Runnable {
	
	private int patientNumber;
	private int doctorNumber;
	
	private Semaphore receptionistReady;

	private Queue<Patient> registerQueue;

	
	private Semaphore 	patientRegister, 
						registerReady, 
						patientWait, 
						nurseCall, 
						patientCalled, 
						doctorAdvises,
						registerMutex;
	
	private Semaphore[] patientFinished;

	public Patient(	int patientNumber, 
					Semaphore patientRegister, 
					Semaphore registerReady, 
					Semaphore patientWait, 
					Semaphore nurseCall,
					Semaphore patientCalled, 
					Semaphore doctorAdvises, 
					Semaphore registerMutex, 
					Semaphore receptionistReady,
					Queue<Patient> registerQueue, 
					Semaphore[] patientFinished) {

		this.patientNumber = patientNumber;
		this.patientRegister = patientRegister;
		this.registerReady = registerReady;
		this.patientWait = patientWait;
		this.nurseCall = nurseCall;
		this.patientCalled = patientCalled;
		this.doctorAdvises = doctorAdvises;
		this.registerMutex = registerMutex;
		this.receptionistReady = receptionistReady;
		this.registerQueue = registerQueue;
		this.patientFinished = patientFinished;
	}

	// assign patient a doctor
	public void assignDoctor(int doctorNumber) {
		this.doctorNumber = doctorNumber;
	}

	// return patient number identity
	public int getPatientNumber() {
		return this.patientNumber;
	}
	
	// return patient's doctor
	public int getDoctorNumber() {
		return doctorNumber;
	}

	public void run() {

		System.out.println("Patient " + patientNumber + " enters waiting room, waits for receptionist");

		// wait for receptionist to be ready
		Project2.SemCommand.wait(receptionistReady);
		
		// add to registration queue
		Project2.SemCommand.wait(registerMutex);
		registerQueue.add(this);
		Project2.SemCommand.signal(registerMutex);
		
		// patient ready to be registered
		Project2.SemCommand.signal(patientRegister);

		// waiting for registration
		Project2.SemCommand.wait(registerReady);
		
		// patient waiting
		System.out.println("Patient " + patientNumber + " leaves receptionist and sits in waiting room");
		Project2.SemCommand.signal(patientWait);

		// waiting for nurse call
		Project2.SemCommand.wait(nurseCall);
		
		// nurse calls patient
		System.out.println("Patient " + patientNumber + " enters doctor " + this.getDoctorNumber() + "'s office");
		Project2.SemCommand.signal(patientCalled);

		// patient waits for doctor's advising
		Project2.SemCommand.wait(doctorAdvises);
		System.out.println("Patient " + patientNumber + " receives advice from doctor " + this.getDoctorNumber());

		// patient finishes and leaves
		System.out.println("Patient " + patientNumber + " leaves");
		Project2.SemCommand.signal(patientFinished[patientNumber]);

	}
}
