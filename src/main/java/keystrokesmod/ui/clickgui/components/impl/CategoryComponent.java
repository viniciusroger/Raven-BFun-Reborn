package keystrokesmod.ui.clickgui.components.impl;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.util.MouseUtil;
import keystrokesmod.util.RenderUtils;
import keystrokesmod.misc.Timer;
import keystrokesmod.util.GeneralUtils;
import keystrokesmod.profile.Manager;
import keystrokesmod.profile.Profile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CategoryComponent {
    public List<ModuleComponent> modules = new CopyOnWriteArrayList<>();
    public Module.Category categoryName;
    private boolean categoryOpened;
    private int width;
    private int y;
    private int x;
    private int height;
    public boolean dragging;
    public int xx;
    public int yy;
    public boolean n4m = false;
    public String pvp;
    public boolean pin = false;
    public boolean hovering = false;
    private Timer smoothTimer;
    public int scale;
    private float big;
    private final int translucentBackground = new Color(0, 0, 0, 110).getRGB();
    private final  int background = new Color(0, 0, 0, 255).getRGB();
    private final  int regularOutline = new Color(81, 99, 149).getRGB();
    private final  int regularOutline2 = new Color(97, 67, 133).getRGB();
    private final  int categoryNameColor = new Color(220, 220, 220).getRGB();
    private final  int categoryCloseColor = new Color(250, 95, 85).getRGB();
    private final  int categoryOpenColor = new Color(135, 238, 144).getRGB();

    public CategoryComponent(Module.Category category) {
        this.categoryName = category;
        this.width = 92;
        this.x = 5;
        this.y = 5;
        this.height = 13;
        this.smoothTimer = null;
        this.xx = 0;
        this.categoryOpened = false;
        this.dragging = false;
        int tY = this.height + 3;
        this.scale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();

        for (Module mod : Raven.getModuleManager().inCategory(this.categoryName)) {
            ModuleComponent b = new ModuleComponent(mod, this, tY);
            this.modules.add(b);
            tY += 16;
        }
    }

    public List<ModuleComponent> getModules() {
        return this.modules;
    }

    public void reloadModules() {
        this.modules.clear();
        this.height = 13;
        int tY = this.height + 3;

        if (this.categoryName == Module.Category.profiles) {
            ModuleComponent manager = new ModuleComponent(new Manager(), this, tY);
            this.modules.add(manager);

            if (Raven.profileManager == null) {
                return;
            }

			for (Profile profile : Raven.profileManager.profiles) {
				tY += 16;
				ModuleComponent b = new ModuleComponent(profile.getModule(), this, tY);
				this.modules.add(b);
			}
        }
    }

    public void setX(int n) {
        this.x = n;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void isOverText(boolean dragging) {
        this.dragging = dragging;
    }

    public boolean isPin() {
        return this.pin;
    }

    public void setPin(boolean on) {
        this.pin = on;
    }

    public boolean isCategoryOpened() {
        return this.categoryOpened;
    }

    public void mouseClicked(boolean on) {
        this.categoryOpened = on;
        (this.smoothTimer = new Timer(150)).start();
    }

    public void render(FontRenderer renderer) {
        this.width = 92;
        int h = 0;
        if (!this.modules.isEmpty() && this.categoryOpened) {
            for (ModuleComponent c : modules) {
                h += c.getHeight();
            }
            big = h;
        }

        float extra = smoothTimer == null ? this.y + this.height + h + 4 : smoothTimer.getValueFloat(this.y + this.height + 4, this.y + this.height + h + 4, 1);

        if (!this.categoryOpened) {
            extra = smoothTimer == null ? this.y + this.height + h + 4 : (this.y + this.height + 4 + big) - smoothTimer.getValueFloat(0, big, 1);
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.scissor(0, this.y - 2, this.x + this.width + 4, extra - this.y + 4);
        RenderUtils.drawRoundedGradientOutlinedRectangle(this.x - 2, this.y, this.x + this.width + 2, extra, 9, Gui.translucentBackground.isToggled() ? translucentBackground : background,
                ((categoryOpened || hovering) && Gui.rainBowOutlines.isToggled()) ? RenderUtils.setAlpha(GeneralUtils.getChroma(2, 0), 0.5) : regularOutline, ((categoryOpened || hovering) && Gui.rainBowOutlines.isToggled()) ? RenderUtils.setAlpha(GeneralUtils.getChroma(2, 700), 0.5) : regularOutline2);

        renderer.drawString(this.n4m ? this.pvp : this.categoryName.name(), (float) (this.x + 2), (float) (this.y + 4), categoryNameColor, false);
        if (!this.n4m) {
            GL11.glPushMatrix();
            renderer.drawString(this.categoryOpened ? "-" : "+", (float) (this.x + 80), (float) ((double) this.y + 4.5D), this.categoryOpened ? categoryCloseColor : categoryOpenColor, false);
            GL11.glPopMatrix();
            if (this.categoryOpened && !this.modules.isEmpty()) {
                for (ModuleComponent c2 : modules) {
                    c2.render();
                }
            }

        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

    public void render() {
        int o = this.height + 3;

        for (ModuleComponent c : modules) {
            c.setOffset(o);
            o += c.getHeight();
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public void updatePosition(int x, int y) {
        if (this.dragging) {
            this.setX(x - this.xx);
            this.setY(y - this.yy);
        }

        hovering = isOverCategory(x, y);
    }

    public boolean isOverTitle(int x, int y) {
        return x >= this.x + 92 - 13 && x <= this.x + this.width && (float) y >= (float) this.y + 2.0F && y <= this.y + this.height + 1;
    }

    public boolean isOverText(int x, int y) {
        return x >= this.x + 77 && x <= this.x + this.width - 6 && (float) y >= (float) this.y + 2.0F && y <= this.y + this.height + 1;
    }

    public boolean isOverCategory(int x, int y) {
        return x >= this.x - 2 && x <= this.x + this.width + 2 && (float) y >= (float) this.y + 2.0F && y <= this.y + this.height + 1;
    }

    public boolean isOver(int x, int y) {
        return MouseUtil.isOver(x, y, this.x, this.y, this.width, this.height);
    }
}
