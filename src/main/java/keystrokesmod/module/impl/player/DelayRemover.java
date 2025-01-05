package keystrokesmod.module.impl.player;

import keystrokesmod.event.TickEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.ReflectHelper;
import keystrokesmod.utility.Utils;
import net.lenni0451.asmevents.event.EventTarget;

public class DelayRemover extends Module { // from b4 src
    public static ButtonSetting oldReg, removeJumpTicks;

    public DelayRemover() {
        super("Delay Remover", category.player, 0);
        this.registerSetting(oldReg = new ButtonSetting("1.7 hitreg", true));
        this.registerSetting(removeJumpTicks = new ButtonSetting("Remove jump ticks", false));
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (!mc.inGameHasFocus || !Utils.nullCheck()) {
            return;
        }
        if (oldReg.isToggled()) {
            try {
                ReflectHelper.leftClickCounter.set(mc, 0);
            } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
            }
		}
        if (removeJumpTicks.isToggled()) {
            try {
                ReflectHelper.jumpTicks.set(mc.thePlayer, 0);
            } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
            }
		}
    }
}