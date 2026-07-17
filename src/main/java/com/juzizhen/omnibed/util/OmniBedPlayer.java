package com.juzizhen.omnibed.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface OmniBedPlayer {
    void omnibed$setSecondaryBed(BlockPos pos);
    BlockPos omnibed$getSecondaryBed();

    void omnibed$setSleepDirection(Direction direction);
    Direction omnibed$getSleepDirection();

    void omnibed$setSecondarySpawn(BlockPos pos);
    BlockPos omnibed$getSecondarySpawn();
}