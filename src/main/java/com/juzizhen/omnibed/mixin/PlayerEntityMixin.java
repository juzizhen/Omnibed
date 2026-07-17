package com.juzizhen.omnibed.mixin;

import com.juzizhen.omnibed.util.OmniBedPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements OmniBedPlayer {
    @Unique private BlockPos secondaryBedPos = null;
    @Unique private Direction customSleepDir = null;
    @Unique private BlockPos secondarySpawnPos = null;

    @Override public void omnibed$setSecondaryBed(BlockPos pos) { this.secondaryBedPos = pos; }
    @Override public BlockPos omnibed$getSecondaryBed() { return this.secondaryBedPos; }

    @Override public void omnibed$setSleepDirection(Direction direction) { this.customSleepDir = direction; }
    @Override public Direction omnibed$getSleepDirection() { return this.customSleepDir; }

    @Override public void omnibed$setSecondarySpawn(BlockPos pos) { this.secondarySpawnPos = pos; }
    @Override public BlockPos omnibed$getSecondarySpawn() { return this.secondarySpawnPos; }
}