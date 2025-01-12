package keystrokesmod.module.impl.render;

import keystrokesmod.event.Render3DEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.RenderUtils;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

import java.awt.*;

public class ChestESP extends Module {
    private SliderSetting red, green, blue;
    private ButtonSetting rainbow, outline, shade, disableIfOpened;

    public ChestESP() {
        super("ChestESP", Category.render, 0);
        this.registerSetting(red = new SliderSetting("Red", 0.0D, 0.0D, 255.0D, 1.0D));
        this.registerSetting(green = new SliderSetting("Green", 0.0D, 0.0D, 255.0D, 1.0D));
        this.registerSetting(blue = new SliderSetting("Blue", 255.0D, 0.0D, 255.0D, 1.0D));
        this.registerSetting(rainbow = new ButtonSetting("Rainbow", false));
        this.registerSetting(outline = new ButtonSetting("Outline", false));
        this.registerSetting(shade = new ButtonSetting("Shade", false));
        this.registerSetting(disableIfOpened = new ButtonSetting("Disable if opened", false));
    }

    @EventTarget
    public void o(Render3DEvent ev) {
        if (!GeneralUtils.nullCheck()) {
            return;
        }
        int rgb = rainbow.isToggled() ? GeneralUtils.getChroma(2L, 0L) : (new Color((int) red.getInput(), (int) green.getInput(), (int) blue.getInput())).getRGB();
        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if (tileEntity instanceof TileEntityChest) {
                if (disableIfOpened.isToggled() && ((TileEntityChest) tileEntity).lidAngle > 0.0f) {
                    continue;
                }
                RenderUtils.renderBlock(tileEntity.getPos(), rgb, outline.isToggled(), shade.isToggled());
            } else {
                if (!(tileEntity instanceof TileEntityEnderChest)) {
                    continue;
                }
                if (disableIfOpened.isToggled() && ((TileEntityEnderChest) tileEntity).lidAngle > 0.0f) {
                    continue;
                }
                RenderUtils.renderBlock(tileEntity.getPos(), rgb, outline.isToggled(), shade.isToggled());
            }
        }
    }
}
