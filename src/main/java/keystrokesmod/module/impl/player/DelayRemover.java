package keystrokesmod.module.impl.player;

import keystrokesmod.event.TickEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.util.ReflectUtil;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;

public class DelayRemover extends Module {
    public static ButtonSetting oldReg, removeJumpTicks;

    public DelayRemover() {
        super("Delay Remover", Category.player, 0);
        this.registerSetting(oldReg = new ButtonSetting("1.7 hitreg", true));
        this.registerSetting(removeJumpTicks = new ButtonSetting("Remove jump ticks", false));
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (!mc.inGameHasFocus || !GeneralUtils.nullCheck()) {
            return;
        }
        if (oldReg.isToggled()) {
            try {
                ReflectUtil.leftClickCounter.set(mc, 0);
            } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
            }
		}
        if (removeJumpTicks.isToggled()) {
            try {
                ReflectUtil.jumpTicks.set(mc.thePlayer, 0);
            } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
            }
		}
    }
}
