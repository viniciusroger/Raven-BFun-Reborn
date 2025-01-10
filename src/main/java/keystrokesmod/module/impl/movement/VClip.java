package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;

public class VClip extends Module {
    private SliderSetting distance;
    private ButtonSetting sendMessage;

    public VClip() {
        super("VClip", Category.movement, 0);
        this.registerSetting(distance = new SliderSetting("Distance", 3.0, -20.0, 20.0, 0.5));
        this.registerSetting(sendMessage = new ButtonSetting("Send message", true));
    }

    public void onEnable() {
        final double distance = this.distance.getInput();
        if (this.distance.getInput() != 0.0D) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + distance, mc.thePlayer.posZ);
            if (sendMessage.isToggled()) {
                GeneralUtils.sendMessage("&7Teleported you " + ((distance > 0.0) ? "upwards" : "downwards") + " by &b" + distance + " &7blocks.");
            }
        }

        this.disable();
    }
}
