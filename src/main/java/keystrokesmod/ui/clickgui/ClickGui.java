package keystrokesmod.ui.clickgui;

import keystrokesmod.Raven;
import keystrokesmod.ui.clickgui.components.Component;
import keystrokesmod.ui.clickgui.components.impl.BindComponent;
import keystrokesmod.ui.clickgui.components.impl.CategoryComponent;
import keystrokesmod.ui.clickgui.components.impl.ModuleComponent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.misc.Timer;
import keystrokesmod.util.GeneralUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ClickGui extends GuiScreen {
    private ScheduledFuture sf;
    private Timer aT;
    private Timer aL;
    private Timer aE;
    private Timer aR;
    public static ArrayList<CategoryComponent> categories;

    public ClickGui() {
        categories = new ArrayList<>();
        int y = 5;

        for (Module.Category cat : Module.Category.values()) {
            CategoryComponent f = new CategoryComponent(cat);
            f.setY(y);
            categories.add(f);
            y += 20;
        }


    }

    public void initMain() {
        (this.aT = this.aE = this.aR = new Timer(500.0F)).start();
        this.sf = Raven.getExecutor().schedule(() -> {
            (this.aL = new Timer(650.0F)).start();
        }, 650L, TimeUnit.MILLISECONDS);
    }

    public void initGui() {
        super.initGui();
    }

    public void drawScreen(int x, int y, float p) {
        drawRect(0, 0, this.width, this.height, (int) (this.aR.getValueFloat(0.0F, 0.7F, 2) * 255.0F) << 24);
        int r;

        if (!Gui.removeWatermark.isToggled()) {
            int h = this.height / 4;
            int wd = this.width / 2;
            int w_c = 30 - this.aT.getValueInt(0, 30, 3);
            this.drawCenteredString(this.fontRendererObj, "r", wd + 1 - w_c, h - 25, GeneralUtils.getChroma(2L, 1500L));
            this.drawCenteredString(this.fontRendererObj, "a", wd - w_c, h - 15, GeneralUtils.getChroma(2L, 1200L));
            this.drawCenteredString(this.fontRendererObj, "v", wd - w_c, h - 5, GeneralUtils.getChroma(2L, 900L));
            this.drawCenteredString(this.fontRendererObj, "e", wd - w_c, h + 5, GeneralUtils.getChroma(2L, 600L));
            this.drawCenteredString(this.fontRendererObj, "n", wd - w_c, h + 15, GeneralUtils.getChroma(2L, 300L));
            this.drawCenteredString(this.fontRendererObj, "bS", wd + 1 + w_c, h + 30, GeneralUtils.getChroma(2L, 0L));
            this.drawVerticalLine(wd - 10 - w_c, h - 30, h + 43, Color.white.getRGB());
            this.drawVerticalLine(wd + 10 + w_c, h - 30, h + 43, Color.white.getRGB());
            if (this.aL != null) {
                r = this.aL.getValueInt(0, 20, 2);
                this.drawHorizontalLine(wd - 10, wd - 10 + r, h - 29, -1);
                this.drawHorizontalLine(wd + 10, wd + 10 - r, h + 42, -1);
            }
        }

        for (CategoryComponent c : categories) {
            c.render(this.fontRendererObj);
            c.updatePosition(x, y);

            for (Component m : c.getModules()) {
                m.drawScreen(x, y);
            }
        }

        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        if (!Gui.removePlayerModel.isToggled()) {
            GuiInventory.drawEntityOnScreen(this.width + 15 - this.aE.getValueInt(0, 40, 2), this.height - 10, 40, (float) (this.width - 25 - x), (float) (this.height - 50 - y), this.mc.thePlayer);
        }
    }

    public void mouseClicked(int x, int y, int m) throws IOException {
        for (CategoryComponent category : categories) {
			if (category.isOver(x, y) && !category.isOverTitle(x, y) && !category.isOverText(x, y) && m == 0) {
				category.isOverText(true);
				category.xx = x - category.getX();
				category.yy = y - category.getY();
			}

			if (category.isOverText(x, y) && m == 0) {
				category.mouseClicked(!category.isCategoryOpened());
			}

			if (category.isOverTitle(x, y) && m == 0) {
				category.setPin(!category.isPin());
			}

            if (!category.isCategoryOpened())
                continue;

            if (category.getModules().isEmpty())
                continue;

			for (Component c : category.getModules()) {
				c.onClick(x, y, m);
			}
		}
    }

    public void mouseReleased(int x, int y, int s) {
        if (s == 0) {
            for (CategoryComponent category : categories) {
                category.isOverText(false);
                if (category.isCategoryOpened() && !category.getModules().isEmpty()) {
                    for (Component module : category.getModules()) {
                        module.mouseReleased(x, y, s);
                    }
                }
            }
        }
    }

    @Override
    public void keyTyped(char t, int k) {
        if (k == Keyboard.KEY_ESCAPE && !binding()) {
            this.mc.displayGuiScreen(null);
        } else {
            for (CategoryComponent category : categories) {
                if (category.isCategoryOpened() && !category.getModules().isEmpty()) {
                    for (Component module : category.getModules()) {
                        module.keyTyped(t, k);
                    }
                }
            }
        }
    }

    public void onGuiClosed() {
        this.aL = null;
        if (this.sf != null) {
            this.sf.cancel(true);
            this.sf = null;
        }
        for (CategoryComponent c : categories) {
            c.dragging = false;
            for (Component m : c.getModules()) {
                m.onGuiClosed();
            }
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    private boolean binding() {
        for (CategoryComponent c : categories) {
            for (ModuleComponent m : c.getModules()) {
                for (Component component : m.settings) {
                    if (component instanceof BindComponent && ((BindComponent) component).isBinding) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
