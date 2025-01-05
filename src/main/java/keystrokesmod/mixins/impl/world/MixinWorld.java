package keystrokesmod.mixins.impl.world;

import keystrokesmod.event.JoinWorldEvent;
import net.lenni0451.asmevents.EventManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {

	@Inject(method = "spawnEntityInWorld", at = @At("HEAD"))
	public void onSpawnEntity(Entity entityIn, CallbackInfoReturnable<Boolean> cir) {
		EventManager.call(new JoinWorldEvent((World) (Object) this, entityIn));
	}
}
