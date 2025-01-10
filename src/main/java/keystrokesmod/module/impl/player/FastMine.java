package keystrokesmod.module.impl.player;

import keystrokesmod.event.TickEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.BlockUtils;
import keystrokesmod.util.ReflectUtil;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import org.lwjgl.input.Mouse;

public class FastMine extends Module {
    private SliderSetting delay;
    public SliderSetting multiplier;
    private SliderSetting mode;
    private ButtonSetting creativeDisable;
    private float lastCurBlockDamageMP;
    private String[] modes = new String[]{"Pre", "Post", "Increment"};
    
    public FastMine() {
        super("FastMine", Category.player);
        this.registerSetting(new DescriptionSetting("Default is 5 delay & 1x speed."));
        this.registerSetting(delay = new SliderSetting("Break delay ticks", 5.0, 0.0, 5.0, 1.0));
        this.registerSetting(multiplier = new SliderSetting("Break speed multiplier", 1.0, 1.0, 2.0, 0.02, "x"));
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(creativeDisable = new ButtonSetting("Disable in creative", true));
    }

    @Override
    public String getInfo() {
        return ((int) multiplier.getInput() == multiplier.getInput() ? (int) multiplier.getInput() + "" : multiplier.getInput()) + multiplier.getInfo();
    }

    @EventTarget
    public void a(TickEvent e) {
        if (!mc.inGameHasFocus || !GeneralUtils.nullCheck()) {
            return;
        }
        if (creativeDisable.isToggled() && mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }
        final int delay = (int) this.delay.getInput();
        if (delay < 5.0) {
            try {
                if (delay == 0.0) {
                    ReflectUtil.blockHitDelay.set(mc.playerController, 0);
                } else if (ReflectUtil.blockHitDelay.getInt(mc.playerController) > delay) {
                    ReflectUtil.blockHitDelay.set(mc.playerController, delay);
                }
            } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
            }
		}
        final double c = multiplier.getInput();
        if (c > 1.0) {
            if (!mc.thePlayer.capabilities.isCreativeMode && Mouse.isButtonDown(0)) {
                try {
                    final float float1 = ReflectUtil.curBlockDamageMP.getFloat(mc.playerController);
                    switch ((int) mode.getInput()) {
                        case 0: {
                            float n = (float) (1.0 - 1.0 / c);
                            if (float1 > 0.0f && float1 < n) {
                                ReflectUtil.curBlockDamageMP.set(mc.playerController, n);
                                break;
                            }
                            break;
                        }
                        case 1: {
                            final double n2 = 1.0 / c;
                            if (float1 < 1.0f && float1 >= n2) {
                                ReflectUtil.curBlockDamageMP.set(mc.playerController, 1);
                                break;
                            }
                            break;
                        }
                        case 2: {
                            float n3 = -1.0f;
                            if (float1 < 1.0f) {
                                if (mc.objectMouseOver != null && float1 > this.lastCurBlockDamageMP) {
                                    n3 = (float) (this.lastCurBlockDamageMP + BlockUtils.getBlockHardness(mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock(), mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem), false, false) * (c - 0.2152857 * (c - 1.0)));
                                }
                                if (n3 != -1.0f && float1 > 0.0f) {
                                    ReflectUtil.curBlockDamageMP.set(mc.playerController, n3);
                                }
                            }
                            this.lastCurBlockDamageMP = float1;
                            break;
                        }
                    }
                } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
                }
            } else if (mode.getInput() == 2) {
                this.lastCurBlockDamageMP = 0.0f;
            }
        }
    }
}
