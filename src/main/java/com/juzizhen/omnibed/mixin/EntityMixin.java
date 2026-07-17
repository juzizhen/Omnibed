package com.juzizhen.omnibed.mixin;

import com.juzizhen.omnibed.util.OmniBedPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    // 拦截原版读取方块 Facing 的行为
    @Inject(method = "getMovementDirection", at = @At("HEAD"), cancellable = true)
    private void overrideBedFacing(CallbackInfoReturnable<Direction> cir) {
        if ((Object) this instanceof PlayerEntity player) {
            OmniBedPlayer omniPlayer = (OmniBedPlayer) player;
            if (omniPlayer.omnibed$getSleepDirection() != null) {
                cir.setReturnValue(omniPlayer.omnibed$getSleepDirection());
            }
        }
    }
}