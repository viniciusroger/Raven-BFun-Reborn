package keystrokesmod.util;

public class MouseUtil {

	public static boolean isOver(double mouseX, double mouseY, double x, double y, double width, double height) {
		return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
	}
}
