package keystrokesmod.mixin.impl.render;

import keystrokesmod.event.Render2DEvent;
import net.lenni0451.asmevents.EventManager;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiInGame {

	@Inject(method = "renderTooltip", at = @At("HEAD"))
	public void onRenderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo ci) {
		EventManager.call(new Render2DEvent(partialTicks));
	}
}
