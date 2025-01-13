package keystrokesmod.module.impl.player;

import keystrokesmod.event.Render2DEvent;
import keystrokesmod.event.Render3DEvent;
import keystrokesmod.event.TickEvent;
import keystrokesmod.module.Module;
import keystrokesmod.manager.ModuleManager;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.ReflectUtil;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.lenni0451.asmevents.event.enums.EnumEventPriority;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.lwjgl.input.Mouse;

public class AutoPlace extends Module {
    private SliderSetting frameDelay;
    private SliderSetting minPlaceDelay;
    private SliderSetting autoPlaceEvent;
    private ButtonSetting disableLeft;
    private ButtonSetting holdRight;
    private ButtonSetting fastPlaceJump;
    private ButtonSetting pitchCheck;
    private double fDelay = 0.0D;
    private long l = 0L;
    private int f = 0;
    private MovingObjectPosition lm = null;
    private BlockPos lp = null;

    public AutoPlace() {
        super("AutoPlace", Category.player, 0);
        this.registerSetting(new DescriptionSetting("Best with safewalk."));
        this.registerSetting(frameDelay = new SliderSetting("Frame delay", 8.0D, 0.0D, 30.0D, 1.0D));
        this.registerSetting(minPlaceDelay = new SliderSetting("Min place delay", 60.0, 0.0, 500.0, 5.0));
        this.registerSetting(autoPlaceEvent = new SliderSetting("AutoPlace Event", new String[]{"Tick", "Render2D", "Render3D"}, 0));
        this.registerSetting(disableLeft = new ButtonSetting("Disable left", false));
        this.registerSetting(holdRight = new ButtonSetting("Hold right", true));
        this.registerSetting(fastPlaceJump = new ButtonSetting("Fast place on jump", true));
        this.registerSetting(pitchCheck = new ButtonSetting("Pitch check", false));
    }

    public void guiUpdate() {
        if (this.fDelay != frameDelay.getInput()) {
            this.resetVariables();
        }

        this.fDelay = frameDelay.getInput();
    }

    public void onDisable() {
        if (holdRight.isToggled()) {
            this.rd(4);
        }

        this.resetVariables();
    }

    public void onUpdate() {
        if (mc.currentScreen != null || mc.thePlayer.capabilities.isFlying) {
            return;
        }
        final ItemStack getHeldItem = mc.thePlayer.getHeldItem();
        if (getHeldItem == null || !(getHeldItem.getItem() instanceof ItemBlock)) {
            return;
        }
        if (fastPlaceJump.isToggled() && holdRight.isToggled() && !ModuleManager.fastPlace.isEnabled() && Mouse.isButtonDown(1)) {
            if (mc.thePlayer.motionY > 0.0) {
                this.rd(1);
            }
            else if (!pitchCheck.isToggled()) {
                this.rd(1000);
            }
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (autoPlaceEvent.getInput() == 0)
            onEvent();
    }

    @EventTarget
    public void onRender2D(Render2DEvent ev) {
        if (autoPlaceEvent.getInput() == 1)
            onEvent();
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (autoPlaceEvent.getInput() == 2)
            onEvent();
    }

    private void onEvent() {
        if (GeneralUtils.nullCheck()) {
            if (mc.currentScreen == null && !mc.thePlayer.capabilities.isFlying) {
                ItemStack i = mc.thePlayer.getHeldItem();
                if (i != null && i.getItem() instanceof ItemBlock) {
                    MovingObjectPosition m = mc.objectMouseOver;
                    if (disableLeft.isToggled() && Mouse.isButtonDown(0)) {
                        return;
                    }
                    if (m != null && m.typeOfHit == MovingObjectType.BLOCK) {
                        if (this.lm != null && (double) this.f < frameDelay.getInput()) {
                            ++this.f;
                        } else {
                            this.lm = m;
                            BlockPos pos = m.getBlockPos();
                            if (this.lp == null || pos.getX() != this.lp.getX() || pos.getY() != this.lp.getY() || pos.getZ() != this.lp.getZ()) {
                                Block b = mc.theWorld.getBlockState(pos).getBlock();
                                if (b != null && b != Blocks.air && !(b instanceof BlockLiquid)) {
                                    if (!holdRight.isToggled() || Mouse.isButtonDown(1)) {
                                        long n = System.currentTimeMillis();
                                        if (n - this.l >= minPlaceDelay.getInput()) {
                                            this.l = n;
                                            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, i, pos, m.sideHit, m.hitVec)) {
                                                ReflectUtil.setButton(1, true);
                                                mc.thePlayer.swingItem();
                                                mc.getItemRenderer().resetEquippedProgress();
                                                ReflectUtil.setButton(1, false);
                                                this.lp = pos;
                                                this.f = 0;
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void rd(int i) {
        try {
            if (ReflectUtil.rightClickDelayTimerField != null) {
                ReflectUtil.rightClickDelayTimerField.set(mc, i);
            }
        } catch (IllegalAccessException | IndexOutOfBoundsException ignored) {
        }
    }

    private void resetVariables() {
        this.lp = null;
        this.lm = null;
        this.f = 0;
    }
}
