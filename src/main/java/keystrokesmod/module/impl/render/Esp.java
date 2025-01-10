package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.event.Render3DEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.RenderUtils;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

public class Esp extends Module {
    public SliderSetting red;
    public SliderSetting green;
    public SliderSetting blue;
    public ButtonSetting teamColor;
    public ButtonSetting rainbow;
    private ButtonSetting twoD;
    private ButtonSetting box;
    private ButtonSetting healthBar;
    public ButtonSetting outline;
    private ButtonSetting shaded;
    private ButtonSetting ring;
    public ButtonSetting redOnDamage;
    public ButtonSetting renderSelf;
    private ButtonSetting showInvis;
    private ButtonSetting onlyPlayers;
    private int rgb_c = 0;
    // none, outline, box, shaded, 2d, ring

    public Esp() {
        super("Esp", Category.render, 0);
        this.registerSetting(red = new SliderSetting("Red", 0.0D, 0.0D, 255.0D, 1.0D));
        this.registerSetting(green = new SliderSetting("Green", 255.0D, 0.0D, 255.0D, 1.0D));
        this.registerSetting(blue = new SliderSetting("Blue", 0.0D, 0.0D, 255.0D, 1.0D));
        this.registerSetting(rainbow = new ButtonSetting("Rainbow", false));
        this.registerSetting(teamColor = new ButtonSetting("Team color", false));
        this.registerSetting(new DescriptionSetting("ESP Types"));
        this.registerSetting(twoD = new ButtonSetting("2D", false));
        this.registerSetting(box = new ButtonSetting("Box", false));
        this.registerSetting(healthBar = new ButtonSetting("Health bar", true));
        this.registerSetting(outline = new ButtonSetting("Outline", false));
        this.registerSetting(ring = new ButtonSetting("Ring", false));
        this.registerSetting(shaded = new ButtonSetting("Shaded", false));
        this.registerSetting(redOnDamage = new ButtonSetting("Red on damage", true));
        this.registerSetting(renderSelf = new ButtonSetting("Render self", false));
        this.registerSetting(showInvis = new ButtonSetting("Show invis", true));
        this.registerSetting(onlyPlayers = new ButtonSetting("Only Players", false));
    }

    public void onDisable() {
        RenderUtils.ring_c = false;
    }

    public void guiUpdate() {
        this.rgb_c = (new Color((int) red.getInput(), (int) green.getInput(), (int) blue.getInput())).getRGB();
    }

    @EventTarget
    public void onRenderWorld(Render3DEvent e) {
        if (GeneralUtils.nullCheck()) {
            int rgb = rainbow.isToggled() ? GeneralUtils.getChroma(2L, 0L) : this.rgb_c;
            if (Raven.debugger) {
                for (final Entity entity : mc.theWorld.loadedEntityList) {
                    if (onlyPlayers.isToggled() && !(entity instanceof EntityPlayer))
                        continue;

                    if (entity != mc.thePlayer) {
                        if (teamColor.isToggled()) {
                            rgb = getColorFromTags(entity.getDisplayName().getFormattedText());
                        }
                        this.render(entity, rgb);
                    } else {
                        this.render(entity, rgb);
                    }
                }
                return;
            }
            for (EntityPlayer player : mc.theWorld.playerEntities) {
                if (player != mc.thePlayer || (renderSelf.isToggled() && mc.gameSettings.thirdPersonView > 0)) {
                    if (player.deathTime != 0) {
                        continue;
                    }
                    if (!showInvis.isToggled() && player.isInvisible()) {
                        continue;
                    }
                    if (mc.thePlayer != player && AntiBot.isBot(player)) {
                        continue;
                    }
                    if (teamColor.isToggled()) {
                        rgb = getColorFromTags(player.getDisplayName().getFormattedText());
                    }
                    this.render(player, rgb);
                }
            }
        }
    }

    private void render(Entity en, int rgb) {
        if (box.isToggled()) {
            RenderUtils.renderEntity(en, 1, 0, 0, rgb, redOnDamage.isToggled());
        }

        if (shaded.isToggled()) {
            RenderUtils.renderEntity(en, 2, 0, 0, rgb, redOnDamage.isToggled());
        }

        if (twoD.isToggled()) {
            RenderUtils.renderEntity(en, 3, 0, 0, rgb, redOnDamage.isToggled());
        }

        if (healthBar.isToggled()) {
            RenderUtils.renderEntity(en, 4, 0, 0, rgb, redOnDamage.isToggled());
        }

        if (ring.isToggled()) {
            RenderUtils.renderEntity(en, 6, 0, 0, rgb, redOnDamage.isToggled());
        }
    }

    public int getColorFromTags(String displayName) {
        displayName = GeneralUtils.removeFormatCodes(displayName);
        if (displayName.isEmpty() || !displayName.startsWith("§") || displayName.charAt(1) == 'f') {
            return -1;
        }
        switch (displayName.charAt(1)) {
            case '0':
                return -16777216;
            case '1':
                return -16777046;
            case '2':
                return -16733696;
            case '3':
                return -16733526;
            case '4':
                return -5636096;
            case '5':
                return -5635926;
            case '6':
                return -22016;
            case '7':
                return -5592406;
            case '8':
                return -11184811;
            case '9':
                return -11184641;
            case 'a':
                return -11141291;
            case 'b':
                return -11141121;
            case 'c':
                return -43691;
            case 'd':
                return -43521;
            case 'e':
                return -171;
        }
        return -1;
    }
}
