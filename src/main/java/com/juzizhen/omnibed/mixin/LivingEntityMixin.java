package com.juzizhen.omnibed.mixin;

import com.juzizhen.omnibed.util.OmniBedPlayer;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "getSleepingDirection", at = @At("HEAD"), cancellable = true)
    private void overrideSleepingDirection(CallbackInfoReturnable<Direction> cir) {
        if ((Object) this instanceof PlayerEntity player) {
            OmniBedPlayer omniPlayer = (OmniBedPlayer) player;
            if (omniPlayer.omnibed$getSleepDirection() != null) {
                cir.setReturnValue(omniPlayer.omnibed$getSleepDirection());
            }
        }
    }

    @Inject(method = "wakeUp()V", at = @At("HEAD"), cancellable = true)
    private void customPlayerWakeUp(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof PlayerEntity player) {
            Optional<BlockPos> optional = self.getSleepingPosition();

            if (optional.isPresent()) {
                BlockPos primaryPos = optional.get();
                BlockState s1 = self.getWorld().getBlockState(primaryPos);
                if (s1.getBlock() instanceof BedBlock) {
                    self.getWorld().setBlockState(primaryPos, s1.with(BedBlock.OCCUPIED, false), 3);
                }

                OmniBedPlayer omniPlayer = (OmniBedPlayer) player;
                BlockPos secPos = omniPlayer.omnibed$getSecondaryBed();
                if (secPos != null) {
                    BlockState s2 = self.getWorld().getBlockState(secPos);
                    if (s2.getBlock() instanceof BedBlock) {
                        self.getWorld().setBlockState(secPos, s2.with(BedBlock.OCCUPIED, false), 3);
                    }
                    omniPlayer.omnibed$setSecondaryBed(null);
                }
                omniPlayer.omnibed$setSleepDirection(null);
            }

            self.setPose(EntityPose.STANDING);
            self.clearSleepingPosition();
            ci.cancel();
        }
    }
}