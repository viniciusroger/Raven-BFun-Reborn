package keystrokesmod.event;

import net.lenni0451.asmevents.event.wrapper.CancellableEvent;
import org.lwjgl.input.Mouse;

public class MouseEvent extends CancellableEvent {
	private final int x = Mouse.getEventX();
	private final int y = Mouse.getEventY();
	private final int dx = Mouse.getEventDX();
	private final int dy = Mouse.getEventDY();
	private final int dwheel = Mouse.getEventDWheel();
	private final int button = Mouse.getEventButton();
	private final boolean buttonstate = Mouse.getEventButtonState();
	private final long nanoseconds = Mouse.getEventNanoseconds();

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getDx() {
		return dx;
	}

	public int getDy() {
		return dy;
	}

	public int getDWheel() {
		return dwheel;
	}

	public int getButton() {
		return button;
	}

	public long getNanoseconds() {
		return nanoseconds;
	}

	public boolean isButtonState() {
		return buttonstate;
	}
}
