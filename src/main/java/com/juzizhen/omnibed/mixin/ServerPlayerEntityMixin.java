package com.juzizhen.omnibed.mixin;

import com.juzizhen.omnibed.util.OmniBedPlayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void saveSecondarySpawnPoint(NbtCompound nbt, CallbackInfo ci) {
        OmniBedPlayer player = (OmniBedPlayer) this;
        if (player.omnibed$getSecondarySpawn() != null) {
            BlockPos pos = player.omnibed$getSecondarySpawn();
            nbt.putInt("OmniBed_SpawnX", pos.getX());
            nbt.putInt("OmniBed_SpawnY", pos.getY());
            nbt.putInt("OmniBed_SpawnZ", pos.getZ());
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void loadSecondarySpawnPoint(NbtCompound nbt, CallbackInfo ci) {
        OmniBedPlayer player = (OmniBedPlayer) this;
        if (nbt.contains("OmniBed_SpawnX")) {
            player.omnibed$setSecondarySpawn(new BlockPos(
                    nbt.getInt("OmniBed_SpawnX"),
                    nbt.getInt("OmniBed_SpawnY"),
                    nbt.getInt("OmniBed_SpawnZ")
            ));
        }
    }
}