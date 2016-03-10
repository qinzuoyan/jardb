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
}
