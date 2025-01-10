package keystrokesmod.module.impl.render;

import keystrokesmod.event.JoinWorldEvent;
import keystrokesmod.event.Render3DEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.BlockUtils;
import keystrokesmod.util.RenderUtils;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Xray extends Module {
    private SliderSetting range;
    private SliderSetting rate;
    private ButtonSetting iron;
    private ButtonSetting gold;
    private ButtonSetting diamond;
    private ButtonSetting emerald;
    private ButtonSetting lapis;
    private ButtonSetting redstone;
    private ButtonSetting coal;
    private ButtonSetting spawner;
    private ButtonSetting obsidian;
    private List<BlockPos> blocks = new ArrayList<>();
    private long lastCheck = 0;

    public Xray() {
        super("Xray", Category.render);
        this.registerSetting(range = new SliderSetting("Range", 20, 5, 50, 1));
        this.registerSetting(rate = new SliderSetting("Rate", 0.5, 0.1, 3.0, 0.1, " second"));
        this.registerSetting(coal = new ButtonSetting("Coal", true));
        this.registerSetting(diamond = new ButtonSetting("Diamond", true));
        this.registerSetting(emerald = new ButtonSetting("Emerald", true));
        this.registerSetting(gold = new ButtonSetting("Gold", true));
        this.registerSetting(iron = new ButtonSetting("Iron", true));
        this.registerSetting(lapis = new ButtonSetting("Lapis", true));
        this.registerSetting(obsidian = new ButtonSetting("Obsidian", true));
        this.registerSetting(redstone = new ButtonSetting("Redstone", true));
        this.registerSetting(spawner = new ButtonSetting("Spawner", true));
    }

    public void onDisable() {
        this.blocks.clear();
    }

    public void onUpdate() {
        if (System.currentTimeMillis() - lastCheck < rate.getInput() * 1000) {
            return;
        }
        lastCheck = System.currentTimeMillis();
        int i;
        for (int n = i = (int) range.getInput(); i >= -n; --i) {
            for (int j = -n; j <= n; ++j) {
                for (int k = -n; k <= n; ++k) {
                    BlockPos blockPos = new BlockPos(mc.thePlayer.posX + j, mc.thePlayer.posY + i, mc.thePlayer.posZ + k);
                    if (blocks.contains(blockPos)) {
                        continue;
                    }
                    Block blockState = BlockUtils.getBlock(blockPos);
                    if (blockState != null && canBreak(blockState)) {
                        blocks.add(blockPos);
                    }
                }
            }
        }
    }

    @EventTarget
    public void onEntityJoin(JoinWorldEvent e) {
        if (e.getEntity() == mc.thePlayer) {
            this.blocks.clear();
        }
    }

    @EventTarget
    public void onRenderWorld(Render3DEvent ev) {
        if (!GeneralUtils.nullCheck()) {
            return;
        }
        if (!this.blocks.isEmpty()) {
            for (BlockPos blockPos : blocks) {
                Block block = BlockUtils.getBlock(blockPos);

                if (block == null || !canBreak(block)) {
                    blocks.remove(blockPos);
                    continue;
                }

                this.drawBox(blockPos);
            }
        }
    }

    private void drawBox(BlockPos p) {
        if (p == null) {
            return;
        }
        int[] rgb = this.getColor(BlockUtils.getBlock(p));
        if (rgb[0] + rgb[1] + rgb[2] != 0) {
            RenderUtils.renderBlock(p, (new Color(rgb[0], rgb[1], rgb[2])).getRGB(), false, true);
        }
    }

    private int[] getColor(Block b) {
        int red = 0;
        int green = 0;
        int blue = 0;
        if (b.equals(Blocks.iron_ore)) {
            red = 255;
            green = 255;
            blue = 255;
        } else if (b.equals(Blocks.gold_ore)) {
            red = 255;
            green = 255;
        } else if (b.equals(Blocks.diamond_ore)) {
            green = 220;
            blue = 255;
        } else if (b.equals(Blocks.emerald_ore)) {
            red = 35;
            green = 255;
        } else if (b.equals(Blocks.lapis_ore)) {
            green = 50;
            blue = 255;
        } else if (b.equals(Blocks.redstone_ore)) {
            red = 255;
        } else if (b.equals(Blocks.mob_spawner)) {
            red = 30;
            blue = 135;
        }

        return new int[]{red, green, blue};
    }

    public boolean canBreak(Block block) {
        return (iron.isToggled() && block.equals(Blocks.iron_ore)) ||
                (gold.isToggled() && block.equals(Blocks.gold_ore)) ||
                (diamond.isToggled() && block.equals(Blocks.diamond_ore)) ||
                (emerald.isToggled() && block.equals(Blocks.emerald_ore)) ||
                (lapis.isToggled() && block.equals(Blocks.lapis_ore)) ||
                (redstone.isToggled() && block.equals(Blocks.redstone_ore)) ||
                (coal.isToggled() && block.equals(Blocks.coal_ore)) ||
                (spawner.isToggled() && block.equals(Blocks.mob_spawner)) ||
                (obsidian.isToggled() && block.equals(Blocks.obsidian));
    }
}
