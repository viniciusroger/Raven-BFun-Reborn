package keystrokesmod;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import keystrokesmod.event.TickEvent;
import keystrokesmod.keystroke.KeySrokeRenderer;
import keystrokesmod.keystroke.KeyStrokeConfigGui;
import keystrokesmod.keystroke.keystrokeCommand;
import keystrokesmod.module.Module;
import keystrokesmod.clickgui.ClickGui;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.utility.*;
import keystrokesmod.utility.profile.Profile;
import keystrokesmod.utility.profile.ProfileManager;
import net.lenni0451.asmevents.EventManager;
import net.lenni0451.asmevents.event.EventTarget;
import net.lenni0451.asmevents.event.enums.EnumEventType;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(
        modid = "keystrokes",
        name = "KeystrokesMod",
        version = "KMV5",
        acceptedMinecraftVersions = "[1.8.9]"
)
public class Raven {
    public static boolean debugger = false;
    public static Minecraft mc = Minecraft.getMinecraft();
    private static KeySrokeRenderer keySrokeRenderer;
    private static boolean isKeyStrokeConfigGuiToggled;
    private static final ScheduledExecutorService ex = Executors.newScheduledThreadPool(2);
    public static ModuleManager moduleManager;
    public static ClickGui clickGui;
    public static ProfileManager profileManager;
    public static Profile currentProfile;
    public static BadPacketsHandler badPacketsHandler;

    public Raven() {
        moduleManager = new ModuleManager();
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        Runtime.getRuntime().addShutdownHook(new Thread(ex::shutdown));
        ClientCommandHandler.instance.registerCommand(new keystrokeCommand());
        EventManager.register(this);
        EventManager.register(new DebugInfoRenderer());
        EventManager.register(new CPSCalculator());
        EventManager.register(new KeySrokeRenderer());
        EventManager.register(new Ping());
        EventManager.register(badPacketsHandler = new BadPacketsHandler());
        ReflectHelper.getFields();
        ReflectHelper.getMethods();
        moduleManager.register();
        keySrokeRenderer = new KeySrokeRenderer();
        clickGui = new ClickGui();
        profileManager = new ProfileManager();
        profileManager.loadProfiles();
        profileManager.loadProfile("default");
        EventManager.register(ModuleManager.tower);
    }

    @EventTarget
    public void onTick(TickEvent e) {
        if (e.getType() == EnumEventType.POST) {
            if (Utils.nullCheck()) {
                if (ReflectHelper.sendMessage) {
                    Utils.sendMessage("&cThere was an error, relaunch the game.");
                    ReflectHelper.sendMessage = false;
                }
                for (Module module : getModuleManager().getModules()) {
                    if (mc.currentScreen == null && module.canBeEnabled()) {
                        module.keybind();
                    } else if (mc.currentScreen instanceof ClickGui) {
                        module.guiUpdate();
                    }

                    if (module.isEnabled()) {
                        module.onUpdate();
                    }
                }
                for (Profile profile : Raven.profileManager.profiles) {
                    if (mc.currentScreen == null) {
                        profile.getModule().keybind();
                    }
                }
            }

            if (isKeyStrokeConfigGuiToggled) {
                isKeyStrokeConfigGuiToggled = false;
                mc.displayGuiScreen(new KeyStrokeConfigGui());
            }
        }
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static ScheduledExecutorService getExecutor() {
        return ex;
    }

    public static KeySrokeRenderer getKeyStrokeRenderer() {
        return keySrokeRenderer;
    }

    public static void toggleKeyStrokeConfigGui() {
        isKeyStrokeConfigGuiToggled = true;
    }
}
