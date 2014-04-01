package engine;

public class Timer {
	public static long currentTime = System.nanoTime();
	public static long deltaTime;
	public static int deltaTimeMS;
	public static long lastUpdateTime;
	
	public static void update()
	{
		lastUpdateTime = currentTime;
		currentTime = System.nanoTime();
		deltaTime = currentTime - lastUpdateTime;
		deltaTimeMS = (int)(deltaTime / 1000000);
	}
	
	public static long currentFrameDuration()
	{
		return System.nanoTime() - currentTime;
	}

	public static int currentFrameDurationMS()
	{
		return (int)(System.nanoTime() - currentTime)/1000000;
	}
}
