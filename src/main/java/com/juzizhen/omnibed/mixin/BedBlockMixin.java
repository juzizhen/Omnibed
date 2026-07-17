package com.juzizhen.omnibed.mixin;

import com.juzizhen.omnibed.util.OmniBedPlayer;
import com.mojang.datafixers.util.Either;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin {
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onOmniBedUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {

        // 玩家当前的初始视线方向
        Direction viewFacing = Direction.fromRotation(player.getYaw());

        Direction finalSleepDir = viewFacing;

        BlockPos footPos = null;
        BlockPos headPos = null;
        boolean foundAnyBed = false;

        // 定义搜索方向：0=前, 1=后, 2=左, 3=右
        Direction[] searchDirs = {
                viewFacing,                                 // 前
                viewFacing.getOpposite(),                   // 后
                viewFacing.rotateYCounterclockwise(),       // 左
                viewFacing.rotateYClockwise()               // 右
        };

        for (int i = 0; i < searchDirs.length; i++) {
            Direction searchDir = searchDirs[i];
            BlockPos targetPos = pos.offset(searchDir);
            BlockState targetState = world.getBlockState(targetPos);

            if (targetState.getBlock() instanceof BedBlock) {
                foundAnyBed = true;

                if (!targetState.get(BedBlock.OCCUPIED)) {
                    if (i == 1) {
                        footPos = targetPos;
                        headPos = pos;
                    } else if (i == 2 || i == 3) {
                        footPos = pos;
                        headPos = targetPos;
                        finalSleepDir = searchDir;
                    } else {
                        footPos = pos;
                        headPos = targetPos;
                    }
                    break;
                }
            }
        }

        if (!foundAnyBed) {
            if (!world.isClient) player.sendMessage(Text.translatable("omnibed.bed.not_enough"), true);
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }

        if (state.get(BedBlock.OCCUPIED) || headPos == null) {
            if (!world.isClient) player.sendMessage(Text.translatable("block.minecraft.bed.occupied"), true);
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }

        BlockState footState = world.getBlockState(footPos);
        BlockState headState = world.getBlockState(headPos);

        OmniBedPlayer omniPlayer = (OmniBedPlayer) player;

        omniPlayer.omnibed$setSleepDirection(finalSleepDir);

        if (world.isClient) {
            cir.setReturnValue(ActionResult.CONSUME);
            return;
        }

        Either<PlayerEntity.SleepFailureReason, Unit> result = player.trySleep(headPos);

        result.ifLeft(reason -> {
            if (reason.getMessage() != null) player.sendMessage(reason.getMessage(), true);
            omniPlayer.omnibed$setSleepDirection(null);
        });

        BlockPos finalHeadPos = headPos;
        BlockPos finalFootPos = footPos;

        result.ifRight(unit -> {
            world.setBlockState(finalFootPos, footState.with(BedBlock.OCCUPIED, true), 3);
            world.setBlockState(finalHeadPos, headState.with(BedBlock.OCCUPIED, true), 3);
            omniPlayer.omnibed$setSecondaryBed(finalFootPos);
            omniPlayer.omnibed$setSecondarySpawn(finalFootPos);
        });

        cir.setReturnValue(ActionResult.SUCCESS);
    }
}