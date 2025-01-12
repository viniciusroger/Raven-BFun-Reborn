package keystrokesmod.module.impl.clicker;

import keystrokesmod.event.MouseEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.CPSUtil;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.lenni0451.asmevents.event.enums.EnumEventPriority;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class ClickAssist extends Module {
    private SliderSetting chanceLeft, chanceRight;
    private ButtonSetting rightClick, blocksOnly, weaponOnly, onlyWhileTargeting, aboveCPS, leftClick, disableInCreative;
    private Robot bot;
    private boolean ignNL = false;
    private boolean ignNR = false;

    public ClickAssist() {
        super("ClickAssist", Category.clicker, 0);
        this.registerSetting(new DescriptionSetting("Boost your CPS."));
        this.registerSetting(disableInCreative = new ButtonSetting("Disable in creative", true));
        this.registerSetting(leftClick = new ButtonSetting("Left click", true));
        this.registerSetting(chanceLeft = new SliderSetting("Chance left", 80.0D, 0.0D, 100.0D, 1.0D));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", true));
        this.registerSetting(onlyWhileTargeting = new ButtonSetting("Only while targeting", false));
        this.registerSetting(rightClick = new ButtonSetting("Right click", false));
        this.registerSetting(chanceRight = new SliderSetting("Chance right", 80.0D, 0.0D, 100.0D, 1.0D));
        this.registerSetting(blocksOnly = new ButtonSetting("Blocks only", true));
        this.registerSetting(aboveCPS = new ButtonSetting("Above 5 cps", false));
    }

    public void onEnable() {
        try {
            this.bot = new Robot();
        } catch (AWTException var2) {
            this.disable();
        }

    }

    public void onDisable() {
        this.ignNL = false;
        this.ignNR = false;
        this.bot = null;
    }

    @EventTarget(priority = EnumEventPriority.HIGH)
    public void onMouseUpdate(MouseEvent ev) {
        if (disableInCreative.isToggled() && mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }
        if (ev.getButton() >= 0 && ev.isButtonState() && GeneralUtils.nullCheck()) {
            if (mc.currentScreen == null && !mc.thePlayer.isEating()) {
                double ch;
                if (ev.getButton() == 0 && leftClick.isToggled() && chanceLeft.getInput() != 0.0D) {
                    if (this.ignNL) {
                        this.ignNL = false;
                    }
                    else {
                        if (chanceLeft.getInput() == 0) {
                            return;
                        }
                        if (weaponOnly.isToggled() && !GeneralUtils.holdingWeapon()) {
                            return;
                        }

                        if (onlyWhileTargeting.isToggled() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) {
                            return;
                        }

                        if (chanceLeft.getInput() != 100.0D) {
                            ch = Math.random();
                            if (ch >= chanceLeft.getInput() / 100.0D) {
                                this.fix(0);
                                return;
                            }
                        }

                        this.bot.mouseRelease(16);
                        this.bot.mousePress(16);
                        this.ignNL = true;
                    }
                }
                else if (ev.getButton() == 1 && rightClick.isToggled()) {
                    if (this.ignNR) {
                        this.ignNR = false;
                    }
                    else {
                        if (chanceRight.getInput() == 0) {
                            return;
                        }
                        if (blocksOnly.isToggled()) {
                            ItemStack item = mc.thePlayer.getHeldItem();
                            if (item == null || !(item.getItem() instanceof ItemBlock)) {
                                this.fix(1);
                                return;
                            }
                        }

                        if (aboveCPS.isToggled() && CPSUtil.i() <= 5) {
                            this.fix(1);
                            return;
                        }

                        if (chanceRight.getInput() != 100.0D) {
                            ch = Math.random();
                            if (ch >= chanceRight.getInput() / 100.0D) {
                                this.fix(1);
                                return;
                            }
                        }

                        this.bot.mouseRelease(4);
                        this.bot.mousePress(4);
                        this.ignNR = true;
                    }
                }
            }
            this.fix(0);
            this.fix(1);
        }
    }

    private void fix(int t) {
        if (t == 0) {
            if (this.ignNL && !Mouse.isButtonDown(0)) {
                this.bot.mouseRelease(16);
            }
        }
        else if (t == 1 && this.ignNR && !Mouse.isButtonDown(1)) {
            this.bot.mouseRelease(4);
        }

    }
}
