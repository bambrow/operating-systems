import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class encapsulates a singleton object of random number reader.
 *
 */
public class RandomNumberReader {
	
	private static BufferedReader reader = null; // reader

	private RandomNumberReader() {} // private constructor
	
	/**
	 * Initialize the reader and open the random number file.
	 */
	public static void initializeReader() {
		String path = "random-numbers";
		try {
			reader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the next random number.
	 * @return the next random integer
	 * @throws NumberFormatException
	 */
	private static int getNextRandomNumber() throws NumberFormatException {
		String line = null;
		try {
			line = reader.readLine();
			if (line == null) {
				resetReader();
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Integer.parseInt(line.trim());
	}
	
	/**
	 * Close the reader.
	 */
	public static void closeReader() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reset the reader and read from the beginning of the file.
	 */
	public static void resetReader() {
		closeReader();
		initializeReader();
	}
	
	/**
	 * Get the next random word according to A, B and C probabilities.
	 * @param A
	 * @param B
	 * @param C
	 * @return an Address enum showing the next location
	 */
	public static Address randomNextWord(double A, double B, double C) {
		int r = getNextRandomNumber();
		// System.out.println("Random Number: " + r);
		double y = r / (Integer.MAX_VALUE + 1d);
		if (y < A) {
			return Address.HIGHER_ONE;
		} else if (y < A + B) {
			return Address.LOWER_NEARBY;
		} else if (y < A + B + C) {
			return Address.HIGHER_NEARBY;
		} else {
			return Address.RANDOM;
		}
	}
	
	/**
	 * Calculate the random integer according to k.
	 * @param k
	 * @return a random number from 0 to k-1
	 */
	public static int randomInteger(int k) {
		int x = getNextRandomNumber();
		// System.out.println("Random Number: " + x);
		return x % k;
	}
	
}
