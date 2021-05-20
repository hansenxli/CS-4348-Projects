// Hansen Li
// CS 4348 
// Project 1
// Notes: Some code learned from youtube tutorial and based off sample files Prof Ozbirn uploaded 

/*It will have these registers:  PC, SP, IR, AC, X, Y.
   It will support the instructions shown on the next page of this document.
   It will run the user program at address 0.
   Instructions are fetched into the IR from memory.  The operand can be fetched into a local variable.
   Each instruction should be executed before the next instruction is fetched.
   The user stack resides at the end of user memory and grows down toward address 0.
   The system stack resides at the end of system memory and grows down toward address 0.
   There is no hardware enforcement of stack size.
   The program ends when the End instruction is executed.  The 2 processes should end at that time.
   The user program cannot access system memory (exits with error message).
*/

import java.util.Scanner;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;


public class Project1 {
	
	// cpu class 
	public static class CPU {
		
		// registers:  PC, SP, IR, AC, X, Y
		int PC, SP, IR, AC, X, Y;
		
		Scanner cpuInput;
		PrintWriter cpuOutput;
		
		int timer;
		int timerLimit;
				
		boolean kernelMode;

		// cpu constructor
		public CPU (Scanner cpuInput, PrintWriter cpuOutput, int timerLimit) {
			
			this.cpuInput = cpuInput;
			this.cpuOutput = cpuOutput;
			this.timerLimit = timerLimit;
						
			// initialize registers
			PC = 0;
			SP = 1000;
			IR = 0;
			AC = 0;
			X = 0;
			Y = 0;
			timer = 0;
			
			kernelMode = false;

		}

		/*It will support two operations:
	       read(address) -  returns the value at the address
	       write(address, data) - writes the data to the address
		 */
		
		// uses codes for read, write, and end
		// 7 = read
		// 8 = write
		// 9 = end
		
		public int memRead(int memAddress)	{
			
			// checks to see kernel status and memory address location
			if (memAddress > 999 && !kernelMode) {
				
				System.err.println("Not in Kernel mode - cannot access system memory.");
				System.exit(1);
				
			}
			
			else {
				
				// sends command to Memory
				cpuOutput.println("7" + memAddress);			
				cpuOutput.flush();
			}
			
			return Integer.parseInt(cpuInput.nextLine());
		}
				
		public void memWrite(int memAddress, int memVal) {
			
			// stackoverflow solution referenced for regex
			cpuOutput.printf("8%d,%d\n", memAddress, memVal);			
			cpuOutput.flush();		
		}
		

		public void memEnd() {
			
			cpuOutput.println("9");
			cpuOutput.flush();		
		}
		
		public void nextInstruction() {
			// instruction register set from program counter
			IR = memRead(PC);
			PC++;
		}
				
		public void runProcess() {
			
			// flag for process running
			boolean isRunning = true;
			boolean result;
			
			while (isRunning) {
				
				nextInstruction();
				
				// execute instruction and increment timer
				result = executeInstruction();
				timer++;
				isRunning = result;			
				
				/*Timer
			     A timer will interrupt the processor after every X instructions, where X is a command-line parameter.
			     The timer is always counting, whether in user mode or kernel mode.
			     */
				
				// check to see if timer has breached interrupt timer limit
				if (timer >= timerLimit) {
					
					if (kernelMode == false) {  
						// reset timer
						timer = 0;
						
						// enter kernel mode
						kernelMode();
						
						// execution at address 1000
						PC = 1000;
						
					}
					
				}
			}
		}
			
		// returns stack pointer value and increments
		public int popStack() {
			return memRead(SP++);
		}
		
		// decrements stack pointer and pushes val 
		public void pushStack(int val)	{
			SP--;
			memWrite(SP, val);
		}
		
		// kernel mode
		public void kernelMode() {
			
			// create temporary storage for stack pointer when transitioning into kernel mode
			int temp = SP; 
			
			// stack pointer to top outside array
			SP = 2000;
			
			// push registers onto stack
			pushStack(temp);
			pushStack(PC);
			pushStack(IR);
			pushStack(AC);
			pushStack(X);
			pushStack(Y);
			
			// set kernel mode to true
			kernelMode = true;
		}
		
		


   /*Interrupt processing
     There are two forms of interrupts:  the timer and a system call using the int instruction.
     In both cases the CPU should enter kernel mode.
     The stack pointer should be switched to the system stack.
     The SP and PC registers (and only these registers) should be saved on the system stack by the CPU.
     The handler may save additional registers. 
     A timer interrupt should cause execution at address 1000.
     The int instruction should cause execution at address 1500.
     The iret instruction returns from an interrupt.
     Interrupts should be disabled during interrupt processing to avoid nested execution.
     To make it easy, do not allow interrupts during system calls or vice versa.
	*/
		
		private boolean executeInstruction() {

			// cases taken from project document
			// checks value of instruction register
			switch (IR) {
			
				// Load the value into the AC
				case 1:
					nextInstruction();
					AC = IR;
					break;
					
				// Load the value at the address into the AC
				case 2: 
					nextInstruction();
					AC = memRead(IR);
					break;
					
				// Load the value from the address found in the given address into the AC
				// (for example, if LoadInd 500, and 500 contains 100, then load from 100).	
				case 3: 
					nextInstruction();
					AC = memRead(memRead(IR));
					break;
				
				// Load the value at (address+X) into the AC
				// (for example, if LoadIdxX 500, and X contains 10, then load from 510).
				case 4: 
					nextInstruction();
					AC = memRead(IR + X);					
					break;
					
				// Load the value at (address+Y) into the AC
				case 5: 
					nextInstruction();
					AC = memRead(IR + Y);
					break;
					
				// Load from (Sp+X) into the AC (if SP is 990, and X is 1, load from 991).
				case 6: 
					AC = memRead(SP + X);
					break;
					
				// Store the value in the AC into the address
				case 7: 
					nextInstruction();
					memWrite(IR, AC);
					break;
					
				// Gets a random int from 1 to 100 into the AC
				case 8:
					Random rand = new Random();
					int ranVal = rand.nextInt(101);
					AC = ranVal;
					break;
				
				// If port=1, writes AC as an int to the screen
				// If port=2, writes AC as a char to the screen
				case 9: 
					nextInstruction();
					if (IR == 1) {
						System.out.print(AC);
					}
						
					else if (IR == 2) {
						System.out.print((char)AC);
					}
						
					break;
				
				// Add the value in X to the AC
				case 10:
					AC += X; 
					break;
					
				// Add the value in Y to the AC
				case 11: 
					AC += Y; 
					break;
					
				// Subtract the value in X from the AC
				case 12: 
					AC -= X; 
					break;
					
				// Subtract the value in Y from the AC
				case 13: 
					AC -= Y; 
					break;
					
				// Copy the value in the AC to X
				case 14: 
					X = AC; 
					break;
					
				// Copy the value in X to the AC
				case 15:
					AC = X; 
					break;
					
				// Copy the value in the AC to Y
				case 16:
					Y = AC; 
					break;
					
				// Copy the value in Y to the AC
				case 17:
					AC = Y; 
					break;
					
				// Copy the value in AC to the SP
				case 18: 
					SP = AC; 
					break;
					
				// Copy the value in SP to the AC 
				case 19: 
					AC = SP; 
					break;
					
				// Jump to the address
				case 20: 
					nextInstruction();
					PC = IR;
					break;
					
				// Jump to the address only if the value in the AC is zero
				case 21: 
					nextInstruction();
					if (AC == 0)
						PC = IR;
					break;
					
				// Jump to the address only if the value in the AC is not zero
				case 22: 
					nextInstruction();
					if (AC != 0)
						PC = IR;
					break;
					
				// Push return address onto stack, jump to the address
				case 23: 
					nextInstruction();
					pushStack(PC);
					PC = IR;
					break;
				
				// Pop return address from the stack, jump to the address
				case 24:
					PC = popStack();
					break;
				
				// Increment the value in X
				case 25:
					X++; 
					break;
				
				// Decrement the value in X
				case 26: 
					X--; 
					break;
				
				// Push AC onto stack
				case 27: 
					pushStack(AC);
					break;
				
				// Pop from stack into AC
				case 28: 
					AC = popStack();
					break;
				
				// Perform system call
				case 29: 
					kernelMode();
					PC = 1500;					
					break;
					
				// Return from system call
				case 30: 
					Y = popStack();
					X = popStack();
					AC = popStack();
					IR = popStack();
					PC = popStack();
					SP = popStack();
					
					kernelMode = false;
					
					break;
				
				// End execution
				case 50: 
					memEnd();
					return false;
				
				// no valid instruction from set
				default: 
					System.err.println("Value not from instruction set");
					memEnd();
					return false;
			}
			
			return true;
			
		}
		
	}
		
	public static void main(String[] args) {
		
		if (args.length <= 1) {					
			System.err.println("Please include input file and interrupt timer limit.");
			System.exit(1);
			
		}
		
		// arguments read and stored
		String inFile = args[0];		
		int timerLimit = Integer.parseInt(args[1]);

		// Note: tutorials referenced learning how to get runtime to work
		Runtime rt = Runtime.getRuntime();
				
		// Note: this section coded with guidance from CS mentor + tutorial notes		
		try {
			
			// creates new memory process with runtime exec 
			Process memProcess = rt.exec("java Memory " + inFile);
			
			Scanner memInput = new Scanner(memProcess.getInputStream());
			
			PrintWriter memOutput = new PrintWriter(memProcess.getOutputStream());
			
			// calls cpu constructor and creates instance
			CPU cpuInst = new CPU(memInput, memOutput, timerLimit);
			
			cpuInst.runProcess();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			System.err.println("Error occurred creating new process.");
			System.exit(1);
			
		}

	}

}