package keystrokesmod.ui.clickgui.components.impl;

import keystrokesmod.ui.clickgui.components.Component;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.enums.Theme;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class DescriptionComponent extends Component {
    private DescriptionSetting desc;
    private ModuleComponent p;
    private int o;
    private int x;
    private int y;

    public DescriptionComponent(DescriptionSetting desc, ModuleComponent b, int o) {
        this.desc = desc;
        this.p = b;
        this.x = b.categoryComponent.getX() + b.categoryComponent.getWidth();
        this.y = b.categoryComponent.getY() + b.o;
        this.o = o;
    }

    public void render() {
        GL11.glPushMatrix();
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        Minecraft.getMinecraft().fontRendererObj.drawString(this.desc.getDesc(), (float) ((this.p.categoryComponent.getX() + 4) * 2), (float) ((this.p.categoryComponent.getY() + this.o + 4) * 2), Theme.getGradient(Theme.descriptor[0], Theme.descriptor[1], 0), true);
        GL11.glPopMatrix();
    }

    public void setOffset(int n) {
        this.o = n;
    }
}
