package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.util.RotationUtils;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.MovingObjectPosition;

public class WaterBucket extends Module {
    private ButtonSetting silentAim;
    private ButtonSetting switchToItem;

    public WaterBucket() {
        super("Water bucket", Category.player, 0);
        this.registerSetting(silentAim = new ButtonSetting("Silent aim", true));
        this.registerSetting(switchToItem = new ButtonSetting("Switch to item", true));
    }

    @EventTarget
    public void onPreMotion(PreMotionEvent e) {
        MovingObjectPosition rayCast = RotationUtils.rayCast(mc.playerController.getBlockReachDistance(), e.getYaw(), 90);
        if (inPosition() && rayCast != null && rayCast.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && holdWaterBucket(switchToItem.isToggled())) {
            if (silentAim.isToggled()) {
                e.setPitch(90);
            }
            else {
                mc.thePlayer.rotationPitch = 90;
            }
            sendPlace();
        }
    }

    private boolean inPosition() {
        return !mc.thePlayer.capabilities.isFlying && !mc.thePlayer.capabilities.isCreativeMode && !mc.thePlayer.onGround && mc.thePlayer.motionY < -0.6D && !mc.thePlayer.isInWater() && fallDistance() <= 2;
    }

    private boolean holdWaterBucket(boolean setSlot) {
        if (this.containsItem(mc.thePlayer.getHeldItem(), Items.water_bucket)) {
            return true;
        } else {
            for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
                if (this.containsItem(mc.thePlayer.inventory.mainInventory[i], Items.water_bucket) && setSlot) {
                    mc.thePlayer.inventory.currentItem = i;
                    return true;
                }
            }

            return false;
        }
    }

    private boolean containsItem(ItemStack itemStack, Item item) {
        return itemStack != null && itemStack.getItem() == item;
    }

    private void sendPlace() {
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
    }

    private int fallDistance() {
        return (int) GeneralUtils.getFallDistance(mc.thePlayer);
    }
}
