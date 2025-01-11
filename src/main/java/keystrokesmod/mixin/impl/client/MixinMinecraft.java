package keystrokesmod.mixin.impl.client;

import keystrokesmod.event.MouseEvent;
import keystrokesmod.event.TickEvent;
import keystrokesmod.manager.ModuleManager;
import net.lenni0451.asmevents.EventManager;
import net.lenni0451.asmevents.event.enums.EnumEventType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.world.EnumDifficulty;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.concurrent.Callable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

	@Shadow
	private int rightClickDelayTimer;

	@Final
	@Shadow
	public Profiler mcProfiler;

	@Shadow
	private boolean isGamePaused;


	@Shadow public GuiIngame ingameGUI;

	@Shadow public EntityRenderer entityRenderer;

	@Shadow public WorldClient theWorld;

	@Shadow public PlayerControllerMP playerController;

	@Shadow public TextureManager renderEngine;

	@Shadow public EntityPlayerSP thePlayer;

	@Shadow public GuiScreen currentScreen;

	@Shadow public abstract void displayGuiScreen(GuiScreen guiScreenIn);

	@Shadow private int leftClickCounter;

	@Shadow
	long systemTime;

	@Shadow public boolean inGameHasFocus;

	@Shadow public abstract void setIngameFocus();

	@Shadow private long debugCrashKeyPressTime;

	@Shadow public abstract void dispatchKeypresses();

	@Shadow public abstract void displayInGameMenu();

	@Shadow public abstract void refreshResources();

	@Shadow public GameSettings gameSettings;

	@Shadow public RenderGlobal renderGlobal;

	@Shadow private RenderManager renderManager;

	@Shadow public abstract Entity getRenderViewEntity();

	@Shadow protected abstract void updateDebugProfilerName(int keyCount);

	@Shadow protected abstract void clickMouse();

	@Shadow protected abstract void rightClickMouse();

	@Shadow protected abstract void middleClickMouse();

	@Shadow protected abstract void sendClickBlockToController(boolean leftClick);

	@Shadow private int joinPlayerCounter;

	@Shadow private MusicTicker mcMusicTicker;

	@Shadow private SoundHandler mcSoundHandler;

	@Shadow public abstract NetHandlerPlayClient getNetHandler();

	@Shadow public EffectRenderer effectRenderer;

	@Shadow private NetworkManager myNetworkManager;

	@Shadow public MovingObjectPosition objectMouseOver;

	@Shadow @Final private static Logger logger;

	@Inject(method = "clickMouse", at = @At("HEAD"), cancellable = true)
	public void onClickMouse(CallbackInfo ci) {
		if (ModuleManager.noHitDelay.isEnabled()) {
			switch ((int) ModuleManager.noHitDelay.mode.getInput()) {
				case 0:
					this.leftClickCounter = 0;

					this.thePlayer.swingItem();

					if (this.objectMouseOver == null)
					{
						logger.error("Null returned as \'hitResult\', this shouldn\'t happen!");
					}
					else
					{
						switch (this.objectMouseOver.typeOfHit)
						{
							case ENTITY:
								this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
								break;
							case BLOCK:
								BlockPos blockpos = this.objectMouseOver.getBlockPos();

								if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air)
								{
									this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
									break;
								}
						}
					}

					ci.cancel();
					break;
				case 1:
					this.leftClickCounter = 0;
			}
		}
	}

	@Inject(method = "clickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;isNotCreative()Z", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
	public void onMissClick(CallbackInfo ci) {
		if (ModuleManager.noMissClick.isEnabled())
			ci.cancel();
	}

	/**
	 * @author a
	 * @reason a
	 */
	@Overwrite
	public void runTick() throws IOException {
		EventManager.call(new TickEvent(EnumEventType.PRE));

		if (this.rightClickDelayTimer > 0)
		{
			--this.rightClickDelayTimer;
		}

		net.minecraftforge.fml.common.FMLCommonHandler.instance().onPreClientTick();

		this.mcProfiler.startSection("gui");

		if (!this.isGamePaused)
		{
			this.ingameGUI.updateTick();
		}

		this.mcProfiler.endSection();
		this.entityRenderer.getMouseOver(1.0F);
		this.mcProfiler.startSection("gameMode");

		if (!this.isGamePaused && this.theWorld != null)
		{
			this.playerController.updateController();
		}

		this.mcProfiler.endStartSection("textures");

		if (!this.isGamePaused)
		{
			this.renderEngine.tick();
		}

		if (this.currentScreen == null && this.thePlayer != null)
		{
			if (this.thePlayer.getHealth() <= 0.0F)
			{
				this.displayGuiScreen(null);
			}
			else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null)
			{
				this.displayGuiScreen(new GuiSleepMP());
			}
		}
		else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping())
		{
			this.displayGuiScreen(null);
		}

		if (this.currentScreen != null)
		{
			this.leftClickCounter = 10000;
		}

		if (this.currentScreen != null)
		{
			try
			{
				this.currentScreen.handleInput();
			}
			catch (Throwable throwable1)
			{
				CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
				crashreportcategory.addCrashSectionCallable("Screen name", new Callable<String>()
				{
					public String call() {
						return currentScreen.getClass().getCanonicalName();
					}
				});
				throw new ReportedException(crashreport);
			}

			if (this.currentScreen != null)
			{
				try
				{
					this.currentScreen.updateScreen();
				}
				catch (Throwable throwable)
				{
					CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Ticking screen");
					CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Affected screen");
					crashreportcategory1.addCrashSectionCallable("Screen name", new Callable<String>()
					{
						public String call() throws Exception
						{
							return currentScreen.getClass().getCanonicalName();
						}
					});
					throw new ReportedException(crashreport1);
				}
			}
		}

		if (this.currentScreen == null || this.currentScreen.allowUserInput)
		{
			this.mcProfiler.endStartSection("mouse");

			while (Mouse.next())
			{
				if (EventManager.call(new MouseEvent()).isCancelled()) continue;

				if (net.minecraftforge.client.ForgeHooksClient.postMouseEvent()) continue;

				int i = Mouse.getEventButton();
				KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());

				if (Mouse.getEventButtonState())
				{
					if (this.thePlayer.isSpectator() && i == 2)
					{
						this.ingameGUI.getSpectatorGui().func_175261_b();
					}
					else
					{
						KeyBinding.onTick(i - 100);
					}
				}

				long i1 = Minecraft.getSystemTime() - this.systemTime;

				if (i1 <= 200L)
				{
					int j = Mouse.getEventDWheel();

					if (j != 0)
					{
						if (this.thePlayer.isSpectator())
						{
							j = j < 0 ? -1 : 1;

							if (this.ingameGUI.getSpectatorGui().func_175262_a())
							{
								this.ingameGUI.getSpectatorGui().func_175259_b(-j);
							}
							else
							{
								float f = MathHelper.clamp_float(this.thePlayer.capabilities.getFlySpeed() + (float)j * 0.005F, 0.0F, 0.2F);
								this.thePlayer.capabilities.setFlySpeed(f);
							}
						}
						else
						{
							this.thePlayer.inventory.changeCurrentItem(j);
						}
					}

					if (this.currentScreen == null)
					{
						if (!this.inGameHasFocus && Mouse.getEventButtonState())
						{
							this.setIngameFocus();
						}
					}
					else {
						this.currentScreen.handleMouseInput();
					}
				}
				net.minecraftforge.fml.common.FMLCommonHandler.instance().fireMouseInput();
			}

			if (this.leftClickCounter > 0)
			{
				--this.leftClickCounter;
			}

			this.mcProfiler.endStartSection("keyboard");

			while (Keyboard.next())
			{
				int k = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
				KeyBinding.setKeyBindState(k, Keyboard.getEventKeyState());

				if (Keyboard.getEventKeyState())
				{
					KeyBinding.onTick(k);
				}

				if (this.debugCrashKeyPressTime > 0L)
				{
					if (Minecraft.getSystemTime() - this.debugCrashKeyPressTime >= 6000L)
					{
						throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
					}

					if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61))
					{
						this.debugCrashKeyPressTime = -1L;
					}
				}
				else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61))
				{
					this.debugCrashKeyPressTime = Minecraft.getSystemTime();
				}

				this.dispatchKeypresses();

				if (Keyboard.getEventKeyState())
				{
					if (k == 62 && this.entityRenderer != null)
					{
						this.entityRenderer.switchUseShader();
					}

					if (this.currentScreen != null)
					{
						this.currentScreen.handleKeyboardInput();
					}
					else
					{
						if (k == 1)
						{
							this.displayInGameMenu();
						}

						if (k == 32 && Keyboard.isKeyDown(61) && this.ingameGUI != null)
						{
							this.ingameGUI.getChatGUI().clearChatMessages();
						}

						if (k == 31 && Keyboard.isKeyDown(61))
						{
							this.refreshResources();
						}

						if (k == 17 && Keyboard.isKeyDown(61))
						{
							;
						}

						if (k == 18 && Keyboard.isKeyDown(61))
						{
							;
						}

						if (k == 47 && Keyboard.isKeyDown(61))
						{
							;
						}

						if (k == 38 && Keyboard.isKeyDown(61))
						{
							;
						}

						if (k == 22 && Keyboard.isKeyDown(61))
						{
							;
						}

						if (k == 20 && Keyboard.isKeyDown(61))
						{
							this.refreshResources();
						}

						if (k == 33 && Keyboard.isKeyDown(61))
						{
							this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
						}

						if (k == 30 && Keyboard.isKeyDown(61))
						{
							this.renderGlobal.loadRenderers();
						}

						if (k == 35 && Keyboard.isKeyDown(61))
						{
							this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
							this.gameSettings.saveOptions();
						}

						if (k == 48 && Keyboard.isKeyDown(61))
						{
							this.renderManager.setDebugBoundingBox(!this.renderManager.isDebugBoundingBox());
						}

						if (k == 25 && Keyboard.isKeyDown(61))
						{
							this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
							this.gameSettings.saveOptions();
						}

						if (k == 59)
						{
							this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
						}

						if (k == 61)
						{
							this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
							this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
							this.gameSettings.showLagometer = GuiScreen.isAltKeyDown();
						}

						if (this.gameSettings.keyBindTogglePerspective.isPressed())
						{
							++this.gameSettings.thirdPersonView;

							if (this.gameSettings.thirdPersonView > 2)
							{
								this.gameSettings.thirdPersonView = 0;
							}

							if (this.gameSettings.thirdPersonView == 0)
							{
								this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
							}
							else if (this.gameSettings.thirdPersonView == 1)
							{
								this.entityRenderer.loadEntityShader(null);
							}

							this.renderGlobal.setDisplayListEntitiesDirty();
						}

						if (this.gameSettings.keyBindSmoothCamera.isPressed())
						{
							this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
						}
					}

					if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart)
					{
						if (k == 11)
						{
							this.updateDebugProfilerName(0);
						}

						for (int j1 = 0; j1 < 9; ++j1)
						{
							if (k == 2 + j1)
							{
								this.updateDebugProfilerName(j1 + 1);
							}
						}
					}
				}
				net.minecraftforge.fml.common.FMLCommonHandler.instance().fireKeyInput();
			}

			for (int l = 0; l < 9; ++l)
			{
				if (this.gameSettings.keyBindsHotbar[l].isPressed())
				{
					if (this.thePlayer.isSpectator())
					{
						this.ingameGUI.getSpectatorGui().func_175260_a(l);
					}
					else
					{
						this.thePlayer.inventory.currentItem = l;
					}
				}
			}

			boolean flag = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

			while (this.gameSettings.keyBindInventory.isPressed())
			{
				if (this.playerController.isRidingHorse())
				{
					this.thePlayer.sendHorseInventory();
				}
				else
				{
					this.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
					this.displayGuiScreen(new GuiInventory(this.thePlayer));
				}
			}

			while (this.gameSettings.keyBindDrop.isPressed())
			{
				if (!this.thePlayer.isSpectator())
				{
					this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
				}
			}

			while (this.gameSettings.keyBindChat.isPressed() && flag)
			{
				this.displayGuiScreen(new GuiChat());
			}

			if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && flag)
			{
				this.displayGuiScreen(new GuiChat("/"));
			}

			if (this.thePlayer.isUsingItem())
			{
				if (!this.gameSettings.keyBindUseItem.isKeyDown())
				{
					this.playerController.onStoppedUsingItem(this.thePlayer);
				}

				while (this.gameSettings.keyBindAttack.isPressed())
				{
					;
				}

				while (this.gameSettings.keyBindUseItem.isPressed())
				{
					;
				}

				while (this.gameSettings.keyBindPickBlock.isPressed())
				{
					;
				}
			}
			else
			{
				while (this.gameSettings.keyBindAttack.isPressed())
				{
					this.clickMouse();
				}

				while (this.gameSettings.keyBindUseItem.isPressed())
				{
					this.rightClickMouse();
				}

				while (this.gameSettings.keyBindPickBlock.isPressed())
				{
					this.middleClickMouse();
				}
			}

			if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem())
			{
				this.rightClickMouse();
			}

			this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus);
		}

		if (this.theWorld != null)
		{
			if (this.thePlayer != null)
			{
				++this.joinPlayerCounter;

				if (this.joinPlayerCounter == 30)
				{
					this.joinPlayerCounter = 0;
					this.theWorld.joinEntityInSurroundings(this.thePlayer);
				}
			}

			this.mcProfiler.endStartSection("gameRenderer");

			if (!this.isGamePaused)
			{
				this.entityRenderer.updateRenderer();
			}

			this.mcProfiler.endStartSection("levelRenderer");

			if (!this.isGamePaused)
			{
				this.renderGlobal.updateClouds();
			}

			this.mcProfiler.endStartSection("level");

			if (!this.isGamePaused)
			{
				if (this.theWorld.getLastLightningBolt() > 0)
				{
					this.theWorld.setLastLightningBolt(this.theWorld.getLastLightningBolt() - 1);
				}

				this.theWorld.updateEntities();
			}
		}
		else if (this.entityRenderer.isShaderActive())
		{
			this.entityRenderer.stopUseShader();
		}

		if (!this.isGamePaused)
		{
			this.mcMusicTicker.update();
			this.mcSoundHandler.update();
		}

		if (this.theWorld != null)
		{
			if (!this.isGamePaused)
			{
				this.theWorld.setAllowedSpawnTypes(this.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL, true);

				try
				{
					this.theWorld.tick();
				}
				catch (Throwable throwable2)
				{
					CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception in world tick");

					if (this.theWorld == null)
					{
						CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Affected level");
						crashreportcategory2.addCrashSection("Problem", "Level is null!");
					}
					else
					{
						this.theWorld.addWorldInfoToCrashReport(crashreport2);
					}

					throw new ReportedException(crashreport2);
				}
			}

			this.mcProfiler.endStartSection("animateTick");

			if (!this.isGamePaused && this.theWorld != null)
			{
				this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
			}

			this.mcProfiler.endStartSection("particles");

			if (!this.isGamePaused)
			{
				this.effectRenderer.updateEffects();
			}
		}
		else if (this.myNetworkManager != null)
		{
			this.mcProfiler.endStartSection("pendingConnection");
			this.myNetworkManager.processReceivedPackets();
		}

		EventManager.call(new TickEvent(EnumEventType.POST));
		net.minecraftforge.fml.common.FMLCommonHandler.instance().onPostClientTick();

		this.mcProfiler.endSection();
		this.systemTime = Minecraft.getSystemTime();
	}
}
