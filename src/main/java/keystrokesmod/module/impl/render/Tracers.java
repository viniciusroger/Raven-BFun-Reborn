package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.event.Render3DEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.RenderUtils;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public class Tracers extends Module {
    public ButtonSetting showInvis;
    public SliderSetting red;
    public SliderSetting green;
    public SliderSetting blue;
    public ButtonSetting rainbow;
    public SliderSetting lineWidth;
    private boolean g;
    private int rgb_c = 0;

    public Tracers() {
        super("Tracers", Category.render, 0);
        this.registerSetting(showInvis = new ButtonSetting("Show invis", true));
        this.registerSetting(lineWidth = new SliderSetting("Line Width", 1.0D, 1.0D, 5.0D, 1.0D));
        this.registerSetting(red = new SliderSetting("Red", 0.0D, 0.0D, 255.0D, 1.0D));
        this.registerSetting(green = new SliderSetting("Green", 255.0D, 0.0D, 255.0D, 1.0D));
        this.registerSetting(blue = new SliderSetting("Blue", 0.0D, 0.0D, 255.0D, 1.0D));
        this.registerSetting(rainbow = new ButtonSetting("Rainbow", false));
    }

    public void onEnable() {
        this.g = mc.gameSettings.viewBobbing;
        if (this.g) {
            mc.gameSettings.viewBobbing = false;
        }

    }

    public void onDisable() {
        mc.gameSettings.viewBobbing = this.g;
    }

    public void onUpdate() {
        if (mc.gameSettings.viewBobbing) {
            mc.gameSettings.viewBobbing = false;
        }

    }

    public void guiUpdate() {
        this.rgb_c = (new Color((int) red.getInput(), (int) green.getInput(), (int) blue.getInput())).getRGB();
    }

    @EventTarget
    public void o(Render3DEvent ev) {
        if (GeneralUtils.nullCheck()) {
            int rgb = rainbow.isToggled() ? GeneralUtils.getChroma(2L, 0L) : this.rgb_c;

            for (Entity en : mc.theWorld.loadedEntityList) {
                if (Raven.debugger) {
                    if (en instanceof EntityLivingBase && en != mc.thePlayer) {
                        RenderUtils.dtl(en, rgb, (float) lineWidth.getInput());
                    }
                } else {
                    if (en instanceof EntityLivingBase) {
                        if (en == mc.thePlayer)
                            continue;

                        if (((EntityLivingBase) en).deathTime != 0)
                            continue;

                        if (!showInvis.isToggled() && en.isInvisible())
                            continue;

                        if (!AntiBot.isBot(en)) {
                            RenderUtils.dtl(en, rgb, (float) lineWidth.getInput());
                        }
                    }
                }
            }
        }
    }
}
