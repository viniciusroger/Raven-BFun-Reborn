package keystrokesmod.ui.clickgui.components.impl;

import keystrokesmod.Raven;
import keystrokesmod.ui.clickgui.components.Component;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.client.Gui;
import keystrokesmod.enums.Theme;
import keystrokesmod.profile.ProfileModule;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class BindComponent extends Component {
    public boolean isBinding;
    private ModuleComponent moduleComponent;
    private int bind;
    private int x;
    private int y;

    public BindComponent(ModuleComponent moduleComponent, int bind) {
        this.moduleComponent = moduleComponent;
        this.x = moduleComponent.categoryComponent.getX() + moduleComponent.categoryComponent.getWidth();
        this.y = moduleComponent.categoryComponent.getY() + moduleComponent.o;
        this.bind = bind;
    }

    public void setOffset(int n) {
        this.bind = n;
    }

    public void render() {
        GL11.glPushMatrix();
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        this.drawString(!this.moduleComponent.mod.canBeEnabled() ? EnumChatFormatting.RED + "This module cannot be enable" : this.isBinding ? "Press a key..." : "Current bind: '§e" + (this.moduleComponent.mod.getKeycode() >= 1000 ? "M" + (this.moduleComponent.mod.getKeycode() - 1000) : Keyboard.getKeyName(this.moduleComponent.mod.getKeycode())) + "§r'");
        GL11.glPopMatrix();
    }

    public void drawScreen(int x, int y) {
        this.y = this.moduleComponent.categoryComponent.getY() + this.bind;
        this.x = this.moduleComponent.categoryComponent.getX();
    }

    public void onClick(int x, int y, int button) {
        if (isOverBind(x, y) && this.moduleComponent.po && this.moduleComponent.mod.canBeEnabled()) {
            if (button == 0) {
                this.isBinding = !this.isBinding;
            }
            else if (button == 1 && this.moduleComponent.mod.moduleCategory() != Module.Category.profiles) {
                this.moduleComponent.mod.setHidden(!this.moduleComponent.mod.isHidden());
                if (Raven.currentProfile != null) {
                    ((ProfileModule) Raven.currentProfile.getModule()).saved = false;
                }
            }
            else if (button > 1) {
                if (this.isBinding) {
                    this.moduleComponent.mod.setBind(button + 1000);
                    if (Raven.currentProfile != null) {
                        ((ProfileModule) Raven.currentProfile.getModule()).saved = false;
                    }
                    this.isBinding = false;
                }
            }
        }
    }

    public void keyTyped(char typedChar, int key) {
        if (this.isBinding) {
            if (key == Keyboard.KEY_0 || key == Keyboard.KEY_ESCAPE) {
                if (this.moduleComponent.mod instanceof Gui) {
                    this.moduleComponent.mod.setBind(54);
                } else {
                    this.moduleComponent.mod.setBind(0);
                }
                if (Raven.currentProfile != null) {
                    ((ProfileModule) Raven.currentProfile.getModule()).saved = false;
                }
            } else {
                if (Raven.currentProfile != null) {
                    ((ProfileModule) Raven.currentProfile.getModule()).saved = false;
                }
                this.moduleComponent.mod.setBind(key);
            }

            this.isBinding = false;
        }
    }

    public int getHeight() {
        return 16;
    }

    private void drawString(String s) {
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(s, (float) ((this.moduleComponent.categoryComponent.getX() + 4) * 2), (float) ((this.moduleComponent.categoryComponent.getY() + this.bind + 3) * 2), !this.moduleComponent.mod.hidden ? Theme.getGradient(Theme.descriptor[0], Theme.descriptor[1], 0) : Theme.getGradient(Theme.hiddenBind[0], Theme.hiddenBind[1], 0));
    }

    public void onGuiClosed() {
        this.isBinding = false;
    }

    public boolean isOverBind(int x, int y) {
        return (x > this.x && x < this.x + moduleComponent.categoryComponent.getWidth()) && (y > this.y - 1 && y < this.y + 12);
    }
}
