package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;
import org.lwjgl.input.Keyboard;

public class BHop extends Module {
    private SliderSetting mode;
    public static SliderSetting speed;
    private ButtonSetting autoJump;
    private ButtonSetting liquidDisable;
    private ButtonSetting sneakDisable;
    private ButtonSetting stopMotion;
    private String[] modes = new String[]{"Strafe", "Ground"};
    public boolean hopping;

    public BHop() {
        super("Bhop", Category.movement);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(speed = new SliderSetting("Speed", 2.0, 0.5, 8.0, 0.1));
        this.registerSetting(autoJump = new ButtonSetting("Auto jump", true));
        this.registerSetting(liquidDisable = new ButtonSetting("Disable in liquid", true));
        this.registerSetting(sneakDisable = new ButtonSetting("Disable while sneaking", true));
        this.registerSetting(stopMotion = new ButtonSetting("Stop motion", false));
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }

    public void onUpdate() {
        if (((mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) && liquidDisable.isToggled()) || (mc.thePlayer.isSneaking() && sneakDisable.isToggled())) {
            return;
        }
        switch ((int) mode.getInput()) {
            case 0:
                if (GeneralUtils.isMoving()) {
                    if (mc.thePlayer.onGround && autoJump.isToggled()) {
                        mc.thePlayer.jump();
                    }
                    mc.thePlayer.setSprinting(true);
                    GeneralUtils.setSpeed(GeneralUtils.getHorizontalSpeed() + 0.005 * speed.getInput());
                    hopping = true;
                    break;
                }
                break;
            case 1:
                if (!GeneralUtils.jumpDown() && GeneralUtils.isMoving() && mc.currentScreen == null) {
                    if (!mc.thePlayer.onGround) {
                        break;
                    }
                    if (autoJump.isToggled()) {
                        mc.thePlayer.jump();
                    }
                    else if (!Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && !autoJump.isToggled()) {
                        return;
                    }
                    mc.thePlayer.setSprinting(true);
                    double horizontalSpeed = GeneralUtils.getHorizontalSpeed();
                    double additionalSpeed = 0.4847 * ((speed.getInput() - 1.0) / 3.0 + 1.0);
                    if (horizontalSpeed < additionalSpeed) {
                        horizontalSpeed = additionalSpeed;
                    }
                    GeneralUtils.setSpeed(horizontalSpeed);
                    hopping = true;
                }
                break;
        }
    }

    public void onDisable() {
        if (stopMotion.isToggled()) {
            mc.thePlayer.motionZ = 0;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionX = 0;
        }
        hopping = false;
    }
}
