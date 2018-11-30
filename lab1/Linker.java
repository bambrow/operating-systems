import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Linker {
	
	private static final int BASE_ADDRESS = 0; // base address of first module is 0
	private static final int MACHINE_SIZE = 200; // size of machine is 200
	
	private static String path; // file path
	private static BufferedReader reader; // buffer reader to parse file
	private static int numModules; // number of modules in total
	private static int currentNumModule; // current number of module
	private static int currentAddress; // current address
	
	private static Map<Integer, Integer> baseAddressTable; // table of base addresses
	private static Map<Integer, Integer> moduleSizeTable; // table of module sizes
	
	private static LinkedHashMap<String, Integer> symbolTable; // symbol table
	private static Map<String, Integer> symbolDefinedInModule; // table of symbols and their defined modules
	private static Set<String> symbolDuplicateTable; // table of duplicate defined symbols
	private static LinkedHashMap<String, Integer> symbolExceedSizeTable; // table of symbols that exceed the module size 
	
	private static LinkedHashSet<String> currentUseTable; // use table of current module that keeps updated to delete actual used ones
	private static Set<String> symbolUsedTable; // table of symbols in use list
	private static Map<Integer, List<String>> symbolUsedInModule; // table of modules and their used symbols in use list
	private static Map<Integer, LinkedHashSet<String>> symbolNotActuallyUsed; // table of modules and their symbols that are not actually used
	
	private static LinkedList<String> buffer; // buffer of unused text (tokens)
	
	/**
	 * Read the path name from the command line input.
	 * @param args
	 * @throws IllegalArgumentException
	 */
	private static void readPath(String[] args) throws IllegalArgumentException {
		if (args.length < 1) {
			throw new IllegalArgumentException("Error! File name is required as command line input!");
		}
		path = args[0];
	}
	
	/**
	 * Open the file using BufferedReader if it exists.
	 * @throws FileNotFoundException
	 */
	private static void openFile() throws FileNotFoundException {
		File file = new File(path);
		if (!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException("Error! File does not exist!");
		}
		reader = new BufferedReader(new FileReader(path));
	}
	
	/**
	 * Get and return the next token from file, and update the buffer if needed.
	 * @return next token as String
	 * @throws IOException
	 */
	private static String getNextToken() throws IOException {
		if (!buffer.isEmpty()) {
			return buffer.removeFirst();
		}
		String line = reader.readLine();
		if (line == null) {
			throw new IOException("Error! The file is not a valid input file!");
		}
		while (isEmptyLine(line) || buffer.isEmpty()) {
			if (!isEmptyLine(line)) {
				for (String token : line.split("\\s+")) {
					if (!isEmptyLine(token)) {
						buffer.addLast(token);
					}
				}
			} else {
				line = reader.readLine();
			}
		}
		return buffer.removeFirst();
	}
	
	/**
	 * Jump over the next k tokens in the file.
	 * @param k
	 * @throws IOException
	 */
	private static void jumpTokens(int k) throws IOException {
		for (int i = 0; i < k; i++) {
			getNextToken();
		}
	}
	
	/**
	 * Determine and return if the line is empty (containing only spaces).
	 * @param line
	 * @return true if the line is empty
	 */
	private static boolean isEmptyLine(String line){
		return line.trim().length() == 0;
	}
	
	/**
	 * Read the module number (first token) in the file.
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void readModuleNum() throws NumberFormatException, IOException {
		numModules = Integer.parseInt(getNextToken());
		if (numModules > 0) {
			baseAddressTable.put(currentNumModule, currentAddress);
		}
	}
	
	/**
	 * Run the first pass to parse the file.
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void firstPass() throws NumberFormatException, IOException {
		while (currentNumModule < numModules) {
			readDefinitionList();
			int useListLength = Integer.parseInt(getNextToken());
			jumpTokens(useListLength);
			int programTextLength = Integer.parseInt(getNextToken());
			jumpTokens(programTextLength * 2);
			moduleSizeTable.put(currentNumModule, programTextLength);
			currentAddress += programTextLength;
			if (currentNumModule < numModules - 1) {
				baseAddressTable.put(currentNumModule + 1, currentAddress);
			}
			currentNumModule++;
		}
		printSymbolTable();
	}
	
	/**
	 * Print the symbol table after the first pass.
	 */
	private static void printSymbolTable() {
		System.out.println("Symbol Table");
		for (Entry<String, Integer> symbolEntry : symbolTable.entrySet()) {
			String symbol = symbolEntry.getKey();
			int value = symbolEntry.getValue();
			int module = symbolDefinedInModule.get(symbol);
			if (value >= baseAddressTable.get(module) + moduleSizeTable.get(module)) {
				symbolExceedSizeTable.put(symbol, module);
				symbolTable.put(symbol, baseAddressTable.get(module));
				value = baseAddressTable.get(module);
			}
			System.out.print(symbol + "=" + value);
			if (symbolDuplicateTable.contains(symbol)) {
				System.out.print(" Error: This variable is multiply defined; first value used.");
			}
			System.out.println();
		}
	}
	
	/**
	 * Run the second pass to parse the file.
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void secondPass() throws NumberFormatException, IOException {
		jumpTokens(1); // jump over the number of modules
		System.out.println("Memory Map");
		while (currentNumModule < numModules) {
			currentAddress = baseAddressTable.get(currentNumModule);
			int defListLength = Integer.parseInt(getNextToken());
			jumpTokens(defListLength * 2);
			readUseList();
			readProgramTextList();
			detectUnusedSymbols();
			currentNumModule++;
		}
	}
	
	/**
	 * Read the definition list in a module.
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void readDefinitionList() throws NumberFormatException, IOException {
		int defListLength = Integer.parseInt(getNextToken());
		for (int i = 0; i < defListLength; i++) {
			readDefinition();
		}
	}
	
	/**
	 * Read one definition in a definition list.
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void readDefinition() throws NumberFormatException, IOException {
		String symbolName = getNextToken();
		int symbolValue = Integer.parseInt(getNextToken());
		symbolValue += currentAddress;
		if (symbolTable.containsKey(symbolName)) {
			symbolDuplicateTable.add(symbolName);
		} else {
			symbolTable.put(symbolName, symbolValue);
			symbolDefinedInModule.put(symbolName, currentNumModule);
		}
	}
	
	/**
	 * Read the use list in a module.
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void readUseList() throws NumberFormatException, IOException {
		int useListLength = Integer.parseInt(getNextToken());
		currentUseTable = new LinkedHashSet<String>();
		symbolUsedInModule.put(currentNumModule, new ArrayList<String>(useListLength));
		for (int i = 0; i < useListLength; i++) {
			readUse(i);
		}
	}
	
	/**
	 * Read one use in a use list.
	 * @param useListIndex
	 * @throws IOException
	 */
	private static void readUse(int useListIndex) throws IOException {
		String symbolName = getNextToken();
		currentUseTable.add(symbolName);
		symbolUsedTable.add(symbolName);
		symbolUsedInModule.get(currentNumModule).add(symbolName);
	}
	
	/**
	 * Read the program text list in a module.
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void readProgramTextList() throws NumberFormatException, IOException {
		int programTextLength = Integer.parseInt(getNextToken());
		for (int i = 0; i < programTextLength; i++) {
			readProgramText(programTextLength, i + baseAddressTable.get(currentNumModule));
		}
	}
	
	/**
	 * Read one program text in a program text list and print any error detected.
	 * @param programTextLength
	 * @param absoluteAddress
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void readProgramText(int programTextLength, int absoluteAddress) throws NumberFormatException, IOException {
		String operand = getNextToken();
		int digits = Integer.parseInt(getNextToken());
		int opcode = digits / 1000;
		int address = digits % 1000;
		System.out.printf("%-4s", absoluteAddress + ":");
		if (operand.equals("I")) {
			System.out.println(digits);
		} else if (operand.equals("A")) {
			if (address >= MACHINE_SIZE) {
				System.out.print(opcode + "000");
				System.out.println(" Error: Absolute address exceeds machine size; zero used.");
			} else {
				System.out.println(digits);
			}
		} else if (operand.equals("R")) {
			if (address >= programTextLength) {
				System.out.print(opcode + "000");
				System.out.println(" Error: Relative address exceeds module size; zero used.");
			} else {
				int value = address + baseAddressTable.get(currentNumModule);
				String newDigit = opcode + String.format("%03d", value);
				System.out.println(newDigit);
			}
		} else if (operand.equals("E")) {
			if (address >= symbolUsedInModule.get(currentNumModule).size()) {
				System.out.print(digits);
				System.out.println(" Error: External address exceeds length of use list; treated as immediate.");
			} else {
				String symbol = symbolUsedInModule.get(currentNumModule).get(address);
				if (!symbolTable.containsKey(symbol)) {
					System.out.print(opcode + "000");
					System.out.println(" Error: " + symbol + " is not defined; zero used.");
				} else {
					int value = symbolTable.get(symbol);
					String newDigit = opcode + String.format("%03d", value);
					System.out.println(newDigit);
				}
				currentUseTable.remove(symbol);
			}
		} else {
			throw new IOException("Error! The file is not a valid input file!");
		}
	}
	
	/**
	 * Handle unused symbols after parsing each module.
	 */
	private static void detectUnusedSymbols() {
		symbolNotActuallyUsed.put(currentNumModule, new LinkedHashSet<String>());
		for (String symbol : currentUseTable) {
			symbolNotActuallyUsed.get(currentNumModule).add(symbol);
		}
	}
	
	/**
	 * Print warning messages of symbols that appeared in the use list but were not actually used.
	 */
	private static void printWarningNotActuallyUsed() {
		boolean blankLinePrinted = false;
		for (int module = 0; module < numModules; module++) {
			LinkedHashSet<String> symbols = symbolNotActuallyUsed.get(module);
			for (String symbol : symbols) {
				if (!blankLinePrinted) {
					System.out.println();
					blankLinePrinted = true;
				}
				System.out.println("Warning: In module " + module + " " + symbol + " appeared in the use list but was not actually used.");
			}
			
		}
	}
	
	/**
	 * Print warning messages of symbols that were defined but never used.
	 */
	private static void printWarningNeverUsed() {
		boolean blankLinePrinted = false;
		for (Entry<String, Integer> symbolEntry : symbolTable.entrySet()) {
			String symbol = symbolEntry.getKey();
			int module = symbolDefinedInModule.get(symbol);
			if (!symbolUsedTable.contains(symbol)) {
				if (!blankLinePrinted) {
					System.out.println();
					blankLinePrinted = true;
				}
				System.out.println("Warning: " + symbol + " was defined in module " + module + " but never used.");
			}
		}
	}
	
	/**
	 * Print error messages of defined symbols that exceeds the size of the module.
	 */
	private static void printErrorDefinitionExceedsModuleSize() {
		if (!symbolExceedSizeTable.isEmpty()) {
			System.out.println();
		}
		for (Entry<String, Integer> symbolEntry : symbolExceedSizeTable.entrySet()) {
			String symbol = symbolEntry.getKey();
			int module = symbolEntry.getValue();
			System.out.println("Error: In module " + module + " the def of " + symbol + " exceeds the module size; zero (relative) used.");
		}
	}
	
	/**
	 * Print error messages of symbols that were not defined but in the use list.
	 */
	private static void printErrorNoDefinitionButInUseList() {
		boolean blankLinePrinted = false;
		for (int module = 0; module < numModules; module++) {
			List<String> symbolsUseList = symbolUsedInModule.get(module);
			LinkedHashSet<String> symbolsNotUsed = symbolNotActuallyUsed.get(module);
			for (String symbol : symbolsUseList) {
				if (!symbolTable.containsKey(symbol) && symbolsNotUsed.contains(symbol)) {
					if (!blankLinePrinted) {
						System.out.println();
						blankLinePrinted = true;
					}
					System.out.println("Error: In module " + module + " " + symbol + " is in the use list but was not defined.");
				}
			}
		}
	}
	
	/**
	 * Initialize and start everything before the first pass.
	 */
	private static void start() {
		path = null;
		reader = null;
		numModules = 0;
		currentNumModule = 0;
		currentAddress = BASE_ADDRESS;
		baseAddressTable = new HashMap<Integer, Integer>();
		moduleSizeTable = new HashMap<Integer, Integer>();
		symbolTable = new LinkedHashMap<String, Integer>();
		symbolDefinedInModule = new HashMap<String, Integer>();
		symbolDuplicateTable = new HashSet<String>();
		symbolExceedSizeTable = new LinkedHashMap<String, Integer>();
		symbolUsedTable = new HashSet<String>();
		symbolUsedInModule = new LinkedHashMap<Integer, List<String>>();
		symbolNotActuallyUsed = new LinkedHashMap<Integer, LinkedHashSet<String>>();
		buffer = new LinkedList<String>();
	}
	
	/**
	 * Some bit of clean-up between the transition from first pass to second pass.
	 * @throws IOException
	 */
	private static void moveToSecondPass() throws IOException {
		reader.close();
		reader = new BufferedReader(new FileReader(path));
		currentNumModule = 0;
		currentAddress = BASE_ADDRESS;
		buffer = new LinkedList<String>();
		System.out.println();
	}
	
	/**
	 * Clean-up everything after the second pass.
	 * @throws IOException
	 */
	private static void end() throws IOException {
		if (currentUseTable != null) {
			currentUseTable.clear();
		}
		reader.close();
	}
	
	/**
	 * Master method that runs everything.
	 * @param args
	 * @throws FileNotFoundException
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static void run(String[] args) throws FileNotFoundException, NumberFormatException, IOException {
		start();
		readPath(args);
		openFile();
		readModuleNum();
		firstPass();
		moveToSecondPass();
		secondPass();
		printWarningNotActuallyUsed();
		printWarningNeverUsed();
		printErrorDefinitionExceedsModuleSize();
		printErrorNoDefinitionButInUseList();
		end();
	}
	
	public static void main(String[] args) throws FileNotFoundException, NumberFormatException, IOException {
		Linker.run(args);
	}
	
}
