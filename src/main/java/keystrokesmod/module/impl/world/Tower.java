package keystrokesmod.module.impl.world;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.manager.ModuleManager;
import keystrokesmod.module.impl.player.SafeWalk;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import org.lwjgl.input.Keyboard;

public class Tower extends Module {
    private SliderSetting mode;
    private SliderSetting slowedSpeed;
    private SliderSetting slowedTicks;
    private ButtonSetting disableWhileCollided;
    private ButtonSetting disableWhileHurt;
    private ButtonSetting sprintJumpForward;
    private String[] modes = new String[]{"Vanilla"};
    private int slowTicks;
    private boolean wasTowering;
    private int offGroundTicks;
    public Tower() {
        super("Tower", Category.world);
        this.registerSetting(new DescriptionSetting("Works with Safewalk & Scaffold"));
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(slowedSpeed = new SliderSetting("Slowed speed", 2, 0, 9, 0.1));
        this.registerSetting(slowedTicks = new SliderSetting("Slowed ticks", 1, 0, 20, 1));
        this.registerSetting(disableWhileCollided = new ButtonSetting("Disable while collided", false));
        this.registerSetting(disableWhileHurt = new ButtonSetting("Disable while hurt", false));
        this.registerSetting(sprintJumpForward = new ButtonSetting("Sprint jump forward", false));
        this.canBeEnabled = false;
    }

    @EventTarget
    public void onPreMotion(PreMotionEvent e) {
        if (canTower()) {
            wasTowering = true;
            switch ((int) mode.getInput()) {
                case 0:
                    offGroundTicks++;
                    if (mc.thePlayer.onGround) {
                        offGroundTicks = 0;
                    }
                    mc.thePlayer.motionY = 0.41965;
                    switch (offGroundTicks) {
                        case 1:
                            mc.thePlayer.motionY = 0.33;
                            break;
                        case 2:
                            mc.thePlayer.motionY = 1 - mc.thePlayer.posY % 1;
                            break;
                    }
                    if (offGroundTicks >= 3) {
                        offGroundTicks = 0;
                    }
                    break;
            }
        }
        else {
            if (wasTowering && slowedTicks.getInput() > 0 && modulesEnabled()) {
                if (slowTicks++ < slowedTicks.getInput()) {
                    GeneralUtils.setSpeed(Math.max(slowedSpeed.getInput() * 0.1 - 0.25, 0));
                }
                else {
                    slowTicks = 0;
                    wasTowering = false;
                }
            }
            else {
                if (wasTowering) {
                    wasTowering = false;
                }
                slowTicks = 0;
            }
            reset();
        }
    }

    private void reset() {
        offGroundTicks = 0;
    }

    private boolean canTower() {
        if (!GeneralUtils.nullCheck() || !GeneralUtils.jumpDown()) {
            return false;
        }
        else if (disableWhileHurt.isToggled() && mc.thePlayer.hurtTime >= 9) {
            return false;
        }
        else if (disableWhileCollided.isToggled() && mc.thePlayer.isCollidedHorizontally) {
            return false;
        }
        else if (modulesEnabled()) {
            return true;
        }
        return false;
    }

    private boolean modulesEnabled() {
        return  ((ModuleManager.safeWalk.isEnabled() && ModuleManager.safeWalk.tower.isToggled() && SafeWalk.canSafeWalk()) || (ModuleManager.scaffold.isEnabled() && ModuleManager.scaffold.tower.isToggled()));
    }

    public boolean canSprint() {
        return canTower() && this.sprintJumpForward.isToggled() && Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()) && GeneralUtils.jumpDown();
    }
}
