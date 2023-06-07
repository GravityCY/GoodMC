package me.gravityio.goodmc.mixin.mod.locator;

import me.gravityio.goodlib.helper.GoodNbtHelper;
import me.gravityio.goodmc.mixin.interfaces.ILocatorPlayer;
import me.gravityio.goodmc.tweaks.locator.LocatorPlayerData;
import me.gravityio.goodmc.tweaks.locator.LocatorTweak;
import me.gravityio.goodmc.tweaks.locator.impl.BiomeLocatable;
import me.gravityio.goodmc.tweaks.locator.impl.StructureLocatable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

import static me.gravityio.goodmc.tweaks.locator.LocatorPlayerData.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerLocatorMixin extends LivingEntity implements ILocatorPlayer {

    private final LocatorPlayerData playerData = new LocatorPlayerData();

    protected PlayerLocatorMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound strMap = GoodNbtHelper.fromMap(this.playerData.excludedStructures, Identifier::toString, ARRAYLIST_OF_IDS_TO_NBT_ELEMENT);
        NbtCompound biomeMap = GoodNbtHelper.fromMap(this.playerData.excludedBiomes, Identifier::toString, ARRAYLIST_OF_IDS_TO_NBT_ELEMENT);
        nbt.put(STRUCTURE_EXCLUSIONS_KEY, strMap);
        nbt.put(BIOME_EXCLUSIONS_KEY, biomeMap);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomFromNbt(NbtCompound nbt, CallbackInfo ci) {
        GoodNbtHelper.toMap(nbt.getCompound(STRUCTURE_EXCLUSIONS_KEY), this.playerData.excludedStructures, Identifier::new, NBT_LIST_OF_IDS_TO_ARRAYLIST);
        GoodNbtHelper.toMap(nbt.getCompound(BIOME_EXCLUSIONS_KEY), this.playerData.excludedBiomes, Identifier::new, NBT_LIST_OF_IDS_TO_ARRAYLIST);
        this.playerData.availableStructures = LocatorTweak.getAvailable(StructureLocatable.StructureRegistry.getDimensionStructures(), this.playerData.excludedStructures);
        this.playerData.availableBiomes = LocatorTweak.getAvailable(BiomeLocatable.BiomeRegistry.getDimensionBiomes(), this.playerData.excludedBiomes);
    }

    @Override
    public Map<Identifier, List<Identifier>> getAvailableStructures() {
        return this.playerData.availableStructures;
    }

    @Override
    public void setAvailableStructures(Map<Identifier, List<Identifier>> availableStructures) {
        this.playerData.availableStructures = availableStructures;
    }

    @Override
    public Map<Identifier, List<Identifier>> getAvailableBiomes() {
        return this.playerData.availableBiomes;
    }

    @Override
    public void setAvailableBiomes(Map<Identifier, List<Identifier>> availableBiomes) {
        this.playerData.availableBiomes = availableBiomes;
    }

    @Override
    public Map<Identifier, List<Identifier>> getExcludedStructures() {
        return this.playerData.excludedStructures;
    }

    @Override
    public void setExcludedStructures(Map<Identifier, List<Identifier>> excludedStructures) {
        this.playerData.excludedStructures = excludedStructures;
    }

    @Override
    public Map<Identifier, List<Identifier>> getExcludedBiomes() {
        return this.playerData.excludedBiomes;
    }

    @Override
    public void setExcludedBiomes(Map<Identifier, List<Identifier>> excludedBiomes) {
        this.playerData.excludedBiomes = excludedBiomes;
    }

}
