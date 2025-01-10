package keystrokesmod.module.impl.clicker;

import keystrokesmod.Raven;
import keystrokesmod.event.Render2DEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.ReflectUtil;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;

public class BurstClicker extends Module {
    private SliderSetting clicks;
    private SliderSetting delay;
    private ButtonSetting delayRandomizer;
    private ButtonSetting placeWhenBlock;
    private boolean l_c = false;
    private boolean l_r = false;

    public BurstClicker() {
        super("BurstClicker", Category.clicker, 0);
        this.registerSetting(new DescriptionSetting("Artificial dragclicking."));
        this.registerSetting(clicks = new SliderSetting("Clicks", 0.0D, 0.0D, 150.0D, 1.0D));
        this.registerSetting(delay = new SliderSetting("Delay (ms)", 5.0D, 1.0D, 40.0D, 1.0D));
        this.registerSetting(delayRandomizer = new ButtonSetting("Delay randomizer", true));
        this.registerSetting(placeWhenBlock = new ButtonSetting("Place when block", false));
    }

    public void onEnable() {
        if (clicks.getInput() != 0.0D && mc.currentScreen == null && mc.inGameHasFocus) {
            Raven.getExecutor().execute(() -> {
                try {
                    int cl = (int) clicks.getInput();
                    int del = (int) delay.getInput();

                    for (int i = 0; i < cl * 2 && this.isEnabled() && GeneralUtils.nullCheck() && mc.currentScreen == null && mc.inGameHasFocus; ++i) {
                        if (i % 2 == 0) {
                            this.l_c = true;
                            if (del != 0) {
                                int realDel = del;
                                if (delayRandomizer.isToggled()) {
                                    realDel = del + GeneralUtils.getRandom().nextInt(25) * (GeneralUtils.getRandom().nextBoolean() ? -1 : 1);
                                    if (realDel <= 0) {
                                        realDel = del / 3 - realDel;
                                    }
                                }

                                Thread.sleep(realDel);
                            }
                        } else {
                            this.l_r = true;
                        }
                    }

                    this.disable();
                } catch (InterruptedException var5) {
                }

            });
        } else {
            this.disable();
        }
    }

    public void onDisable() {
        this.l_c = false;
        this.l_r = false;
    }

    @EventTarget
    public void r(Render2DEvent ev) {
        if (GeneralUtils.nullCheck()) {
            if (this.l_c) {
                this.c(true);
                this.l_c = false;
            } else if (this.l_r) {
                this.c(false);
                this.l_r = false;
            }
        }

    }

    private void c(boolean st) {
        boolean r = placeWhenBlock.isToggled() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock;
        if (r) {
            ReflectUtil.rightClick();
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
            if (st)
                KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
        }

        ReflectUtil.setButton(r ? 1 : 0, st);
    }
}
