package keystrokesmod.util;

public class TimerUtil {
	private long lastMs;

	public TimerUtil() {
		lastMs = System.currentTimeMillis();
	}

	public boolean hasTimePassed(long ms, boolean autoReset) {
		if (System.currentTimeMillis() - lastMs > ms) {
			if (autoReset)
				reset();

			return true;
		}

		return false;
	}

	public boolean hasTimePassed(long ms) {
		return System.currentTimeMillis() > ms;
	}

	public void reset() {
		lastMs = System.currentTimeMillis();
	}
}
