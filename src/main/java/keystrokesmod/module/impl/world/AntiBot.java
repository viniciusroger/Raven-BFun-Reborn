package keystrokesmod.module.impl.world;

import keystrokesmod.event.JoinWorldEvent;
import keystrokesmod.module.Module;
import keystrokesmod.manager.ModuleManager;
import keystrokesmod.module.impl.player.Freecam;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AntiBot extends Module {
    private static final HashMap<EntityPlayer, Long> entities = new HashMap<>();
    private static ButtonSetting entitySpawnDelay;
    private SliderSetting delay;
    private static ButtonSetting tablist;

    public AntiBot() {
        super("AntiBot", Category.world, 0);
        this.registerSetting(entitySpawnDelay = new ButtonSetting("Entity spawn delay", false));
        this.registerSetting(delay = new SliderSetting("Delay", 7.0, 0.5, 15.0, 0.5, " second"));
        this.registerSetting(tablist = new ButtonSetting("Tab list", false));
    }

    @EventTarget
    public void c(final JoinWorldEvent entityJoinWorldEvent) {
        if (entitySpawnDelay.isToggled() && entityJoinWorldEvent.getEntity() instanceof EntityPlayer && entityJoinWorldEvent.getEntity() != mc.thePlayer) {
            entities.put((EntityPlayer) entityJoinWorldEvent.getEntity(), System.currentTimeMillis());
        }
    }

    public void onUpdate() {
        if (entitySpawnDelay.isToggled() && !entities.isEmpty()) {
            entities.values().removeIf(n -> n < System.currentTimeMillis() - delay.getInput());
        }
    }

    public void onDisable() {
        entities.clear();
    }

    public static boolean isBot(Entity entity) {
        if (!ModuleManager.antiBot.isEnabled()) {
            return false;
        }
        if (Freecam.freeEntity != null && Freecam.freeEntity == entity) {
            return true;
        }
        if (!(entity instanceof EntityPlayer)) {
            return true;
        }
        final EntityPlayer entityPlayer = (EntityPlayer) entity;
        if (entitySpawnDelay.isToggled() && !entities.isEmpty() && entities.containsKey(entityPlayer)) {
            return true;
        }
        if (entityPlayer.isDead) {
            return true;
        }
        if (entityPlayer.getName().isEmpty()) {
            return true;
        }
        if (tablist.isToggled() && !getTablist().contains(entityPlayer.getName())) {
            return true;
        }
        if (entityPlayer.getHealth() != 20.0f && entityPlayer.getName().startsWith("§c")) {
            return true;
        }

        if (entityPlayer.maxHurtTime == 0) {
            if (entityPlayer.getHealth() == 20.0f) {
                String unformattedText = entityPlayer.getDisplayName().getUnformattedText();
                if (unformattedText.length() == 10 && unformattedText.charAt(0) != '§') {
                    return true;
                }
                if (unformattedText.length() == 12 && entityPlayer.isPlayerSleeping() && unformattedText.charAt(0) == '§') {
                    return true;
                }
                if (unformattedText.length() >= 7 && unformattedText.charAt(2) == '[' && unformattedText.charAt(3) == 'N' && unformattedText.charAt(6) == ']') {
                    return true;
                }

				return entityPlayer.getName().contains(" ");
            } else if (entityPlayer.isInvisible()) {
                String unformattedText = entityPlayer.getDisplayName().getUnformattedText();

                return unformattedText.length() >= 3 && unformattedText.charAt(0) == '§' && unformattedText.charAt(1) == 'c';
            }
        }
        return false;
    }

    private static List<String> getTablist() {
        List<String> tab = new ArrayList<>();
        for (NetworkPlayerInfo networkPlayerInfo : GeneralUtils.getTablist()) {
            if (networkPlayerInfo == null) {
                continue;
            }
            tab.add(networkPlayerInfo.getGameProfile().getName());
        }
        return tab;
    }
}
