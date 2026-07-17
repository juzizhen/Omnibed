package com.juzizhen.omnibed.mixin;

import com.juzizhen.omnibed.util.OmniBedPlayer;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements OmniBedPlayer {
    @Unique private BlockPos secondaryBedPos = null;
    @Unique private BlockPos secondarySpawnPos = null;

    @Unique
    private static final TrackedData<Byte> OMNIBED_DIR = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BYTE);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void onInitDataTracker(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        self.getDataTracker().startTracking(OMNIBED_DIR, (byte) -1);
    }

    @Override public void omnibed$setSecondaryBed(BlockPos pos) { this.secondaryBedPos = pos; }
    @Override public BlockPos omnibed$getSecondaryBed() { return this.secondaryBedPos; }

    @Override public void omnibed$setSecondarySpawn(BlockPos pos) { this.secondarySpawnPos = pos; }
    @Override public BlockPos omnibed$getSecondarySpawn() { return this.secondarySpawnPos; }

    @Override
    public void omnibed$setSleepDirection(Direction direction) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        self.getDataTracker().set(OMNIBED_DIR, direction == null ? (byte) -1 : (byte) direction.getId());
    }

    @Override
    public Direction omnibed$getSleepDirection() {
        PlayerEntity self = (PlayerEntity) (Object) this;
        byte dirId = self.getDataTracker().get(OMNIBED_DIR);
        return dirId == -1 ? null : Direction.byId(dirId);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void checkSecondaryBedValid(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        if (!self.getWorld().isClient && self.isSleeping() && this.secondaryBedPos != null) {
            BlockState state = self.getWorld().getBlockState(this.secondaryBedPos);

            if (!(state.getBlock() instanceof BedBlock)) {
                self.wakeUp(true, true);
            }
        }
    }

    @Inject(method = "wakeUp(ZZ)V", at = @At("TAIL"))
    private void cleanOmniBedSecondaryState(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (!player.getWorld().isClient && this.secondaryBedPos != null) {
            BlockState s2 = player.getWorld().getBlockState(this.secondaryBedPos);
            if (s2.getBlock() instanceof BedBlock) {
                player.getWorld().setBlockState(this.secondaryBedPos, s2.with(BedBlock.OCCUPIED, false), 3);
            }
            this.secondaryBedPos = null;
            this.omnibed$setSleepDirection(null);
        }
    }
}