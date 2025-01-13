package keystrokesmod.module.impl.ghost;

import keystrokesmod.event.*;
import keystrokesmod.module.Module;
import keystrokesmod.manager.ModuleManager;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.ReflectUtil;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

public class FastPlace extends Module {
    public SliderSetting mode, fastPlaceEvent, tickDelay;
    public ButtonSetting blocksOnly, pitchCheck, allowUseItem;

    public FastPlace() {
        super("FastPlace", Category.ghost, 0);
        this.registerSetting(mode = new SliderSetting("Mode", new String[]{"Normal", "No CPS Cap"}, 0));
        this.registerSetting(fastPlaceEvent = new SliderSetting("FastPlace Event", new String[]{"Tick", "Render2D", "Render3D"}, 0));
        this.registerSetting(tickDelay = new SliderSetting("Tick delay", 1.0, 0.0, 3.0, 1.0));
        this.registerSetting(blocksOnly = new ButtonSetting("Blocks only", true));
        this.registerSetting(pitchCheck = new ButtonSetting("Pitch check", false));
        this.registerSetting(allowUseItem = new ButtonSetting("Allow Use Item", false));
    }

    @EventTarget
    public void onTick(TickEvent e) {
        if (fastPlaceEvent.getInput() == 0)
            onEvent();
    }

    @EventTarget
    public void onRender2D(Render2DEvent e) {
        if (fastPlaceEvent.getInput() == 1)
            onEvent();
    }

    @EventTarget
    public void onRender3D(Render3DEvent e) {
        if (fastPlaceEvent.getInput() == 2)
            onEvent();
    }

    private void onEvent() {
        if (ModuleManager.scaffold.stopFastPlace()) {
            return;
        }

        if (GeneralUtils.nullCheck() && mc.inGameHasFocus && ReflectUtil.rightClickDelayTimerField != null) {
            if (allowUseItem.isToggled() && mc.thePlayer.isUsingItem())
                return;

            if (blocksOnly.isToggled()) {
                ItemStack item = mc.thePlayer.getHeldItem();
                if (item == null || !(item.getItem() instanceof ItemBlock)) {
                    return;
                }
            }

			try {
                int delayCrick = ReflectUtil.rightClickDelayTimerField.getInt(mc);

                if (tickDelay.getInput() <= 1)
				    ReflectUtil.rightClickDelayTimerField.set(mc, (int) tickDelay.getInput());
                else {
                    if (delayCrick == 4)
                        ReflectUtil.rightClickDelayTimerField.set(mc, (int) tickDelay.getInput());
                }
			} catch (Exception ignored) {}

			if (mode.getInput() == 1) {
				if (Mouse.isButtonDown(1))
					ReflectUtil.rightClick();
			}
        }
    }
}
