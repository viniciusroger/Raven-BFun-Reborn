package keystrokesmod.module.impl.player;

import keystrokesmod.event.PostUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.ReflectHelper;
import keystrokesmod.utility.Utils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class FastPlace extends Module {
    public SliderSetting tickDelay;
    public ButtonSetting blocksOnly, pitchCheck;

    public FastPlace() {
        super("FastPlace", Module.category.player, 0);
        this.registerSetting(tickDelay = new SliderSetting("Tick delay", 1.0, 1.0, 3.0, 1.0));
        this.registerSetting(blocksOnly = new ButtonSetting("Blocks only", true));
        this.registerSetting(pitchCheck = new ButtonSetting("Pitch check", false));
    }

    @EventTarget
    public void a(PostUpdateEvent e) {
        if (ModuleManager.scaffold.stopFastPlace()) {
            return;
        }
        if (Utils.nullCheck() && mc.inGameHasFocus && ReflectHelper.rightClickDelayTimerField != null) {
            if (blocksOnly.isToggled()) {
                ItemStack item = mc.thePlayer.getHeldItem();
                if (item == null || !(item.getItem() instanceof ItemBlock)) {
                    return;
                }
            }

            try {
                int c = (int) tickDelay.getInput();
                if (c == 0) {
                    ReflectHelper.rightClickDelayTimerField.set(mc, 0);
                } else {
                    if (c == 4) {
                        return;
                    }

                    int d = ReflectHelper.rightClickDelayTimerField.getInt(mc);
                    if (d == 4) {
                        ReflectHelper.rightClickDelayTimerField.set(mc, c);
                    }
                }
            } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
            }
        }
    }
}
