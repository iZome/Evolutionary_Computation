import java.util.Random;

public class ShuffleUtils {
	
	private static int seed = 0;
	
	public static int[] ShuffleArray(int [] arr) {
		seed += 1;
		Random random = new Random(seed);
		int [] shuffledArray = new int[arr.length];
		
		for (int i = 0; i<arr.length; ++i) {
			shuffledArray[i] = arr[i];
		}
		
		for (int i = 0; i<arr.length; ++i) {
			int swapIndex = random.nextInt(arr.length);
			int temp = arr[swapIndex];
			arr[swapIndex] = arr[i];
			arr[i] = temp;
		}
		
		return arr;
	}

}
