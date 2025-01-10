package keystrokesmod.module.impl.render;

import keystrokesmod.event.Render2DEvent;
import keystrokesmod.event.Render3DEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.combat.KillAura;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.RenderUtils;
import keystrokesmod.enums.Theme;
import keystrokesmod.misc.Timer;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public class TargetHUD extends Module {
    private SliderSetting theme;
    private ButtonSetting renderEsp;
    private ButtonSetting showStatus;
    private ButtonSetting healthColor;
    private Timer fadeTimer;
    private Timer healthBarTimer = null;
    private EntityLivingBase target;
    private long lastAliveMS;
    private double lastHealth;
    private float lastHealthBar;
    public EntityLivingBase renderEntity;

    public TargetHUD() {
        super("TargetHUD", Category.render);
        this.registerSetting(new DescriptionSetting("Only works with KillAura."));
        this.registerSetting(theme = new SliderSetting("Theme", Theme.themes, 0));
        this.registerSetting(renderEsp = new ButtonSetting("Render ESP", true));
        this.registerSetting(showStatus = new ButtonSetting("Show win or loss", true));
        this.registerSetting(healthColor = new ButtonSetting("Traditional health color", false));
    }

    public void onDisable() {
        reset();
    }

    @EventTarget
    public void onRenderTick(Render2DEvent ev) {
        if (!GeneralUtils.nullCheck()) {
            reset();
            return;
        }

        if (mc.currentScreen != null) {
            reset();
            return;
        }
        if (KillAura.target != null) {
            target = KillAura.target;
            lastAliveMS = System.currentTimeMillis();
            fadeTimer = null;
        } else if (target != null) {
            if (System.currentTimeMillis() - lastAliveMS >= 200 && fadeTimer == null) {
                (fadeTimer = new Timer(400)).start();
            }
        }
        else {
            return;
        }
        String playerInfo = target.getDisplayName().getFormattedText();
        double health = target.getHealth() / target.getMaxHealth();
        if (health != lastHealth) {
            (healthBarTimer = new Timer(350)).start();
        }
        lastHealth = health;
        playerInfo += " " + GeneralUtils.getHealthStr(target);
        drawTargetHUD(fadeTimer, playerInfo, health);
    }

    @EventTarget
    public void onRenderWorld(Render3DEvent renderWorldLastEvent) {
        if (!renderEsp.isToggled() || !GeneralUtils.nullCheck()) {
            return;
        }
        if (KillAura.target != null) {
            RenderUtils.renderEntity(KillAura.target, 2, 0.0, 0.0, Theme.getGradient((int) theme.getInput(), 0), false);
        }
        else if (renderEntity != null) {
            RenderUtils.renderEntity(renderEntity, 2, 0.0, 0.0, Theme.getGradient((int) theme.getInput(), 0), false);
        }
    }

    private void drawTargetHUD(Timer cd, String string, double health) {
        if (showStatus.isToggled()) {
            string = string + " " + ((health <= GeneralUtils.getCompleteHealth(mc.thePlayer) / mc.thePlayer.getMaxHealth()) ? "§aW" : "§cL");
        }
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int n2 = 8;
        final int n3 = mc.fontRendererObj.getStringWidth(string) + n2;
        final int n4 = scaledResolution.getScaledWidth() / 2 - n3 / 2 + 70;
        final int n5 = scaledResolution.getScaledHeight() / 2 + 15 + 30;
        final int n6 = n4 - n2;
        final int n7 = n5 - n2;
        final int n8 = n4 + n3;
        final int n9 = n5 + (mc.fontRendererObj.FONT_HEIGHT + 5) - 6 + n2;
        final int n10 = (cd == null) ? 255 : (255 - cd.getValueInt(0, 255, 1));
        if (n10 > 0) {
            final int n11 = Math.min(n10, 110);
            final int n12 = Math.min(n10, 210);
            final int[] array = Theme.getGradients((int) theme.getInput());
            RenderUtils.drawRoundedGradientOutlinedRectangle((float) n6, (float) n7, (float) n8, (float) (n9 + 13), 10.0f, GeneralUtils.merge(Color.black.getRGB(), n11), GeneralUtils.merge(array[0], n10), GeneralUtils.merge(array[1], n10)); // outline
            final int n13 = n6 + 6;
            final int n14 = n8 - 6;
            final int n15 = n9;
            RenderUtils.drawRoundedRectangle((float) n13, (float) n15, (float) n14, (float) (n15 + 5), 4.0f, GeneralUtils.merge(Color.black.getRGB(), n11)); // background
            int k = GeneralUtils.merge(array[0], n12);
            int n16 = GeneralUtils.merge(array[1], n12);
            float healthBar = (float) (int) (n14 + (n13 - n14) * (1.0 - (Math.max(health, 0.05))));
            if (healthBar - n13 < 3) { // if goes below, the rounded health bar glitches out
                healthBar = n13 + 3;
            }
            if (healthBar != lastHealthBar && lastHealthBar - n13 >= 3 && healthBarTimer != null ) {
                float diff = lastHealthBar - healthBar;
                if (diff > 0) {
                    lastHealthBar = lastHealthBar - healthBarTimer.getValueFloat(0, diff, 1);
                }
                else {
                    lastHealthBar = healthBarTimer.getValueFloat(lastHealthBar, healthBar, 1);
                }
            }
            else {
                lastHealthBar = healthBar;
            }
            if (healthColor.isToggled()) {
                k = n16 = GeneralUtils.merge(GeneralUtils.getColorForHealth(health), n12);
            }
            RenderUtils.drawRoundedGradientRect((float) n13, (float) n15, lastHealthBar, (float) (n15 + 5), 4.0f, k, k, k, n16); // health bar
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            mc.fontRendererObj.drawString(string, (float) n4, (float) n5, (new Color(220, 220, 220, 255).getRGB() & 0xFFFFFF) | GeneralUtils.clamp(n10 + 15) << 24, true);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
        else {
            target = null;
            healthBarTimer = null;
        }
    }

    private void reset() {
        fadeTimer = null;
        target = null;
        healthBarTimer = null;
        renderEntity = null;
    }
}
