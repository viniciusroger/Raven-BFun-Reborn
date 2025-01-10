package keystrokesmod.module.impl.player;

import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.BlockUtils;
import keystrokesmod.util.GeneralUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

public class AutoTool extends Module {
    private SliderSetting hoverDelay;
    private ButtonSetting rightDisable;
    private ButtonSetting requireMouse;
    private ButtonSetting swap;
    private int previousSlot = -1;
    private int ticksHovered;
    private BlockPos currentBlock;
    public AutoTool() {
        super("AutoTool", Category.player);
        this.registerSetting(hoverDelay = new SliderSetting("Hover delay", 0.0, 0.0, 20.0, 1.0));
        this.registerSetting(rightDisable = new ButtonSetting("Disable while right click", true));
        this.registerSetting(requireMouse = new ButtonSetting("Require mouse down", true));
        this.registerSetting(swap = new ButtonSetting("Swap to previous slot", true));
    }

    public void onDisable() {
        resetVariables();
    }

    public void setSlot(final int currentItem) {
        if (currentItem == -1) {
            return;
        }
        mc.thePlayer.inventory.currentItem = currentItem;
    }

    public void onUpdate() {
        if (!mc.inGameHasFocus || mc.currentScreen != null || (rightDisable.isToggled() && Mouse.isButtonDown(1)) || !mc.thePlayer.capabilities.allowEdit) {
            resetVariables();
            return;
        }
        if (!Mouse.isButtonDown(0) && requireMouse.isToggled()) {
            resetSlot();
            return;
        }
        MovingObjectPosition over = mc.objectMouseOver;
        if (over == null || over.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            resetSlot();
            resetVariables();
            return;
        }
        if (over.getBlockPos().equals(currentBlock)) {
            ticksHovered++;
        }
        else {
            ticksHovered = 0;
        }
        currentBlock = over.getBlockPos();
        if (hoverDelay.getInput() == 0 || ticksHovered > hoverDelay.getInput()) {
            int slot = GeneralUtils.getTool(BlockUtils.getBlock(currentBlock));
            if (slot == -1) {
                return;
            }
            if (previousSlot == -1) {
                previousSlot = mc.thePlayer.inventory.currentItem;
            }
            setSlot(slot);
        }
    }

    private void resetVariables() {
        ticksHovered = 0;
        resetSlot();
        previousSlot = -1;
    }

    private void resetSlot() {
        if (previousSlot == -1 || !swap.isToggled()) {
            return;
        }
        setSlot(previousSlot);
        previousSlot = -1;
    }
}
