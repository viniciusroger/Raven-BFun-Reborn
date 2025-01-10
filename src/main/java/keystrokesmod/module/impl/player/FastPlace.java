package keystrokesmod.module.impl.player;

import keystrokesmod.event.PostUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.manager.ModuleManager;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.ReflectUtil;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class FastPlace extends Module {
    public SliderSetting tickDelay;
    public ButtonSetting blocksOnly, pitchCheck;

    public FastPlace() {
        super("FastPlace", Category.player, 0);
        this.registerSetting(tickDelay = new SliderSetting("Tick delay", 1.0, 1.0, 3.0, 1.0));
        this.registerSetting(blocksOnly = new ButtonSetting("Blocks only", true));
        this.registerSetting(pitchCheck = new ButtonSetting("Pitch check", false));
    }

    @EventTarget
    public void a(PostUpdateEvent e) {
        if (ModuleManager.scaffold.stopFastPlace()) {
            return;
        }
        if (GeneralUtils.nullCheck() && mc.inGameHasFocus && ReflectUtil.rightClickDelayTimerField != null) {
            if (blocksOnly.isToggled()) {
                ItemStack item = mc.thePlayer.getHeldItem();
                if (item == null || !(item.getItem() instanceof ItemBlock)) {
                    return;
                }
            }

            try {
                int c = (int) tickDelay.getInput();
                if (c == 0) {
                    ReflectUtil.rightClickDelayTimerField.set(mc, 0);
                } else {
                    if (c == 4) {
                        return;
                    }

                    int d = ReflectUtil.rightClickDelayTimerField.getInt(mc);
                    if (d == 4) {
                        ReflectUtil.rightClickDelayTimerField.set(mc, c);
                    }
                }
            } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
            }
        }
    }
}
