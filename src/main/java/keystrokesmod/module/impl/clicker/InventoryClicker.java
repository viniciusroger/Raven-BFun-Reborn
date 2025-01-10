package keystrokesmod.module.impl.clicker;

import keystrokesmod.event.Render2DEvent;
import keystrokesmod.event.Render3DEvent;
import keystrokesmod.event.TickEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;
import keystrokesmod.util.ReflectUtil;
import keystrokesmod.util.TimerUtil;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import org.lwjgl.input.Mouse;

public class InventoryClicker extends Module {
	private SliderSetting cps, clickType, clickEvent;
	private TimerUtil timer = new TimerUtil();

	public InventoryClicker() {
		super("InventoryClicker", Category.clicker, 0);

		this.registerSetting(cps = new SliderSetting("CPS", 15, 1, 30, 1));
		this.registerSetting(clickType = new SliderSetting("Click Type", new String[]{"Left", "Right"}, 0));
		this.registerSetting(clickEvent = new SliderSetting("Click Event", new String[]{"Render3D", "Rende2D", "Tick"}, 0));
	}

	@EventTarget
	public void onRender3D(Render3DEvent event) {
		if (clickEvent.getInput() == 0)
			onEvent();
	}

	@EventTarget
	public void onRender2D(Render2DEvent event) {
		if (clickEvent.getInput() == 1)
			onEvent();
	}

	@EventTarget
	public void onTick(TickEvent event) {
		if (clickEvent.getInput() == 2)
			onEvent();
	}

	private void onEvent() {
		if (GeneralUtils.nullCheck() && (mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChest)) {
			if (timer.hasTimePassed(1000 / (int) cps.getInput(), true)) {
				if (Mouse.isButtonDown((int) clickType.getInput()))
					ReflectUtil.clickMouseGui(mc.currentScreen, (int) clickType.getInput());
			}
		}
	}
}
