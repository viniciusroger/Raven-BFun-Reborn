package keystrokesmod.module.impl.movement;

import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.utility.Utils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.client.settings.KeyBinding;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", Module.category.movement, 0);
    }

    @EventTarget
    public void p(PreUpdateEvent e) {
        if (Utils.nullCheck() && mc.inGameHasFocus) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }
}
