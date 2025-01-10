package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.ReflectUtil;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.lenni0451.asmevents.event.enums.EnumEventPriority;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Module {
    public SliderSetting mode;
    private SliderSetting minFallDistance;
    private ButtonSetting disableAdventure;
    private ButtonSetting ignoreVoid;
    private String[] modes = new String[]{"Spoof", "Extra", "NoGround"};

    public NoFall() {
        super("NoFall", Category.player);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(minFallDistance = new SliderSetting("Minimum fall distance", 3, 0, 10, 0.1));
        this.registerSetting(disableAdventure = new ButtonSetting("Disable adventure", false));
        this.registerSetting(ignoreVoid = new ButtonSetting("Ignore void", true));
    }

    public void onDisable() {
        GeneralUtils.resetTimer();
    }

    @EventTarget(priority = EnumEventPriority.LOWEST)
    public void onPreMotion(PreMotionEvent e) {
        GeneralUtils.resetTimer();
        if (disableAdventure.isToggled() && mc.playerController.getCurrentGameType().isAdventure()) {
            return;
        }
        if (ignoreVoid.isToggled() && isVoid()) {
            return;
        }
        if (((double) mc.thePlayer.fallDistance > minFallDistance.getInput() || minFallDistance.getInput() == 0) || mode.getInput() == 2) {
            switch ((int) mode.getInput()) {
                case 0:
                    e.setOnGround(true);
                    break;
                case 1:
                    float fallDistance = 0;
                    try {
                        fallDistance = ReflectUtil.fallDistance.getFloat(mc.thePlayer);
                    }
                    catch (Exception exception) {
                        GeneralUtils.sendMessage("&cFailed to get fall distance.");
                    }
                    if (fallDistance > minFallDistance.getInput()) {
                        GeneralUtils.getTimer().timerSpeed = (float) 0.5;
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
                        try {
                            ReflectUtil.fallDistance.setFloat(mc.thePlayer, 0);
                        }
                        catch (Exception exception) {
                            GeneralUtils.sendMessage("&cFailed to set fall distance to 0.");
                        }
                    }
                    break;
                case 2:
                    e.setOnGround(false);
                    break;
            }
        }
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }

    private boolean isVoid() {
        return GeneralUtils.overVoid(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }
}
