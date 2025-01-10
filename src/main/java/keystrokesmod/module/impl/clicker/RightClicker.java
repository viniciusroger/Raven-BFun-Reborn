package keystrokesmod.module.impl.clicker;

import keystrokesmod.event.Render2DEvent;
import keystrokesmod.event.Render3DEvent;
import keystrokesmod.event.TickEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;
import keystrokesmod.util.ReflectUtil;
import keystrokesmod.util.TimerUtil;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Mouse;

public class RightClicker extends Module {
    private SliderSetting minCps, maxCps, dropChance, clickEvent;
    private ButtonSetting useItem, onlyBlocks, humanize;
    private TimerUtil timer = new TimerUtil();
    private boolean bruh2, bruh3, bruh4;
    private int lastCPS = -1;

    public RightClicker() {
        super("RightClicker", Category.clicker, 0);
        this.registerSetting(minCps = new SliderSetting("Min CPS", 13, 0, 150, 1));
        this.registerSetting(maxCps = new SliderSetting("Max CPS", 16, 0, 150, 1));
        this.registerSetting(dropChance = new SliderSetting("Drop Chance", 60, 0, 100, 1));
        this.registerSetting(clickEvent = new SliderSetting("Click Event", new String[]{"Render3D", "Render2D", "Tick"}, 0));
        this.registerSetting(useItem = new ButtonSetting("Allow Use Item", true));
        this.registerSetting(onlyBlocks = new ButtonSetting("Only Blocks", false));
        this.registerSetting(humanize = new ButtonSetting("Humanize", true));
    }

    @Override
    public void onDisable() {
        reset();
	}

    @Override
    public void guiUpdate() {
        GeneralUtils.correctValue(minCps, maxCps);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (mc.currentScreen != null)
            return;

        if (clickEvent.getInput() == 0)
            onEvent();
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (mc.currentScreen != null)
            return;

        if (clickEvent.getInput() == 1)
            onEvent();
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (!GeneralUtils.nullCheck() || mc.currentScreen != null)
            return;

        if (clickEvent.getInput() == 2)
            onEvent();
    }

    private void onEvent() {
        if (bruh3) {
            ReflectUtil.setButton(1, false);
            bruh3 = false;
        }

        if (Mouse.isButtonDown(1)) {
            if (timer.hasTimePassed(1000 / randomize((int) minCps.getInput(), (int) maxCps.getInput(), (int) dropChance.getInput()), true)) {
                if (onlyBlocks.isToggled() && mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock))
                    return;

                if (useItem.isToggled() && mc.thePlayer.isUsingItem()) {
                    if (!bruh4) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                        bruh4 = true;
                    }

                    return;
                }

                if (bruh4) {
                    bruh4 = false;
                }

                if (!bruh3) {
                    ReflectUtil.setButton(1, true);
                    bruh3 = true;
                }

                click();

                resetClick();
            }
        }
    }

    private void click() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
        bruh2 = true;
    }

    private void reset() {
        bruh2 = false;
        bruh3 = false;
        bruh4 = false;
        lastCPS = -1;
    }

    private void resetClick() {
        if (bruh2) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            bruh2 = false;
        }
    }

    private int randomize(int min, int max, int dropChance) {
        int ran = (int) keystrokesmod.util.RandomUtils.gaussian(RandomUtils.nextInt(Math.min(max, min), Math.max(min, max)), 1);

        for (int i = 0; i < RandomUtils.nextInt(1, 2); i++) {
            ran -= i % RandomUtils.nextInt(1, 2);

            if (ran < min)
                ran += ran;
        }

        if (Math.random() * 100 <= dropChance) {
            for (int i = 0; i < RandomUtils.nextInt(1, 3); i++) {
                ran -= i + 1;

                if (ran < min)
                    ran += i + 1;
            }
        }

        int difference = ran - lastCPS;

        if (humanize.isToggled() && lastCPS != -1 && difference >= 2)
            ran += difference - 1;

        lastCPS = ran;

        return ran;
    }
}
