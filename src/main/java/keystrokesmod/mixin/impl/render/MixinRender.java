package keystrokesmod.mixin.impl.render;

import keystrokesmod.event.RenderNametag;
import net.lenni0451.asmevents.EventManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Render.class)
public abstract class MixinRender<T extends Entity> {

	@Inject(method = "renderLivingLabel", at = @At("HEAD"), cancellable = true)
	public void onRenderName(T entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
		RenderNametag event = new RenderNametag(entityIn);
		EventManager.call(event);

		if (event.isCancelled())
			ci.cancel();
	}
}
