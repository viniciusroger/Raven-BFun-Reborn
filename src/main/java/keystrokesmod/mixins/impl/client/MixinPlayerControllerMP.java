package keystrokesmod.mixins.impl.client;

import keystrokesmod.event.AttackEvent;
import net.lenni0451.asmevents.EventManager;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

	@Inject(method = "attackEntity", at = @At("HEAD"))
	public void onAttackEntity(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci) {
		EventManager.call(new AttackEvent(targetEntity, playerIn));
	}
}
