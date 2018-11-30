import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RandomNumberReader {
	
	private static BufferedReader reader = null;

	private RandomNumberReader() {}
	
	public static void initializeReader() {
		String path = "random-numbers";
		try {
			reader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
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
	
	public static void closeReader() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void resetReader() {
		closeReader();
		initializeReader();
	}
	
	public static int randomOS(int k) {
		int x = getNextRandomNumber();
		// System.out.println("Random Number: " + x);
		return 1 + (x % k);
	}
	
}
