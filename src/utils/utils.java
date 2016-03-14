package utils;

public class utils {
	public static void sleepFor(long ms)
	{
		long startTime = System.currentTimeMillis();
		long elapse;
		while (ms > 0)
		{
			try {
				Thread.sleep(ms);
				break;
			}
			catch (InterruptedException e)
			{
				elapse = System.currentTimeMillis() - startTime;
				ms -= elapse;
				startTime += elapse;
			}
		}
	}
	
	public static void waitForever(Object obj) {
		try {
			obj.wait();
		}
		catch (InterruptedException e) {
		}
	}
}
