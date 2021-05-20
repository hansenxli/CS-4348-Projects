// Hansen Li
// CS 4348
// Project 1
// Notes: Some code learned from youtube tutorial and based off sample files Prof Ozbirn uploaded 

/*It will consist of 2000 integer entries, 0-999 for the user program, 1000-1999 for system code.
   It will support two operations:
       read(address) -  returns the value at the address
       write(address, data) - writes the data to the address
   Memory will read an input file containing a program into its array, before any CPU fetching begins.
   Note that the memory is simply storage; it has no real logic beyond reading and writing.
*/

import java.util.Scanner;
import java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;

public class Memory {

	
	/*Memory
   It will consist of 2000 integer entries, 0-999 for the user program, 1000-1999 for system code.
   It will support two operations:
       read(address) -  returns the value at the address
       write(address, data) - writes the data to the address
   Memory will read an input file containing a program into its array, before any CPU fetching begins.
   Note that the memory is simply storage; it has no real logic beyond reading and writing.
	*/
	
	// array representing memory registers
	static int[] memArray;	
	
	private static void initMem (String inFile) throws FileNotFoundException {
		
		// 2000 int entries for memory array
		// 0-999 used for user program
		// 1000-1999 used for system code
		memArray = new int[2000];
		
		int indexMem = 0;
		String[] temp;
		
		// input file to scanner
		Scanner sc = new Scanner(new File(inFile));
		
		// iterate through file line by line
		while (sc.hasNextLine()) {
			
			String currLine = sc.nextLine().trim();
			
			// empty line read
			if (currLine.length() == 0) {
				continue;
			}
			
			// for .1000, .1500, .1700 instructions		
			else if (currLine.charAt(0) == '.') {
				
				// slice string with regex for white space
				// starts at string position after '.'
				temp = currLine.substring(1).split("\\s+");
				indexMem = Integer.parseInt(temp[0]);				
				continue;				
			}
			
			// checks to see if line begins with a non-int and skips if it is
			// checks after the '.' check
			else if (currLine.charAt(0) < '0' || currLine.charAt(0) > '9') {
				continue;
			}
			
			// splits line by white space into string array
			String[] currLineArray = currLine.split("\\s+");
			
			// checks if line empty
			// reads first int val into memory array
			if (currLineArray.length > 0) {
				
				memArray[indexMem] = Integer.parseInt(currLineArray[0]);
				indexMem++;
				
			}	
			
			// line was empty, skips
			else 
				continue;
		}
		
		sc.close();
	}
	
	/*It will support two operations:
	       read(address) -  returns the value at the address
	       write(address, data) - writes the data to the address
	*/
	
	private static int read(int address) {
		
		// returns value at address from memory array
		return memArray[address];
	}

	private static void write(int address, int data) {
		
		// sets index in memory array to value
		memArray[address] = data;
	}
	
	
	public static void main (String[] args) {
		
		// checks for argument length
		if (args.length < 1) {
			
			System.err.println("Arguments not sufficient; requires input file.");
			System.exit(1);
		}
		
		String inFilePath = args[0];
		
		try {
			
			initMem(inFilePath);
			
		} catch (FileNotFoundException e) {
			
			System.err.println("Error - Double check CPU component output.");
			System.exit(1);
			
		}
		
		Scanner cpuInput = new Scanner(System.in);
		
		// takes output from CPU component
		while (cpuInput.hasNextLine()) {
			
			String currLine = cpuInput.nextLine();
			int memAddress;
			int memVal;

			// value equivalent of command
			char comVal = currLine.charAt(0);
			
			
			// uses codes for different commands
			// 7 = read
			// 8 = write
			// 9 = terminate
			switch (comVal)	{
			
				case '7': 
					// stores address index from cpu input
					memAddress = Integer.parseInt(currLine.substring(1));					
					System.out.println(read(memAddress));
					break;
					
				case '8': 
					// 
					String[] params = currLine.substring(1).split(",");
					memAddress = Integer.parseInt(params[0]);
					memVal = Integer.parseInt(params[1]);
					write(memAddress, memVal);
					break;
					
				case '9': 
					
					System.exit(0);
			}
			
		}
		
		cpuInput.close();
	}

}