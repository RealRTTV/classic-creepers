package ca.rttv.classic.creepers.mixin;

import ca.rttv.classic.creepers.CreeperAttackGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CreeperEntity.class, priority = 990)
abstract class CreeperEntityMixin extends HostileEntity {
	protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow protected abstract void explode();

	@Override
	protected void updatePostDeath() {
		super.updatePostDeath();
		if (this.deathTime == 20) {
			this.explode();
		}
	}

	@Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V"))
	private void add(GoalSelector instance, int priority, Goal goal) {
		// it's just that easy
	}

	@Inject(method = "initGoals", at = @At("TAIL"))
	public void initGoals(CallbackInfo ci) {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new CreeperAttackGoal((CreeperEntity) (Object) this, 1.0D, false));
		this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0D));
		this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(4, new LookAroundGoal(this));
		this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}

	@Inject(method = "tryAttack", at = @At("HEAD"), cancellable = true)
	private void tryAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
		boolean bl = super.tryAttack(target);
		if (bl) {
			float f = this.world.getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
			if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
				target.setOnFireFor(2 * (int)f);
			}
		}

		if (!bl) cir.setReturnValue(false);
	}
}
